import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import com.fathzer.soft.javaluator.DoubleEvaluator;

public class Engine {
	
	// Variables to store URLs for files to pass in.
	private static final String TRAIN_URL = "src/cwk_train.csv";
	private static final String TEST_URL = "src/cwk_test.csv";
	
	// Parameters for Evolutionary Algorithm
	private boolean ELITISM = false;
	private static final int GENERATIONS = 100;
	private static final int POPULATION = 1000;
	private static final int SUB_POPULATION = (int) (POPULATION * 0.05);
	private static final int CROSSOVER_METHOD = 2;
	private static final double MUTATION_PROBABILITY = 0.3;
	private static final int SUBPOP_INTERVAL = (int) (POPULATION * 0.20);
	
	// ArrayLists to store population and data.
	private ArrayList<Row> dataSet = new ArrayList<Row>();
	private ArrayList<Row> testSet = new ArrayList<Row>();
	private ArrayList<Solution> population = new ArrayList<Solution>();
	
	// Creating an instance of javaluator.
	private DoubleEvaluator evaluator = new DoubleEvaluator();
	
	private Solution overallBest = null;
	
	private int duplicateIteration = 0;
	
	/**
	 * 
	 */
	public Engine(){
		dataSet = popDataTable(TRAIN_URL);
		testSet = popDataTable(TEST_URL);
		population = initialisation(13);		
		
		for(int i = 0; i<GENERATIONS; i++){
			evaluate(population);
			Solution best = getBest(population);
			if(overallBest == null || Math.abs(best.getEvaluation()) < Math.abs(overallBest.getEvaluation())){
				overallBest = best.clone();
			}
			
			if(bestIsSame(best, overallBest)){
				duplicateIteration++;
			}
			else{
				duplicateIteration = 0;
			}
			System.out.println(best.getEvaluation() + " :-:  \n "
					+ "OVERALL-BEST: "+overallBest.getEvaluation() + "\n"
							+ ELITISM +" \n"
									+ duplicateIteration
							+ "************");
			
			ArrayList<Solution> subPop = getSubPopulation(population,SUB_POPULATION);
			ArrayList<Solution> winners = getWinners(subPop);
			ArrayList<Solution> newPopulation = newPopulation(winners);
			ArrayList<Solution> mutated = mutatePopulation(newPopulation);
			//mutated.add(best);
	
			
			population.clear();
			population = (ArrayList<Solution>) mutated.clone();
			
			if(i == GENERATIONS/2){;
				ELITISM = true;
			}
		}
		
		System.out.println("\n *************************** \n"
				+ "Best at end: " + overallBest + "\n *************************** \n");
			
		applyToTest(overallBest);
	}
	
	public void applyToTest(Solution best){
		for(){
			// TODO: uhefiuehiuehiuhes
		}
	}
	
	public boolean bestIsSame(Solution one, Solution two){
		return one.getEvaluation() == two.getEvaluation();
	}
	
	public ArrayList<Solution> newPopulation(ArrayList<Solution> winners){
		ArrayList<Solution> newPopulation = new ArrayList<>();
		Random r = new Random();
		for(int i = 0; i<POPULATION; i++){
			int random = r.nextInt(winners.size());
			Solution toAdd = winners.get(random).clone();
			newPopulation.add(toAdd);
		}
		return newPopulation;
	}
	
	public ArrayList<Solution> getWinners(ArrayList<Solution> subPop){
		ArrayList<Solution> winners = new ArrayList<>();
		for(int i = 0; i<subPop.size(); i++){
			if((i+1) != subPop.size()){
				winners.add(tournament(subPop.get(i),subPop.get(i+1)).clone());
			}
		}
		return winners;
	}
	
	public ArrayList<Solution> getSubPopulation(ArrayList<Solution> fullPopulation, int size){
		ArrayList<Solution> subPopulation = new ArrayList<Solution>();
		
		int interval = SUBPOP_INTERVAL;
		int place = 0;
		for(int i = 0; i<size; i++){
			place = ((place+interval) % fullPopulation.size());
			subPopulation.add(fullPopulation.get(place).clone());
			fullPopulation.remove(fullPopulation.get(place));
		}
		return subPopulation;
	}
	
	/**
	 * 
	 * @param solutions
	 * @return
	 */
	public Solution getBest(ArrayList<Solution> solutions){
		Solution best = null;
		for(int i = 0; i<solutions.size();i++){
			if(best == null || Math.abs(solutions.get(i).getEvaluation()) < Math.abs(best.getEvaluation())){
				best = solutions.get(i);
			}
		}
		return best;
	}
	
	/**
	 * 
	 * @param operandsInData
	 * @return
	 */
	public ArrayList<Solution> initialisation(int operandsInData){
		ArrayList<Solution> solutions = new ArrayList<Solution>();
		for(int i = 0; i<POPULATION; i++){
			solutions.add(new Solution(operandsInData));
		}
		return solutions;
	}
	
	/**
	 * 
	 * @param data
	 * @param solution
	 * @return
	 */
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
	
	/**
	 * 
	 * @param url
	 * @return
	 */
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
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Row> popTestSet(){
		ArrayList<Row> returnSet = new ArrayList<Row>();
		for(int i = 2; i<5; i++){
			testSet.add(dataSet.get(i));
		}
		return testSet;
	}
	
	/**
	 * 
	 * @param solutions
	 */
	public void evaluate(ArrayList<Solution> solutions){

				for(int i = 0; i<solutions.size(); i++){
					Double distanceAway = 0.0;
					for(int j = 0; j<dataSet.size(); j++){
						Double evaluated = evaluator.evaluate(listToString(
								applyDataToFunction(
								dataSet.get(j),solutions.get(i))));
						Double expected = dataSet.get(j).getExpected();
						Double extra = expected - evaluated;
						distanceAway += extra;
						//System.out.println(expected+" :: "+evaluated+" :: " +extra+" :: "+listToString(applyDataToFunction(testSet.get(j),functions.get(i))));
						}
					
					distanceAway = distanceAway/testSet.size();
					solutions.get(i).setEvaluation(distanceAway);
					//System.out.println(solutions.get(i).getSolution().toString() + " :: " + solutions.get(i).getEvaluation());
				}
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public String listToString(ArrayList<String> string){
		String retStr = "";
		
		for(int i = 0; i<string.size(); i++){
			retStr += string.get(i);
		}
		return retStr;
	}
	
	/**
	 * 
	 * @param toMutate
	 * @return
	 */
	private ArrayList<Solution> mutatePopulation(ArrayList<Solution> toMutate){
		ArrayList<Solution> mutated = new ArrayList<Solution>();
		for(int i = 0; i<toMutate.size(); i++){
			Random r = new Random();
			Double result = r.nextDouble();
			if(result<MUTATION_PROBABILITY){
				int random = r.nextInt(toMutate.get(i).getSize());
				mutated.add(toMutate.get(i).clone());
				mutated.get(i).changeRandomly(random);
			}
			else{
				mutated.add(toMutate.get(i).clone());
			}
			
		}
		return mutated;
	}
	
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private Solution uniformCrossOver(Solution parent1, Solution parent2){
		//System.out.println("uniform");
		ArrayList<String> parent1String = parent1.getSolution();
		ArrayList<String> parent2String = parent2.getSolution();
		
		//System.out.println("_-_-_-_- Uniform Crossover: _-_-_-_-_");
		//System.out.println(parent1.toString());
		//System.out.println(parent2.toString());

		Random r = new Random();
		ArrayList<String> childString = new ArrayList<String>();
		for(int i = 0; i<parent1.getSize();i++){
			if(r.nextBoolean()){
				childString.add(parent1String.get(i));
			}
			else{
				childString.add(parent2String.get(i));
			}
		}
		Solution child = new Solution(childString);
		//System.out.println(child.toString());
		return child;
	}
	
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution onePointCrossOver(Solution parent1, Solution parent2){
		//System.out.println("onePoint");
		Random r = new Random();
		int crossover = r.nextInt(parent1.getSize());
		ArrayList<Solution> potentialChildren = new ArrayList<>();
		ArrayList<String> child1 = new ArrayList<>();
		ArrayList<String> child2 = new ArrayList<>();
		child1.addAll(parent1.getSolution().subList(0, crossover));
		child1.addAll(parent2.getSolution().subList(crossover, parent2.getSize()));
		child2.addAll(parent2.getSolution().subList(0, crossover));
		child2.addAll(parent1.getSolution().subList(crossover, parent2.getSize()));
		
		Solution c1 = new Solution(child1);
		Solution c2 = new Solution(child2);
		ArrayList<Solution> childrenSolutions = new ArrayList<>();
		childrenSolutions.add(c1);
		childrenSolutions.add(c2);
		evaluate(childrenSolutions);
		
		//System.out.println(child1.toString() + " :: " + crossover + " :: " + c1.getEvaluation());
		//System.out.println(child2.toString() + " :: " + crossover + " :: " + c2.getEvaluation());
		
		//System.out.println(getBest(childrenSolutions).getEvaluation());
	
		return getBest(childrenSolutions);
	}
	
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution arithmeticCrossOver(Solution parent1, Solution parent2){
		//System.out.println("arithmetic");
		final String[] chars = parent1.ACCEPTABLE;
		HashMap<String,Integer> charMap = new HashMap<>();
		charMap.put(chars[0],0);
		charMap.put(chars[1],1);
		charMap.put(chars[2],2);
		
		//System.out.println(parent1.getSolution());
		//System.out.println(parent2.getSolution());
		
		//Sum together values of two parents
		ArrayList<String> child = new ArrayList<>();
		Random r = new Random();
		int randomInterval = r.nextInt(9);
		for(int i = 0; i<parent1.getSize(); i++){
			int cellVal = (((charMap.get(parent1.getSolution().get(i)) + charMap.get(parent2.getSolution().get(i)))+randomInterval) % 3);
			child.add(chars[cellVal]);
		}
		Solution childSolution = new Solution(child);
		//System.out.println(childSolution.getSolution());
		return childSolution;
	}
	
	/**
	 * 
	 * @param method
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution crossover(int method, Solution parent1, Solution parent2){
		if(method == 1){
			return uniformCrossOver(parent1, parent2);
		}
		else if(method == 2){
			return onePointCrossOver(parent1, parent2);
		}
		else{
			return arithmeticCrossOver(parent1, parent2);
		}
	}
	
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution tournament(Solution parent1, Solution parent2){
		Solution child = crossover(CROSSOVER_METHOD, parent1, parent2);
		ArrayList<Solution> tournament = new ArrayList<>();
		tournament.add(parent1);
		tournament.add(parent2);
		tournament.add(child);
		evaluate(tournament);
		
	/*	System.out.println("Parent 1: "+parent1.getSolution().toString()+" :: "+parent1.getEvaluation());
		System.out.println("Parent 2: "+parent2.getSolution().toString()+" :: "+parent2.getEvaluation());
		System.out.println("Child   : "+child.getSolution().toString()+" :: "+child.getEvaluation());
		System.out.println("Winner  : "+getBest(tournament).toString()+" :: "+getBest(tournament).getEvaluation());*/
	
		return getBest(tournament);
		
	}
	
	/**
	 * 
	 */
	public void test(){
		ArrayList<String> testSol = new ArrayList<String>();
		testSol.add("+"); testSol.add("-"); testSol.add("*"); testSol.add("+");
		testSol.add("+"); testSol.add("-"); testSol.add("*"); testSol.add("+");
		testSol.add("+"); testSol.add("-"); testSol.add("*"); testSol.add("+");
		
		ArrayList<String> testSol2 = new ArrayList<String>();
		testSol2.add("*"); testSol2.add("*"); testSol2.add("*"); testSol2.add("+");
		testSol2.add("-"); testSol2.add("-"); testSol2.add("-"); testSol2.add("+");
		testSol2.add("+"); testSol2.add("+"); testSol2.add("+"); testSol2.add("+");

		
		Solution sol = new Solution(13);
		Solution sol2 = new Solution(13);
		
		sol.setSolution((ArrayList<String>) testSol);
		sol2.setSolution((ArrayList<String>) testSol2);
		
		
		ArrayList<Solution> solutionsTest = new ArrayList<Solution>();
		
		solutionsTest.add(sol);
		solutionsTest.add(sol2);
		
		
		evaluate(solutionsTest);
		

		System.out.println("----------------------------");
		//ArrayList<Solution> mutated = new ArrayList<Solution>();
		//mutated = mutatePopulation(solutionsTest);
		
		//evaluate(mutated);
			
		
		//System.out.println("____________________ \n"+getBest(mutated).getEvaluation());
		
		//uniformCrossOver(mutated.get(0),mutated.get(1));
		//onePointCrossOver(solutionsTest.get(0),solutionsTest.get(1));
		//arithmeticCrossOver(solutionsTest.get(0),solutionsTest.get(1));
		
		tournament(solutionsTest.get(0),solutionsTest.get(1));
		
	}

}
