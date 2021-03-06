package playground.gregor.misanthrope.debug;
/* *********************************************************************** *
 * project: org.matsim.*
 *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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

import com.google.inject.Provider;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.mobsim.framework.Mobsim;
import org.matsim.core.router.costcalculators.FreespeedTravelTimeAndDisutility;
import org.matsim.core.router.util.*;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import playground.gregor.misanthrope.events.JumpEventCSVWriter;
import playground.gregor.misanthrope.events.JumpEventHandler;
import playground.gregor.misanthrope.router.CTRoutingModule;
import playground.gregor.misanthrope.run.CTRunner;
import playground.gregor.misanthrope.simulation.CTMobsimFactory;
import playground.gregor.sim2d_v4.debugger.eventsbaseddebugger.EventBasedVisDebuggerEngine;
import playground.gregor.sim2d_v4.debugger.eventsbaseddebugger.InfoBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by laemmel on 20/10/15.
 */
public class Debugger5 {

    public static final double WIDTH = 20;
    public static double AGENTS_LR = 6000;
    public static double AGENTS_RL = 6000;
    public static double INV_INFLOW = 0.3;
    private static double LL = 64.95190528383289850700;

    public static void main(String[] args) {
        Config c2 = ConfigUtils.createConfig();
        Scenario sc2 = ScenarioUtils.createScenario(c2);
        createSc(sc2);
        createBigPopLR(sc2);
//        createBigPopRL(sc2);
        setupConfig(sc2);
        runScenario(sc2);

    }

    private static void runScenario(Scenario sc) {
        final Controler controller = new Controler(sc);
        controller.getConfig().controler().setOverwriteFileSetting(
                true ?
                        OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles :
                        OutputDirectoryHierarchy.OverwriteFileSetting.failIfDirectoryExists);
        //DEBUG
        CTRunner.DEBUG = true;
//		Sim2DConfig conf2d = Sim2DConfigUtils.createConfig();
//		Sim2DScenario sc2d = Sim2DScenarioUtils.createSim2dScenario(conf2d);
//
//
//		sc.addScenarioElement(Sim2DScenario.ELEMENT_NAME, sc2d);
        EventBasedVisDebuggerEngine dbg = new EventBasedVisDebuggerEngine(sc);
        InfoBox iBox = new InfoBox(dbg, sc);
//        dbg.addAdditionalDrawer(iBox);
        //		dbg.addAdditionalDrawer(new Branding());
//			QSimDensityDrawer qDbg = new QSimDensityDrawer(sc);
//			dbg.addAdditionalDrawer(qDbg);

        EventsManager em = controller.getEvents();
//			em.addHandler(qDbg);
        em.addHandler(dbg);
        //END_DEBUG

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File("/Users/laemmel/scenarios/misanthrope/sc1")));
            JumpEventHandler h = new JumpEventCSVWriter(bw);
            em.addHandler(h);
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                addRoutingModuleBinding("walkct").toProvider(CTRoutingModule.class);
            }
        });

        final CTMobsimFactory factory = new CTMobsimFactory();

        controller.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                if (getConfig().controler().getMobsim().equals("ctsim")) {
                    bind(Mobsim.class).toProvider(new Provider<Mobsim>() {
                        @Override
                        public Mobsim get() {
                            return factory.createMobsim(controller.getScenario(), controller.getEvents());
                        }
                    });
                }
            }
        });

        controller.run();

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setupConfig(Scenario sc) {
        sc.getNetwork().setEffectiveCellSize(.26);
        sc.getNetwork().setEffectiveLaneWidth(.71);
        sc.getNetwork().setCapacityPeriod(1);
        Config c = sc.getConfig();

        c.strategy().addParam("Module_1", "ChangeExpBeta");
        c.strategy().addParam("ModuleProbability_1", ".5");

        c.controler().setMobsim("ctsim");

        c.controler().setLastIteration(0);

        PlanCalcScoreConfigGroup.ActivityParams pre = new PlanCalcScoreConfigGroup.ActivityParams("origin");
        pre.setTypicalDuration(49); // needs to be geq 49, otherwise when
        // running a simulation one gets
        // "java.lang.RuntimeException: zeroUtilityDuration of type pre-evac must be greater than 0.0. Did you forget to specify the typicalDuration?"
        // the reason is the double precision. see also comment in
        // ActivityUtilityParameters.java (gl)
        pre.setMinimalDuration(49);
        pre.setClosingTime(49);
        pre.setEarliestEndTime(49);
        pre.setLatestStartTime(49);
        pre.setOpeningTime(49);

        PlanCalcScoreConfigGroup.ActivityParams post = new PlanCalcScoreConfigGroup.ActivityParams("destination");
        post.setTypicalDuration(49); // dito
        post.setMinimalDuration(49);
        post.setClosingTime(49);
        post.setEarliestEndTime(49);
        post.setLatestStartTime(49);
        post.setOpeningTime(49);
        sc.getConfig().planCalcScore().addActivityParams(pre);
        sc.getConfig().planCalcScore().addActivityParams(post);

        sc.getConfig().planCalcScore().setLateArrival_utils_hr(0.);
        sc.getConfig().planCalcScore().setPerforming_utils_hr(0.);

        QSimConfigGroup qsim = sc.getConfig().qsim();
        // qsim.setEndTime(20 * 60);
        c.controler().setMobsim("ctsim");
        c.global().setCoordinateSystem("EPSG:3395");

        c.qsim().setEndTime(30 * 3600);

        c.controler().setOutputDirectory("/Users/laemmel/tmp" + "/sc2/");
    }

    private static void createSc(Scenario sc1) {
        Network net1 = sc1.getNetwork();
        NetworkFactory fac1 = net1.getFactory();
//		Node n1_0 = fac1.createNode(Id.createNodeId("n1_0"), CoordUtils.createCoord(0, 0));
//		Node n1_1 = fac1.createNode(Id.createNodeId("n1_1"), CoordUtils.createCoord(10, 0));
//		Node n1_9 = fac1.createNode(Id.createNodeId("n1_9"), CoordUtils.createCoord(10 + LL, 0));
//		Node n1_10 = fac1.createNode(Id.createNodeId("n1_10"), CoordUtils.createCoord(10 + LL + 10, 0));
//		net1.addNode(n1_0);
//		net1.addNode(n1_1);
//		net1.addNode(n1_9);
//		net1.addNode(n1_10);
//		Link l1_0 = fac1.createLink(Id.createLinkId("l1_0"), n1_0, n1_1);
//		Link l1_8 = fac1.createLink(Id.createLinkId("e1_8"), n1_1, n1_9);
//		Link l1_9 = fac1.createLink(Id.createLinkId("l1_9"), n1_9, n1_10);
//		Link l1_0r = fac1.createLink(Id.createLinkId("l1_0r"), n1_1, n1_0);
//		Link l1_8r = fac1.createLink(Id.createLinkId("1_8r"), n1_9, n1_1);
//		Link l1_9r = fac1.createLink(Id.createLinkId("l1_9r"), n1_10, n1_9);
//		net1.addLink(l1_0);
//		net1.addLink(l1_8);
//		net1.addLink(l1_9);
//		net1.addLink(l1_0r);
//		net1.addLink(l1_8r);
//		net1.addLink(l1_9r);

        double x1 = 150;
        double y1 = 0;
        Node n1_0 = fac1.createNode(Id.createNodeId("n1_0"), CoordUtils.createCoord(0, 0));
        net1.addNode(n1_0);
        int nodId = 1;
        int linkId = 0;

        int cnt = 0;
        for (double angle = 0; angle <= 2 * Math.PI - Math.PI / 3; angle += Math.PI / 3) {
//            if (MatsimRandom.getRandom().nextDouble() > 0.4){
//                continue;
//            }
            if (cnt++ == 1 || cnt == 6) {
                continue;
            }
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double xPrime = x1 * cos - y1 * sin;
            double yPrime = x1 * sin + y1 * cos;
            Node n = fac1.createNode(Id.createNodeId("n" + nodId++), CoordUtils.createCoord(xPrime, yPrime));
            net1.addNode(n);
            Link l = fac1.createLink(Id.createLinkId("l" + linkId), n1_0, n);
            Link lr = fac1.createLink(Id.createLinkId("lr" + linkId++), n, n1_0);
            net1.addLink(l);
            net1.addLink(lr);
        }


        Set<String> modes = new HashSet<>();
        modes.add("walkct");
        for (Link l : net1.getLinks().values()) {


            l.setAllowedModes(modes);
            l.setFreespeed(20);
            l.setLength(150.);
//            l.setCapacity((MatsimRandom.getRandom().nextDouble()+0.5)*WIDTH * 1.33);
            l.setCapacity(WIDTH * 1.33);

            if (l.getId().toString().contains("0")) {
                l.setCapacity(1.5 * WIDTH * 1.33);
            }
        }

//		l1_8.setLength(LL);
//		l1_8r.setLength(LL);

    }

    private static void createBigPopLR(Scenario sc1) {
        Population pop1 = sc1.getPopulation();
        PopulationFactory popFac1 = pop1.getFactory();

        int offset = pop1.getPersons().size();

        for (int i = 0; i < AGENTS_LR; i++) {
            Person pers = popFac1.createPerson(Id.create("b" + (i + offset), Person.class));
            Plan plan = popFac1.createPlan();
            pers.addPlan(plan);
            Activity act0;
            act0 = popFac1.createActivityFromLinkId("origin",
                    Id.create("lr0", Link.class));
            act0.setEndTime(i * INV_INFLOW);
            plan.addActivity(act0);
            Leg leg = popFac1.createLeg("walkct");
            plan.addLeg(leg);
            Activity act1 = popFac1.createActivityFromLinkId("destination",
                    Id.create("l2", Link.class));
            plan.addActivity(act1);
            pop1.addPerson(pers);
        }

    }

    private static void createBigPopRL(Scenario sc1) {
        Population pop1 = sc1.getPopulation();
        PopulationFactory popFac1 = pop1.getFactory();

        int offset = pop1.getPersons().size();


        for (int i = 0; i < AGENTS_RL; i++) {
            Person pers = popFac1.createPerson(Id.create("r" + (i + offset), Person.class));
            Plan plan = popFac1.createPlan();
            pers.addPlan(plan);
            Activity act0;
            act0 = popFac1.createActivityFromLinkId("origin",
                    Id.create("l2r", Link.class));
            act0.setEndTime(i * INV_INFLOW);
            plan.addActivity(act0);
            Leg leg = popFac1.createLeg("walkct");
            plan.addLeg(leg);
            Activity act1 = popFac1.createActivityFromLinkId("destination",
                    Id.create("l0", Link.class));
            plan.addActivity(act1);
            pop1.addPerson(pers);
        }

    }

    private static LeastCostPathCalculatorFactory createDefaultLeastCostPathCalculatorFactory(
            Scenario scenario) {
        Config config = scenario.getConfig();
        if (config.controler().getRoutingAlgorithmType()
                .equals(ControlerConfigGroup.RoutingAlgorithmType.Dijkstra)) {
            return new DijkstraFactory();
        } else {
            if (config
                    .controler()
                    .getRoutingAlgorithmType()
                    .equals(ControlerConfigGroup.RoutingAlgorithmType.AStarLandmarks)) {
                return new AStarLandmarksFactory(
                        scenario.getNetwork(),
                        new FreespeedTravelTimeAndDisutility(config.planCalcScore()),
                        config.global().getNumberOfThreads());
            } else {
                if (config.controler().getRoutingAlgorithmType()
                        .equals(ControlerConfigGroup.RoutingAlgorithmType.FastDijkstra)) {
                    return new FastDijkstraFactory();
                } else {
                    if (config
                            .controler()
                            .getRoutingAlgorithmType()
                            .equals(ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks)) {
                        return new FastAStarLandmarksFactory(
                                scenario.getNetwork(),
                                new FreespeedTravelTimeAndDisutility(config.planCalcScore()));
                    } else {
                        throw new IllegalStateException(
                                "Enumeration Type RoutingAlgorithmType was extended without adaptation of Controler!");
                    }
                }
            }
        }
    }
}
