/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package playground.ivt.maxess.nestedlogitaccessibility.scripts.capetown;

import com.google.inject.TypeLiteral;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;
import org.matsim.core.router.TripRouterModule;
import org.matsim.core.scenario.ScenarioByInstanceModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.algorithms.WorldConnectLocations;
import org.matsim.population.algorithms.XY2Links;
import playground.ivt.maxess.nestedlogitaccessibility.framework.AccessibilityComputationResult;
import playground.ivt.maxess.nestedlogitaccessibility.framework.BaseNestedAccessibilityComputationModule;
import playground.ivt.maxess.nestedlogitaccessibility.framework.InjectionUtils;
import playground.ivt.maxess.nestedlogitaccessibility.framework.NestedLogitAccessibilityCalculator;
import playground.ivt.maxess.nestedlogitaccessibility.scripts.AdvantageColumnCalculator;
import playground.ivt.maxess.nestedlogitaccessibility.scripts.ModeNests;
import playground.ivt.maxess.nestedlogitaccessibility.scripts.NestedAccessibilityConfigGroup;
import playground.ivt.maxess.nestedlogitaccessibility.writers.BasicPersonAccessibilityWriter;
import playground.ivt.router.CachingFreespeedCarRouterModule;
import playground.ivt.router.lazyschedulebasedmatrix.LazyScheduleBasedMatrixModule;
import playground.ivt.utils.MoreIOUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author thibautd
 */
public class RunCapeTownAccessibility {
	public static void main( final String... args ) {
		final String configFile = args[ 0 ];
		final String outputDir = args[ 1 ];

		final CapeTownNestedLogitModule modelModule = new CapeTownNestedLogitModule();

		// Logger.getLogger( Trip.class ).setLevel( Level.TRACE );
		MoreIOUtils.initOut( outputDir );

		try {
			final Config config = ConfigUtils.loadConfig(
					configFile,
					new CapeTownNestedLogitModelConfigGroup(),
					new NestedAccessibilityConfigGroup() );
			final Scenario scenario = ScenarioUtils.loadScenario( config );

			// Todo: put in a scenario provider
			final TransportModeNetworkFilter filter = new TransportModeNetworkFilter(scenario.getNetwork());
			final Network carNetwork = NetworkUtils.createNetwork();
			filter.filter( carNetwork, Collections.singleton( "car" ) );
			new WorldConnectLocations( config ).connectFacilitiesWithLinks(
					scenario.getActivityFacilities(),
					(NetworkImpl) carNetwork );

			new XY2Links( carNetwork , scenario.getActivityFacilities() ).run( scenario.getPopulation() );

			final NestedLogitAccessibilityCalculator<ModeNests> calculator =
					InjectionUtils.createCalculator(
							config,
							new TypeLiteral<ModeNests>() {
							},
							InjectionUtils.override(
									new BaseNestedAccessibilityComputationModule<ModeNests>(
											scenario ) {},
									Arrays.asList(
											new LazyScheduleBasedMatrixModule(),
											new CachingFreespeedCarRouterModule() ) ),
							modelModule );

			// TODO store and write results
			final AccessibilityComputationResult accessibilities = calculator.computeAccessibilities ();
			modelModule.stopWatch.printStats( TimeUnit.SECONDS );
			new BasicPersonAccessibilityWriter(
					scenario,
					accessibilities,
					new AdvantageColumnCalculator(
							"car_advantage",
							"all",
							"nocar" ),
					new AdvantageColumnCalculator(
							"pt_advantage",
							"all",
							"nopt" ) ).write( outputDir + "/accessibility_per_person.xy" );
		}
		finally {
			MoreIOUtils.closeOutputDirLogging();
		}
	}
}