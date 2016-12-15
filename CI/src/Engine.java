import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.fathzer.soft.javaluator.DoubleEvaluator;

/**
 * A class to model the process(es) a genetic algorithm will conduct.
 * References the javaluator library; can be found at: http://javaluator.sourceforge.net/en/home/ Date Accessed: 1/12/2016
 * @author James Langford
 *
 */
public class Engine {
	
	// Variables to store URLs for files to pass in.
	private static final String TRAIN_URL = "src/cwk_train.csv";
	private static final String TEST_URL = "src/cwk_test.csv";
	
	// Parameters for Evolutionary Algorithm
	private boolean ELITISM = false;
	private static final int GENERATIONS = 1000; // Recommended:
	private static final int POPULATION = 200; // Recommended: 
	private static final int SUB_POPULATION = 100; // Recommended:
	private static final int CROSSOVER_METHOD = 3; // Recommended: 
	private static final double MUTATION_PROBABILITY = 0.2; //Recomended: 
	
	// ArrayLists to store population and data.
	private ArrayList<Row> trainSet = new ArrayList<Row>();
	private ArrayList<Row> testSet = new ArrayList<Row>();
	private ArrayList<Solution> population = new ArrayList<Solution>();
	
	// Creating an instance of javaluator
	private DoubleEvaluator evaluator = new DoubleEvaluator();

	//The overall best solution found this run.
	private Solution overallBest = null;
	//A record class object to hold information based on solution
	private SolutionRecord bestRecord = null;
	
	/**
	 * Constructor for the Engine class, calls the core of the evolutionary algorithm
	 */
	public Engine(){
		//Populate data sets
		trainSet = popDataTable(TRAIN_URL);
		testSet = popDataTable(TEST_URL);
		population = initialisation(13);		
		
		for(int i = 0; i<GENERATIONS; i++){
			//evaluate the population
			population = evaluate(population, trainSet);
			
			//return the best for this iteration only
			Solution best = getBest(population);
			
			System.out.println("GENERATION "+i);
			
			//if the current best is better than overallBest - update overallBest!
			if(overallBest == null || Math.abs(best.getEvaluation()) < Math.abs(overallBest.getEvaluation())){
				overallBest = best.clone();
				overallBest.setEvaluation(best.getEvaluation());
				overallBest.setSolution(best.getSolution());
				bestRecord = new SolutionRecord(overallBest.getSolution(),i,overallBest.getEvaluation());
				System.out.println(bestRecord.toString());
			}		
			
			//Get a sub population via the technique proposed in this method and it will retun the amount you specify.
			ArrayList<Solution> subPop = getSubPopulation(population,SUB_POPULATION);
			
			//Enures the best has been added if this evaluates to be true.
			if(ELITISM){
				Random r = new Random();
				int random = r.nextInt(subPop.size());
				subPop.remove(random);
				subPop.add(best.clone());			
			}
			
			//Get the winners of the combination and tournament stage
			ArrayList<Solution> winners = getWinners(subPop);
			//Assign these winners to slots in the new population
			ArrayList<Solution> newPopulation = newPopulation(winners);
			//Iterate through and mutate the population
			population = mutatePopulation(newPopulation);	
		}
	
		System.out.println(parameters());
		
	} 
	/**
	 * In essence, a toString method for the parameters of the system.
	 * 	 * @return A string holding the parameter values
	 */
	public String parameters(){
		String returnStr = "";
				
		String crossover = "";
		
		switch (CROSSOVER_METHOD) {
        case 1:  crossover = "Uniform Crossover";
                break;
        case 2:  crossover = "One point Crossover";
                break;
        case 3:  crossover = "Arithemetic Crossover";
        		break;
		}
    
		returnStr+= "GENERATIONS: " + GENERATIONS+". \n"
				+ "POPULATION: " + POPULATION+". \n"
				+ "SUB_POPULATION: " + SUB_POPULATION+". \n"
				+ "CROSSOVER: " + crossover+". \n"
				+ "MUTATION PROBABILITY: " + MUTATION_PROBABILITY+". \n"
						+ "ELITISM: "+ELITISM;
		
		return returnStr;
	}
	
	/**
	 * A method to test out an individual function with the test results.
	 * @param best pass in the solution to test
	 */
	public void applyToTest(Solution best){		
		ArrayList<Solution> s = new ArrayList();
		s.add(best.clone());
		s = evaluate(s,testSet);
		System.out.println(s.get(0).getEvaluation());
	}
		
	/**
	 * Create a 'dummy' population filled with random selections from the winners array
	 * @param winners an array of tournament winners
	 * @return a new population
	 */
	public ArrayList<Solution> newPopulation(ArrayList<Solution> winners){
		ArrayList<Solution> newPopulation = new ArrayList<>();
		Random r = new Random();
		for(int i = 0; i<POPULATION; i++){
			int random = r.nextInt(winners.size());
			Solution toAdd = winners.get(random);
			newPopulation.add(toAdd.clone());
		}
		return newPopulation;
	}
	
	/**
	 * A method that takes in a population, and tournaments each two of them, in-line with the requirements for the tournament method parameters.
	 * @param subPop a subPopulation to tournament 
	 * @return
	 */
	public ArrayList<Solution> getWinners(ArrayList<Solution> subPop){
		
		if(subPop.size() % 2 != 0){
			subPop.remove(subPop.size()-1);
		}
		ArrayList<Solution> winners = new ArrayList<>();
		for(int i = 0; i<subPop.size(); i+=2){
			if((i+1) != subPop.size()){
				winners.add(tournament(subPop.get(i),subPop.get(i+1)).clone());
			}
		}
		return winners;
	}
	
	/**
	 * Returns a subList of the full population of size -> size.
	 * @param fullPopulation
	 * @param size
	 * @return
	 */
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
	 *  Returns the best solution from an arrayList<Solution>
	 * @param solutions
	 * @return
	 */
	public Solution getBest(ArrayList<Solution> solutions){
		ArrayList<Solution> sort = new ArrayList();
		sort = solutions;
		Collections.sort(sort);
		Solution best = sort.get(0).clone();	
		return best;
	}
	
	/**
	 * Creates a collection of random functions.
	 * @param operandsInData
	 * @return
	 */
	public ArrayList<Solution> initialisation(int operandsInData){
		ArrayList<Solution> solutions = new ArrayList<Solution>();
		//make x random to fill population
		for(int i = 0; i<POPULATION; i++){
			//make a random solution
			solutions.add(new Solution(operandsInData));
		}
		return solutions;
	}
	
	/**
	 * Takes a row of data and a solution and formats it for example in ArrayForm:
	 * A+B+C+D etc...
	 * @param data
	 * @param solution
	 * @return
	 */
	public ArrayList<String> applyDataToFunction(Row data, Solution solution){
		ArrayList<String> newSolution = new ArrayList<String>();
		
		for(int i = 0; i<data.getRow().size(); i++){
			//fetch data to add
			newSolution.add(data.getRow().get(i));
			if(i<solution.getSolution().size()){
				newSolution.add(solution.getSolution().get(i));
			}
		}
		
		return newSolution;
	}
	
	/**
	 * Populates the data table with information from the train.csv
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
	 * Populates the data table with information from the test.csv
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
	 * For every function in the array, compares it to every row in the dataset and saves that value back to the returning set
	 * @param solutions
	 */
	public ArrayList<Solution> evaluate(ArrayList<Solution> solutions, ArrayList<Row> dataSet){
		ArrayList<Solution> returnArray = new ArrayList();
		Double totalFitness = 0.0;
		for(int j = 0; j<solutions.size(); j++){
			for(int i = 0; i<dataSet.size();i++){
				Double fitness = 0.0;

				ArrayList<String> expression = applyDataToFunction(dataSet.get(i),solutions.get(j));
				String expressionStr = listToString(expression);			
				Double expected = dataSet.get(i).getExpected();
				Object result = null;
				
				//Double evaluated = Double.valueOf(result.toString());
				
				Double evaluated = evaluator.evaluate(expressionStr);
				fitness = evaluated - expected;
				//show fitness for that row
				totalFitness += Math.abs(fitness);
				//add up average fitess
			}
			Double averageFitness = totalFitness/dataSet.size();

			
			Solution s = new Solution(solutions.get(j).getSolution());
			s.setEvaluation(averageFitness);
			
			returnArray.add(s);	
	}
		return returnArray;
}
	
	
	/**
	 * Takes an arrayList of strings and outputs it in string form 
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
	 * Returns a Collection of mutate population
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
				Solution m = new Solution(toMutate.get(i).getSolution());
				m.changeRandomly(random);
				mutated.add(m.clone());
			}
			
			else{
				mutated.add(toMutate.get(i).clone());
			}
		}
		
		mutated = evaluate(mutated,trainSet);
		return mutated;
	}
	
	/**
	 * Takes two parents and returns a child who is the result of unfiorm crossover
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private Solution uniformCrossOver(Solution parent1, Solution parent2){
		ArrayList<String> parent1String = parent1.getSolution();
		ArrayList<String> parent2String = parent2.getSolution();

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
	 * Takes two parents and returns a child who is the result of one point crossover
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution onePointCrossOver(Solution parent1, Solution parent2){

		Random r = new Random();
		int crossover = r.nextInt(parent1.getSize());
		ArrayList<Solution> potentialChildren = new ArrayList<>(); //arrayList for evaluate method
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
		childrenSolutions = evaluate(childrenSolutions,trainSet);
		
		//System.out.println(child1.toString() + " :: " + crossover + " :: " + c1.getEvaluation());
		//System.out.println(child2.toString() + " :: " + crossover + " :: " + c2.getEvaluation());
		
		//System.out.println(getBest(childrenSolutions).getEvaluation());
	
		//System.out.println("CHILDREN------------");
		return getBest(childrenSolutions);
	}
	
	/**
	 * Takes two parents and returns a child who is the result of arithmetic crossover
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
		
		//Sum together values of two parents
		ArrayList<String> child = new ArrayList<>();
		Random r = new Random();
		int randomInterval = r.nextInt(9);
		for(int i = 0; i<parent1.getSize(); i++){
			int cellVal = (((charMap.get(parent1.getSolution().get(i)) + charMap.get(parent2.getSolution().get(i)))+randomInterval) % 3);
			child.add(chars[cellVal]);
		}
		Solution childSolution = new Solution(child);
		return childSolution;
	}
	
	/**
	 * Redirects user to use the crossover method of their choice
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
	 * Returns the winner of a tournament between the two parents passed in
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	public Solution tournament(Solution parent1, Solution parent2){
		Solution child = crossover(CROSSOVER_METHOD, parent1, parent2);
		ArrayList<Solution> tournament = new ArrayList<>();
		tournament.add(parent1.clone());
		tournament.add(parent2.clone());
		tournament.add(child.clone());
		tournament = evaluate(tournament,trainSet);
		
		return getBest(tournament);
		
	}
	

}
