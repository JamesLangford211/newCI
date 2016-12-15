import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final int POPULATION = 200;
	private static final int SUB_POPULATION = 35;
	private static final int CROSSOVER_METHOD = 2;
	private static final double MUTATION_PROBABILITY = 0.2;
	
	// ArrayLists to store population and data.
	private ArrayList<Row> trainSet = new ArrayList<Row>();
	private ArrayList<Row> testSet = new ArrayList<Row>();
	private ArrayList<Solution> population = new ArrayList<Solution>();
	
	// Creating an instance of javaluator.
	private DoubleEvaluator evaluator = new DoubleEvaluator();
	
	private Solution overallBest = null;
	private FunctionRecord bestRecord = null;
	
	//private int duplicateIteration = 0;
	
	/**
	 * 
	 */
	public Engine(){
		trainSet = popDataTable(TRAIN_URL);
		testSet = popDataTable(TEST_URL);
		population = initialisation(13);		
		
		for(int i = 0; i<GENERATIONS; i++){
			evaluate(population, trainSet);
			Solution best = getBest(population);
			
			if(overallBest == null || Math.abs(best.getEvaluation()) < Math.abs(overallBest.getEvaluation())){
				overallBest = best.clone();
				bestRecord = new FunctionRecord(overallBest.getSolution(),i,overallBest.getEvaluation());
			}
			
			ArrayList<Solution> subPop = getSubPopulation(population,SUB_POPULATION);
			ArrayList<Solution> winners = getWinners(subPop);
			ArrayList<Solution> newPopulation = newPopulation(winners);
			ArrayList<Solution> mutated = mutatePopulation(newPopulation);
			//evaluate(mutated,dataSet);
			//mutated.add(best);
			population.clear();
			population = (ArrayList<Solution>) mutated.clone();
			
			System.out.println("Iteration: " + i + "/" + GENERATIONS + " : OB: "+overallBest.getEvaluation()+ " : IB: "+ best.getEvaluation());	
			
		}
				
		System.out.println(bestRecord.toString());
		System.out.println("------ Applying to TEST set of data ------");
		applyToTest(overallBest);
	} 
	
	public void applyToTest(Solution best){		
		ArrayList<Solution> s = new ArrayList();
		s.add(best.clone());
		evaluate(s,testSet);
		System.out.println(s.get(0).getEvaluation());
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

		Random r = new Random();
		for(int i = 0; i<SUB_POPULATION;i++){
			int random = r.nextInt(fullPopulation.size());
			subPopulation.add(fullPopulation.get(random).clone());
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
			//System.out.println("*************");
			//System.out.print(solutions.get(i).getEvaluation());
			//System.out.println("  "+solutions.get(i).getSolution().toString());
			if(best == null || Math.abs(solutions.get(i).getEvaluation()) < Math.abs(best.getEvaluation())){
				
				best = solutions.get(i);
				//System.out.print("NEW BEST:"+best.getEvaluation());
				//System.out.println("  "+best.getSolution().toString());
			}
		}
		//System.out.print(best.getEvaluation());
		//System.out.println("  "+best.getSolution().toString());
		
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
	        	//System.out.println(Arrays.toString(lineSplit));
	        	dataTable.add(new Row(lineSplit));  	
		}
		} catch(FileNotFoundException e){
		}		
		//System.out.println(dataTable.toString());
		return dataTable;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Row> popTestSet(){
		ArrayList<Row> returnSet = new ArrayList<Row>();
		for(int i = 2; i<5; i++){
			testSet.add(trainSet.get(i));
		}
		return testSet;
	}
	
	/**
	 * 
	 * @param solutions
	 */
	public void evaluate(ArrayList<Solution> solutions, ArrayList<Row> dataSet){
		for(int j = 0; j<solutions.size(); j++){
			Double totalFitness = 0.0;
			for(int i = 0; i<dataSet.size();i++){
				Double fitness = 0.0;

				ArrayList<String> expression = applyDataToFunction(dataSet.get(i),solutions.get(j));
				String expressionStr = listToString(expression);

				
				Double expected = dataSet.get(i).getExpected();
				Double evaluated = evaluator.evaluate(expressionStr);
				fitness = evaluated - expected;
				
				//show fitness for that row
				totalFitness += Math.abs(fitness);
				//add up average fitess
			}
			//System.out.println(dataSet.size());
			Double averageFitness = totalFitness/dataSet.size();
			//System.out.println("Avg Fitness" + averageFitness);
			solutions.get(j).setEvaluation(averageFitness);
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
		evaluate(childrenSolutions,trainSet);
		
		//System.out.println(child1.toString() + " :: " + crossover + " :: " + c1.getEvaluation());
		//System.out.println(child2.toString() + " :: " + crossover + " :: " + c2.getEvaluation());
		
		//System.out.println(getBest(childrenSolutions).getEvaluation());
	
		//System.out.println("CHILDREN------------");
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
		evaluate(tournament,trainSet);
		
	/*	System.out.println("Parent 1: "+parent1.getSolution().toString()+" :: "+parent1.getEvaluation());
		System.out.println("Parent 2: "+parent2.getSolution().toString()+" :: "+parent2.getEvaluation());
		System.out.println("Child   : "+child.getSolution().toString()+" :: "+child.getEvaluation());
		System.out.println("Winner  : "+getBest(tournament).toString()+" :: "+getBest(tournament).getEvaluation());*/
	
		//System.out.println("CHILDREN------------");
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
		
		
		evaluate(solutionsTest,trainSet);
		

		//System.out.println("----------------------------");
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
