/*
 *  *********************************************************************** *
 *  * project: org.matsim.*
 *  * DefaultControlerModules.java
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  * copyright       : (C) 2014 by the members listed in the COPYING, *
 *  *                   LICENSE and WARRANTY file.                            *
 *  * email           : info at matsim dot org                                *
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  *   This program is free software; you can redistribute it and/or modify  *
 *  *   it under the terms of the GNU General Public License as published by  *
 *  *   the Free Software Foundation; either version 2 of the License, or     *
 *  *   (at your option) any later version.                                   *
 *  *   See also COPYING, LICENSE and WARRANTY file                           *
 *  *                                                                         *
 *  * ***********************************************************************
 */
package scenarios.illustrative.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;

import com.google.inject.Inject;

/**
 * Class to write all calculated results of the analyze tool that is given in the constructor.
 * It works for all handlers that extend the abstract analyze tool TtAbstractAnalyzeTool.
 *
 * @author tthunig
 */
public class TtAnalyzedResultsWriter {

	private static final Logger log = Logger.getLogger(TtAnalyzedResultsWriter.class);
	
	private TtAbstractAnalysisTool handler;
	private String outputDirBase;
	private PrintStream overallItWritingStream;
	private int numberOfRoutes;
	private int lastIteration;
	
	@Inject
	public TtAnalyzedResultsWriter(Scenario scenario, TtAbstractAnalysisTool handler) {
		this.handler = handler;
		this.outputDirBase = scenario.getConfig().controler().getOutputDirectory();
		this.lastIteration = scenario.getConfig().controler().getLastIteration();
		
		this.numberOfRoutes = handler.getNumberOfRoutes();
		
		// prepare file for the results of all iterations
		prepareOverallItWriting();
	}

	private void prepareOverallItWriting() {
		// create output dir for overall iteration analysis
		String lastItDir = this.outputDirBase + "ITERS/it." + this.lastIteration + "/";
		new File(lastItDir).mkdir();
		String lastItOutputDir = lastItDir + "analysis/";
		new File(lastItOutputDir).mkdir();
		
		// create writing stream
		try {	
			this.overallItWritingStream = new PrintStream(
					new File(lastItOutputDir + "routesAndTTs.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		// write header
		String header = "it\ttotal tt[s]";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\t#users " + routeNr;
		}
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\tavg tt[s] " + routeNr;
		}
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\tavg toll " + routeNr;
		}
		header += "\t#stucked";
		this.overallItWritingStream.println(header);
	}

	public void writeIterationResults(int iteration) {	
		log.info("Starting to write analysis of iteration " + iteration + "...");
		
		// collect results of this iteration
		double totalTTIt = handler.getTotalTT();
		double[] avgRouteTTsIt = handler.calculateAvgRouteTTs();
		int[] routeUsersIt = handler.getRouteUsers();
		int numberOfStuckedAgents = handler.getNumberOfStuckedAgents();
		double[] avgRouteTollsIt = handler.calculateAvgRouteTolls();

		// add line with results to overallItWritingStream
		StringBuffer line = new StringBuffer();
		line.append(iteration + "\t" + totalTTIt);
		for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
			line.append("\t" + routeUsersIt[routeNr]);
		}
		for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
			line.append("\t" + avgRouteTTsIt[routeNr]);
		}
		for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
			line.append("\t" + avgRouteTollsIt[routeNr]);
		}
		line.append("\t" + numberOfStuckedAgents);
		this.overallItWritingStream.println(line.toString());
		
		// create output dir for this iteration analysis
		String outputDir = this.outputDirBase + "ITERS/it." + iteration + "/analysis/";
		new File(outputDir).mkdir();

		// write iteration specific analysis
		log.info("Results of iteration " + iteration + ":");
		writeFinalResults(outputDir, totalTTIt, avgRouteTTsIt, avgRouteTollsIt, routeUsersIt, numberOfStuckedAgents);
		writeOnRoutes(outputDir, handler.getOnRoutePerSecond());
		writeRouteStarts(outputDir, handler.getRouteDeparturesPerSecond());
		writeSummedRouteStarts(outputDir, handler.calculateSummedRouteDeparturesPerSecond());
		writeAvgRouteTTs(outputDir, "Departure", handler.calculateAvgRouteTTsByDepartureTime());
		writeAvgRouteTolls(outputDir, handler.calculateAvgRouteTollsByDepartureTime());
	}

	private void writeSummedRouteStarts(String outputDir, Map<Double, int[]> summedRouteDeparturesPerSecond) {
		PrintStream stream;
		String filename = outputDir + "summedDeparturesPerRoute.txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String header = "time";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\tsum_starts_" + routeNr;
		}
		header += "\tsum_starts_total";
		stream.println(header);
		for (Double time : summedRouteDeparturesPerSecond.keySet()) {
			StringBuffer line = new StringBuffer();
			int[] routeStarts = summedRouteDeparturesPerSecond.get(time);
			int totalStarts = 0;
			
			line.append(time);
			for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
				line.append("\t" + routeStarts[routeNr]);
				totalStarts += routeStarts[routeNr];
			}
			line.append("\t" + totalStarts);
			stream.println(line.toString());
		}

		stream.close();
		
		log.info("output written to " + filename);
	}

	/**
	 * Create result file for the specific iteration (FinalResults.txt) and
	 * write some results to the console.
	 */
	private void writeFinalResults(String outputDir, double totalTT, double[] avgRouteTTs, double[] avgRouteTolls, int[] routeUsers, int numberOfStuckedAgents) {

		PrintStream stream;
		String filename = outputDir + "FinalResults.txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	
		log.info("Total travel time: " + totalTT);
		String header = "total tt[s]";
		StringBuffer resultLine = new StringBuffer();
		resultLine.append(totalTT);
		String latexFormat = "" + (int)totalTT;
		
		log.info("Route Users: (route: #users)");
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			log.info("\t" + routeNr + ": " + routeUsers[routeNr]);
			header += "\t#users " + routeNr;
			resultLine.append("\t" + routeUsers[routeNr]);
			latexFormat += " & " + routeUsers[routeNr];
		}
		
		log.info("Average travel times: (route: avg tt)");
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			log.info("\t" + routeNr + ": " + avgRouteTTs[routeNr]);
			header += "\tavg tt[s] " + routeNr;
			resultLine.append("\t" + avgRouteTTs[routeNr]);
			latexFormat += " & " + (Double.isNaN(avgRouteTTs[routeNr]) ? "-" : (int)avgRouteTTs[routeNr]);
		}
		
		log.info("Average tolls: (route: avg toll)");
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			log.info("\t" + routeNr + ": " + avgRouteTolls[routeNr]);
			header += "\tavg toll " + routeNr;
			resultLine.append("\t" + avgRouteTolls[routeNr]);
			latexFormat += " & " + (Double.isNaN(avgRouteTolls[routeNr]) ? "-" : (int)avgRouteTolls[routeNr]);
		}
		
		log.info("Number of stucked agents: " + numberOfStuckedAgents);
		header += "\t#stucked";
		resultLine.append("\t" + numberOfStuckedAgents);
		
		latexFormat += " \\\\";
		log.info("Latex format: " + latexFormat);
				
		stream.println(header);
		stream.println(resultLine.toString());
		stream.close();
		
		log.info("output written to " + filename);
	}

	private void writeRouteStarts(String outputDir, Map<Double, int[]> routeStartsMap) {
		PrintStream stream;
		String filename = outputDir + "startsPerRoute.txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String header = "time";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\t#starts " + routeNr;
		}
		header += "\t#starts total";
		stream.println(header);
		for (Double time : routeStartsMap.keySet()) {
			StringBuffer line = new StringBuffer();
			int[] routeStarts = routeStartsMap.get(time);
			int totalStarts = 0;
			
			line.append(time);
			for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
				line.append("\t" + routeStarts[routeNr]);
				totalStarts += routeStarts[routeNr];
			}
			line.append("\t" + totalStarts);
			stream.println(line.toString());
		}

		stream.close();
		
		log.info("output written to " + filename);
	}

	private void writeOnRoutes(String outputDir, Map<Double, int[]> onRoutesMap) {
		PrintStream stream;
		String filename = outputDir + "onRoutes.txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String header = "time";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\t#users " + routeNr;
		}
		header += "\t#users total";
		stream.println(header);
		for (Double time : onRoutesMap.keySet()) {
			StringBuffer line = new StringBuffer();
			int[] onRoutes = onRoutesMap.get(time);
			int totalOnRoute = 0;
			
			line.append(time);
			for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
				line.append("\t" + onRoutes[routeNr]);
				totalOnRoute += onRoutes[routeNr];
			}
			line.append("\t" + totalOnRoute);
			stream.println(line.toString());
		}

		stream.close();
		
		log.info("output written to " + filename);
	}

	private void writeAvgRouteTTs(String outputDir, String eventType, Map<Double, double[]> avgTTs) {
		PrintStream stream;
		String filename = outputDir + "avgRouteTTsPer" + eventType + ".txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String header = eventType + "Time";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\tavg tt " + routeNr;
		}
		stream.println(header);
		for (Double eventTime : avgTTs.keySet()) {
			StringBuffer line = new StringBuffer();
			double[] avgRouteTTs = avgTTs.get(eventTime);
			
			line.append(eventTime);
			for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
				line.append("\t" + avgRouteTTs[routeNr]);
			}
			stream.println(line.toString());
		}

		stream.close();
		
		log.info("output written to " + filename);
	}
	
	private void writeAvgRouteTolls(String outputDir, Map<Double, double[]> avgTolls) {
		PrintStream stream;
		String filename = outputDir + "avgRouteTollsPerDeparture" + ".txt";
		try {
			stream = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		String header = "DepartureTime";
		for (int routeNr=0; routeNr < numberOfRoutes; routeNr++){
			header += "\tavg toll " + routeNr;
		}
		stream.println(header);
		for (Double eventTime : avgTolls.keySet()) {
			StringBuffer line = new StringBuffer();
			double[] avgRouteTolls = avgTolls.get(eventTime);
			
			line.append(eventTime);
			for (int routeNr = 0; routeNr < numberOfRoutes; routeNr++) {
				line.append("\t" + avgRouteTolls[routeNr]);
			}
			stream.println(line.toString());
		}

		stream.close();
		
		log.info("output written to " + filename);
	}

	public void closeAllStreams() {
		this.overallItWritingStream.close();
	}
	
}
