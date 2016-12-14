import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.fathzer.soft.javaluator.DoubleEvaluator;

public class Engine {
	
	private static final String TRAIN_URL = "src/cwk_train.csv";
	private static final int STARTING_POP = 10;
	private static final double MUTATION_PROBABILITY = 1.0;
	private ArrayList<Row> dataSet = new ArrayList<Row>();
	private ArrayList<Row> testSet = new ArrayList<Row>();
	private ArrayList<Solution> current = new ArrayList<Solution>();
	private DoubleEvaluator evaluator = new DoubleEvaluator();
	
	public Engine(){
		dataSet = popDataTable(TRAIN_URL);
		testSet = popTestSet();
		current = initialisation(13);
		//evaluate(current);
		test();
	}
	
	public Solution getBest(ArrayList<Solution> solutions){
		Solution best = null;
		for(int i = 0; i<solutions.size();i++){
			if(best == null || Math.abs(solutions.get(i).getEvaluation()) < Math.abs(best.getEvaluation())){
				best = solutions.get(i);
			}
		}
		return best;
	}
	public ArrayList<Solution> initialisation(int operandsInData){
		ArrayList<Solution> solutions = new ArrayList<Solution>();
		for(int i = 0; i<STARTING_POP; i++){
			solutions.add(new Solution(operandsInData));
		}
		return solutions;
	}

	public ArrayList<String> applyDataToFunction(Row data, Solution solution){
		ArrayList<String> newSolution = new ArrayList<String>();
		
		for(int i = 0; i<data.getRow().size(); i++){
			newSolution.add(data.getRow().get(i));
			if(i<solution.getSolution().size()){
				newSolution.add(solution.getSolution().get(i));
			}
		}
		
		return newSolution;
	}
	
	public ArrayList<Row> popDataTable(String url){
		ArrayList<Row> dataTable = new ArrayList<Row>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File(url));
			scanner.useDelimiter(",");
			
			while(scanner.hasNextLine()){
	        	String line = scanner.nextLine();
	        	String[] lineSplit = line.split(",");
	        	dataTable.add(new Row(lineSplit));  	
		}
		} catch(FileNotFoundException e){
		}		
		
		return dataTable;
	}

	public ArrayList<Row> popTestSet(){
		ArrayList<Row> returnSet = new ArrayList<Row>();
		for(int i = 2; i<5; i++){
			testSet.add(dataSet.get(i));
		}
		return testSet;
	}

	public void evaluate(ArrayList<Solution> solutions){

				for(int i = 0; i<solutions.size(); i++){
					Double distanceAway = 0.0;
					for(int j = 0; j<testSet.size(); j++){
						Double evaluated = evaluator.evaluate(listToString(
								applyDataToFunction(
								testSet.get(j),solutions.get(i))));
						Double expected = testSet.get(j).getExpected();
						Double extra = expected - evaluated;
						distanceAway += extra;
						//System.out.println(expected+" :: "+evaluated+" :: " +extra+" :: "+listToString(applyDataToFunction(testSet.get(j),functions.get(i))));
						}
					
					distanceAway = distanceAway/testSet.size();
					solutions.get(i).setEvaluation(distanceAway);
					System.out.println(solutions.get(i).getSolution().toString() + " :: " + solutions.get(i).getEvaluation());
				}
	}
	
	public String listToString(ArrayList<String> string){
		String retStr = "";
		
		for(int i = 0; i<string.size(); i++){
			retStr += string.get(i);
		}
		return retStr;
	}
	
	private ArrayList<Solution> mutatePopulation(ArrayList<Solution> toMutate){
		ArrayList<Solution> mutated = new ArrayList<Solution>();
		for(int i = 0; i<toMutate.size(); i++){
			Random r = new Random();
			int random = r.nextInt(toMutate.get(i).getSize());
			mutated.add(toMutate.get(i).clone());
			mutated.get(i).changeRandomly(random);
		}
		return mutated;
	}

	
	
	public void test(){
		ArrayList<String> testSol = new ArrayList<String>();
		testSol.add("+"); testSol.add("+"); testSol.add("+");
		testSol.add("-"); testSol.add("-"); testSol.add("-"); 
		testSol.add("*"); testSol.add("*"); testSol.add("*"); 
		
		ArrayList<String> testSol2 = new ArrayList<String>();
		testSol2.add("+"); testSol2.add("+"); testSol2.add("+");
		testSol2.add("-"); testSol2.add("-"); testSol2.add("-"); 
		testSol2.add("*"); testSol2.add("*"); testSol2.add("*");

		
		Solution sol = new Solution(13);
		Solution sol2 = new Solution(13);
		
		sol.setSolution((ArrayList<String>) testSol);
		sol2.setSolution((ArrayList<String>) testSol2);
		
		
		ArrayList<Solution> solutionsTest = new ArrayList<Solution>();
		
		solutionsTest.add(sol);
		solutionsTest.add(sol2);
		
		
		evaluate(solutionsTest);
		

		System.out.println("----------------------------");
		ArrayList<Solution> mutated = new ArrayList<Solution>();
		mutated = mutatePopulation(solutionsTest);
		
		evaluate(mutated);
		
		
		System.out.println("____________________ \n"+getBest(solutionsTest).getEvaluation());
	}

}
