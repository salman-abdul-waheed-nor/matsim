package playground.ikaddoura.busCorridor.version7test;

import java.util.Map;
import org.apache.log4j.Logger;
import org.matsim.core.utils.charts.LineChart;

public class ChartFileWriter {
	private final static Logger log = Logger.getLogger(ChartFileWriter.class);
	
	public void writeChart_Parameters(String outputExternalIterationDirPath, Map<Integer, Double> myMap, String title, String parameter) {
			
			String[] xValues = new String[myMap.size()];
			int cc = 0;
			for (Integer xValue : myMap.keySet()){
				xValues[cc] = xValue.toString();
				cc++;
			}
			
			LineChart chart = new LineChart(title, "Iteration", parameter);
		    
			double[] yWerte = new double[myMap.size()];
			int counter2 = 0;
			for (Integer iteration : myMap.keySet()){
				yWerte[counter2] = myMap.get(iteration);
				counter2++;
			}
			chart.addSeries(parameter, yWerte);
			
			String outputFile = outputExternalIterationDirPath+"/Parameters_"+parameter+".png";
			chart.saveAsPng(outputFile, 1000, 800); //File Export
			log.info("Parameters written to "+outputFile);
		}
	
	public void writeChart_LegModes(String outputExternalIterationDirPath, Map<Integer, Integer> iteration2numberOfCarLegs, Map<Integer, Integer> iteration2numberOfPtLegs) {

		String[] xValues = new String[iteration2numberOfCarLegs.size()];
		int counter1 = 0;
		for (Integer xValue : iteration2numberOfCarLegs.keySet()){
			xValues[counter1] = xValue.toString();
			counter1++;
		}
	
		LineChart chart = new LineChart("Leg Mode Analysis", "Iteration", "Legs", xValues);
			double[] yWerteCar = new double[iteration2numberOfCarLegs.size()];
			double[] yWertePt = new double[iteration2numberOfPtLegs.size()];
			
			int counter2 = 0;
			for (Integer iteration : iteration2numberOfCarLegs.keySet()){
				xValues[counter2] = iteration.toString();
				yWerteCar[counter2] = iteration2numberOfCarLegs.get(iteration);
				yWertePt[counter2] = iteration2numberOfPtLegs.get(iteration);
				counter2++;
			}
			chart.addSeries("CarLegs", yWerteCar);
			chart.addSeries("PtLegs", yWertePt);

		String outputFile = outputExternalIterationDirPath+"/LegMode.png";
		chart.saveAsPng(outputFile, 2000, 800); //File Export
		log.info("Legs written to "+outputFile);
	}
	
	public void writeChart_UserScores(String outputExternalIterationDirPath, Map<Integer, Double> iteration2score) {
		
		String[] xValues = new String[iteration2score.size()];
		int counter1 = 0;
		for (Integer xValue : iteration2score.keySet()){
			xValues[counter1] = xValue.toString();
			counter1++;
		}
		
		LineChart chart = new LineChart("User score per iteration", "Iteration", "User Score (avg. executed)");
	    
		double[] yWerte = new double[iteration2score.size()];
		int counter2 = 0;
		for (Integer iteration : iteration2score.keySet()){
			yWerte[counter2] = iteration2score.get(iteration);
			counter2++;
		}
		chart.addSeries("User Score", yWerte);
		
		String outputFile = outputExternalIterationDirPath+"/UserScore.png";
		chart.saveAsPng(outputFile, 1000, 800); //File Export
		log.info("UserScores written to "+outputFile);
	}
	
	public void writeChart_OperatorScores(String outputExternalIterationDirPath, Map<Integer, Double> iteration2score, Map<Integer, Double> iteration2operatorCosts, Map<Integer, Double> iteration2operatorEarnings) {
		
		String[] xValues = new String[iteration2score.size()];
		int counter1 = 0;
		for (Double xValue : iteration2score.values()){
			xValues[counter1] = xValue.toString();
			counter1++;
		}
		
		LineChart chart = new LineChart("Operator score per iteration", "Iteration", "AUD");
	    
		double[] yWerte1 = new double[iteration2score.size()];
		int counter2 = 0;
		for (Integer iteration : iteration2score.keySet()){
			yWerte1[counter2] = iteration2score.get(iteration);
			counter2++;
		}
		double[] yWerte2 = new double[iteration2operatorCosts.size()];
		int counter3 = 0;
		for (Integer iteration : iteration2operatorCosts.keySet()){
			yWerte2[counter3] = iteration2operatorCosts.get(iteration);
			counter3++;
		}
		double[] yWerte3 = new double[iteration2operatorEarnings.size()];
		int counter4 = 0;
		for (Integer iteration : iteration2operatorEarnings.keySet()){
			yWerte3[counter4] = iteration2operatorEarnings.get(iteration);
			counter4++;
		}
		chart.addSeries("Profit", yWerte1);
		chart.addSeries("Costs", yWerte2);
		chart.addSeries("Earnings", yWerte3);
		
		String outputFile = outputExternalIterationDirPath+"/OperatorScore.png";
		chart.saveAsPng(outputFile, 1000, 800); //File Export
		log.info("OperatorScores written to "+outputFile);
	}
}
