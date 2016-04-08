/*
 * *********************************************************************** *
 * project: org.matsim.*                                                   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package playground.boescpa.ivtBaseline.preparation.crossborderCreation;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.LegImpl;
import org.matsim.core.population.PlanImpl;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.misc.Counter;
import org.matsim.facilities.ActivityFacility;
import playground.boescpa.ivtBaseline.preparation.PrefsCreator;
import playground.boescpa.ivtBaseline.preparation.secondaryFacilityCreation.CreationOfCrossBorderFacilities;

import java.io.BufferedReader;
import java.io.IOException;

import static playground.boescpa.ivtBaseline.preparation.IVTConfigCreator.*;

/**
 * Implementation of the trunk class CreateCBsubpop for the creation of secondary activity single-trip cb-agents.
 *
 * @author boescpa
 */
public class CreateCBSecondaryActivities extends CreateCBsubpop {

	private String actTag = null;

	private CreateCBSecondaryActivities(String pathToFacilities, String pathToCumulativeDepartureProbabilities, double samplePercentage, long randomSeed) {
		super(pathToFacilities, pathToCumulativeDepartureProbabilities, samplePercentage, randomSeed);
	}

	public static void main(final String[] args) {
		final String pathToFacilities = args[0]; // all scenario facilities incl secondary facilities and bc facilities.
		final String pathToCumulativeDepartureProbabilities = args[1];
		final double samplePercentage = Double.parseDouble(args[2]);
		final long randomSeed = Long.parseLong(args[3]);
		final String pathToCB_SA = args[4];
		final String pathToOutput_CBPopulation = args[5];

		CreateCBSecondaryActivities cbSecondaryActivities =
				new CreateCBSecondaryActivities(pathToFacilities, pathToCumulativeDepartureProbabilities, samplePercentage, randomSeed);

		cbSecondaryActivities.createCBPopulation(pathToCB_SA);
		cbSecondaryActivities.writeOutput(pathToOutput_CBPopulation);
	}

	@Override
	final void createCBPopulation(String path2CBFile) {
		BufferedReader reader = IOUtils.getBufferedReader(path2CBFile);
		try {
			reader.readLine(); // read header
			log.info("CB-Pop creation...");
			Counter counter = new Counter(" CB-SA-Pop - Zollst # ");
			String line = reader.readLine();
			while (line != null) {
				counter.incCounter();
				String[] lineElements = line.split(DELIMITER);
				ActivityFacility homeFacility =
						getOrigFacilities().getFacilities().get(Id.create(CreationOfCrossBorderFacilities.BC_TAG + lineElements[0], ActivityFacility.class));
				if (homeFacility == null) {
					log.error("BC-Facility " + CreationOfCrossBorderFacilities.BC_TAG + lineElements[0] + " not found.");
				}
				this.actTag = SHOP;
				for (int i = 0; i < Integer.parseInt(lineElements[1]); i++) {
					createSingleTripAgent(homeFacility, homeFacility, "saShop");
				}
				this.actTag = LEISURE;
				for (int i = 0; i < Integer.parseInt(lineElements[2]); i++) {
					createSingleTripAgent(homeFacility, homeFacility, "saLeisure");
				}
				line = reader.readLine();
			}
			counter.printCounter();
			log.info("CB-Pop creation... done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	final Plan createSingleTripPlan(ActivityFacility origFacility, ActivityFacility destFacility) {
		Plan plan = new PlanImpl();

		double departureTime = getDepartureTime();
		double actDuration = (4 + random.nextInt(24) + random.nextDouble())
				* PrefsCreator.actCharacteristics.valueOf(this.actTag.toUpperCase()).getMinDur();
		if (this.actTag.equals(SHOP)) {
			actDuration *= 1.5; // shop has a very small min-time (representing kiosk-shopping)
		}
		double returnTime = departureTime + actDuration;
		if (returnTime > 24.0 * 3600.0) {
			returnTime = 24.0 * 3600.0;
			departureTime = returnTime - actDuration;
		}

		ActivityImpl actStart = new ActivityImpl(HOME, origFacility.getCoord(), origFacility.getLinkId());
		actStart.setFacilityId(origFacility.getId());
		actStart.setStartTime(0.0);
		actStart.setMaximumDuration(departureTime);
		actStart.setEndTime(departureTime);
		plan.addActivity(actStart);

		plan.addLeg(new LegImpl("car"));

		ActivityImpl actSA = new ActivityImpl(this.actTag, destFacility.getCoord(), destFacility.getLinkId());
		actSA.setFacilityId(destFacility.getId());
		actSA.setStartTime(departureTime);
		actSA.setMaximumDuration(returnTime - departureTime);
		actSA.setEndTime(returnTime);
		plan.addActivity(actSA);

		plan.addLeg(new LegImpl("car"));

		ActivityImpl actEnd = new ActivityImpl(HOME, origFacility.getCoord(), origFacility.getLinkId());
		actEnd.setFacilityId(origFacility.getId());
		actEnd.setStartTime(returnTime);
		actEnd.setMaximumDuration(24.0 * 3600.0 - returnTime);
		plan.addActivity(actEnd);
		return plan;
	}
}