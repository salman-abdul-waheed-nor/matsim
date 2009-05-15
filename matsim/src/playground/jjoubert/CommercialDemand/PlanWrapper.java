/* *********************************************************************** *
 * project: org.matsim.*
 * PlanWrapper.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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

package playground.jjoubert.CommercialDemand;

import java.util.ArrayList;

import org.matsim.api.basic.v01.population.BasicActivity;
import org.matsim.api.basic.v01.population.BasicLeg;
import org.matsim.api.basic.v01.population.BasicPlan;
import org.matsim.api.basic.v01.population.BasicPopulationBuilder;

/**
 * A class to wrap a plan around a given time window. This is achieved by adding a <i>dummy</i> 
 * activity at the end of each <i>circulation</i>. The dummy activity has the same location as 
 * the first activity of the new <i>circulation</i>, forcing the travel leg to be executed in the 
 * current <i>circulation</i>. The dummy activity shares the same activity type as the first 
 * activity of the current <i>circulation</i>.
 *   
 * @author johanwjoubert
 *
 */
public class PlanWrapper {
	private final BasicPopulationBuilder pb; 	// Population builder 
	private final int tw;						// Time window
	private final int squeezeThreshold;			// Threshold 
	
	/**
	 * Constructor to create a <i>plan wrapper</i>
	 * 
	 * @param populationBuilder used to build temporary plans.
	 * @param timeWindow the time window (expressed in minutes), around which the plans will be 
	 *        <i>wrapped</i>.
	 * @param squeezeThreshold a time threshold (expressed in minutes after the time window ends) 
	 *        before which the plan will be squeezed proportionally to fit into one time window, 
	 *        rather than being <i>wrapped</i> around the time window.
	 *        
	 * <h4>Note:</h4> 
	 * TODO I am not quite sure if the <code>populationBuilder</code> is required. Maybe 
	 * one can rather create a new one locally?
	 */
	public PlanWrapper(BasicPopulationBuilder populationBuilder, int timeWindow, int squeezeThreshold){
		this.pb = populationBuilder;
		this.tw = timeWindow;
		this.squeezeThreshold = squeezeThreshold;
	}

	/**
	 * The method receives an activity plan, which may or may not span a given time window, and 
	 * <i>wrap</i> it into separate plans, each fitting within the time window.
	 * 
	 * @param plan of type {@code BasicPlan}
	 * @return an {@code ArrayList} of {@code BasicPlan}s
	 */
	@SuppressWarnings("unchecked")
	
	public ArrayList<BasicPlan> wrapPlan(BasicPlan plan) {
		ArrayList<BasicPlan> result = new ArrayList<BasicPlan>();
		
		Object firstActivity = plan.getPlanElements().get(0);
		// Checks that the first plan element is an activity
		if ( !(firstActivity instanceof BasicActivity) ){
			System.err.println("The last activity of the chain is not of type BasicActivity!!");
		}
		BasicActivity first = (BasicActivity) firstActivity;
	
		Object lastActivity = plan.getPlanElements().get(plan.getPlanElements().size() - 1);
			
		// Checks that the last plan element is an activity. 
		if( !(lastActivity instanceof BasicActivity) ){
			System.err.println("The last activity of the chain is not of type BasicActivity!!");
		} else{
			BasicActivity la = (BasicActivity)lastActivity;
			if(la.getStartTime() < this.tw){
				/*
				 * The whole plan fits within the time window. Just return the complete plan.
				 */
				result.add(plan);
			} else if( la.getStartTime() > this.tw && la.getStartTime() <= (this.tw + this.squeezeThreshold)){
				/*
				 * TODO Squeeze the plan.
				 */				
			} else if(la.getStartTime() > (this.tw + this.squeezeThreshold)){
				/*
				 * Wrap the plan
				 */
				BasicPlan dummyPlan = this.pb.createPlan(null);
				
				int index = 0;
				while(index < plan.getPlanElements().size()){
					Object object = plan.getPlanElements().get(index);
					if(object instanceof BasicActivity ){
						BasicActivity ba = (BasicActivity) object;
						if(ba.getEndTime() < this.tw){
							/*
							 * If the activity ends within the current time window, simply add the
							 * activity to the current dummy plan.
							 */
							dummyPlan.getPlanElements().add(ba);
							index++;
						} else{
							/* 
							 * STEP 1: Create a dummy activity to add at the end of the first plan. The location
							 *         of the dummy activity is then same as first activity of the new 'day': 
							 *         forcing the traveling (if required) to occur in the current plan; the 
							 *         activity type is the same as the first activity of the current plan.
							 */ 
							BasicActivity dummyActivity = this.pb.createActivityFromCoord(first.getType(), ba.getCoord());
							dummyActivity.setStartTime(this.tw);
							dummyPlan.getPlanElements().add(dummyActivity);
							result.add(dummyPlan);
							
							/*
							 * STEP 2: Create a new dummy plan, and add the remaining plan elements to it, adjusting
							 *         the end times of each activity.
							 */
							dummyPlan = this.pb.createPlan(null);
							ba.setStartTime(ba.getStartTime() - this.tw);
							ba.setEndTime(ba.getEndTime() - this.tw);
							dummyPlan.getPlanElements().add(ba);
							index++;
							while(index < plan.getPlanElements().size()){
								Object dummyObject = plan.getPlanElements().get(index);
								if(dummyObject instanceof BasicActivity){
									BasicActivity ba2 = (BasicActivity) dummyObject;
									ba2.setStartTime( (ba2.getStartTime() - this.tw) >= 0 ? ba2.getStartTime() - this.tw : Double.NEGATIVE_INFINITY );
									ba2.setEndTime((ba2.getEndTime() - this.tw) >= 0 ? ba2.getEndTime() - this.tw : Double.NEGATIVE_INFINITY );
									dummyPlan.getPlanElements().add(ba2);
									index++;
								} else if(dummyObject instanceof BasicLeg){
									BasicLeg bl2 = (BasicLeg) dummyObject;
									bl2.setDepartureTime( bl2.getDepartureTime() >= 0 ? bl2.getDepartureTime() : Double.NEGATIVE_INFINITY );
									dummyPlan.getPlanElements().add(bl2);
									index++;
								} else{
									System.err.println("Plan element is neither a BasicActivity nor a BasicLeg!!");									
								}
							}
							
							/* 
							 * STEP 3: Check the new dummy plan. 
							 */
							PlanWrapper pw = new PlanWrapper(this.pb,this.tw,squeezeThreshold);
							ArrayList<BasicPlan> recursivePlans = pw.wrapPlan(dummyPlan);
							for (BasicPlan bp : recursivePlans) {
								result.add(bp);
							}
						}
					} else if(object instanceof BasicLeg){
						BasicLeg bl = (BasicLeg) object;
						dummyPlan.getPlanElements().add(bl);
						index++;
					} else{
						System.err.println("Plan element is neither a BasicActivity nor a BasicLeg!!");
					}
				}
				
			} 
		}
		return result;
	}	
	
	public BasicPopulationBuilder getPopulationBuilder() {
		return this.pb;
	}
	
	public Integer getTimeWindow() {
		return this.tw;
	}

}