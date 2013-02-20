/* *********************************************************************** *
 * project: org.matsim.*
 * HybridQ2DMobsimFactory.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package playground.gregor.sim2d_v4.simulation;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.mobsim.framework.MobsimFactory;
import org.matsim.core.mobsim.qsim.ActivityEngine;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.TeleportationEngine;
import org.matsim.core.mobsim.qsim.agents.AgentFactory;
import org.matsim.core.mobsim.qsim.agents.DefaultAgentFactory;
import org.matsim.core.mobsim.qsim.agents.PopulationAgentSource;
import org.matsim.core.mobsim.qsim.qnetsimengine.HybridQSim2DNetworkFactory;
import org.matsim.core.mobsim.qsim.qnetsimengine.QNetsimEngine;

import playground.gregor.sim2d_v4.debugger.VisDebugger;

public class HybridQ2DMobsimFactory implements MobsimFactory {

	private final static Logger log = Logger.getLogger(HybridQ2DMobsimFactory.class);
	
	Sim2DEngine sim2DEngine = null;

	private VisDebugger debugger;

	@Override
	public Mobsim createMobsim(Scenario sc, EventsManager eventsManager) {

		if (!sc.getConfig().controler().getMobsim().equals("hybridQ2D")) {
			throw new RuntimeException("This factory does not make sense for " + sc.getConfig().controler().getMobsim()  );
		}
		
		QSimConfigGroup conf = sc.getConfig().getQSimConfigGroup();
		if (conf == null) {
			throw new NullPointerException("There is no configuration set for the QSim. Please add the module 'qsim' to your config file.");
		}

		// Get number of parallel Threads
		int numOfThreads = conf.getNumberOfThreads();

//		QNetsimEngineFactory netsimEngFactory;
//		if (numOfThreads > 1) {
//			eventsManager = new SynchronizedEventsManagerImpl(eventsManager);
//			netsimEngFactory = new ParallelQNetsimEngineFactory();
//			log.info("Using parallel QSim with " + numOfThreads + " threads.");
//		} else {
//			netsimEngFactory = new DefaultQSimEngineFactory();
//		}


		QSim qSim = new QSim(sc, eventsManager);

		TeleportationEngine teleportationEngine = new TeleportationEngine();
		qSim.addMobsimEngine(teleportationEngine);
		
		Sim2DEngine e = new Sim2DEngine(qSim);
		
		//DEBUG
		e.debug(this.debugger);
		
		this.sim2DEngine = e;
		qSim.addMobsimEngine(e);
		
		ActivityEngine activityEngine = new ActivityEngine();
		qSim.addMobsimEngine(activityEngine);
		qSim.addActivityHandler(activityEngine);
		
//		Sim2DScenario sc2d = sc.getScenarioElement(Sim2DScenario.class);
//		Sim2DAgentFactory aBuilder = new SocialForceSim2DAgentFactory(sc2d.getSim2DConfig());
		Sim2DAgentFactory aBuilder = new ORCAAgentFactory();
		
		HybridQSim2DNetworkFactory networkFactory = new HybridQSim2DNetworkFactory(e,sc, aBuilder);
		
		QNetsimEngine netsimEngine = new QNetsimEngine( qSim, networkFactory ) ;
		qSim.addMobsimEngine(netsimEngine);
		qSim.addDepartureHandler(netsimEngine.getDepartureHandler());

		AgentFactory agentFactory = new DefaultAgentFactory(qSim);
		PopulationAgentSource agentSource = new PopulationAgentSource(sc.getPopulation(), agentFactory, qSim);
		qSim.addAgentSource(agentSource);

		return qSim;
	}

	public Sim2DEngine getSim2DEngine() {
		return this.sim2DEngine;
	}

	public void debug(VisDebugger visDebugger) {
		this.debugger = visDebugger;
		
	}
}
