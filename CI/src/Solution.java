import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Solution implements Cloneable, Comparable<Solution> {
	//Final string of the only acceptable operators this can hold.
	final String[] ACCEPTABLE = {"+","-","*"};
	//Data structure to represent the solution this represents
	private ArrayList<String> solution = new ArrayList<String>();
	//Double to hold the current evaluation of this object
	private Double evaluation;
	private ArrayList<Integer> mutateShuffle = new ArrayList<Integer>();
	private int columns;

	Random ran = new Random();
	
	/**
	 * Constructor for a new solution for when you know the amount of columns you need to make 
	 * @param columns
	 */
	public Solution(int columns){
		this.columns = columns;
		
		for(int i = 0; i<columns-1;i++){
			int random = ran.nextInt(ACCEPTABLE.length);
			solution.add(ACCEPTABLE[random]);
			mutateShuffle.add(i);
		}
	}
	
	/**
	 * Constructor for making a Solution object from a pre-defined ArrayList
	 * @param premade
	 */
	public Solution(ArrayList<String> premade){
		solution = premade;
	}

	/**
	 * Used to compare this objects fitness to anothers
	 */
	public int compareTo(Solution otherSol){
		Double fitness = Math.abs(evaluation);
		Double otherFitness = Math.abs(otherSol.getEvaluation());
		
		return Math.abs(fitness.compareTo(otherFitness));
	}
	
	/**
	 * Will randomly select an index of the array and change it to something unexpected.
	 * @param index
	 */
	public void changeRandomly(int index){

		int random = ran.nextInt(ACCEPTABLE.length);
		solution.set(index, ACCEPTABLE[random]);

	}
	
	
	/**
	 * Return the data strucutre that holds this solution
	 * @return
	 */
	public ArrayList<String> getSolution(){
		return solution;
	}
	
	/**
	 * Return the double that holds the fitness for this solution
	 * @return
	 */
	public Double getEvaluation(){
		return evaluation;
	}
	
	/**
	 * Pass in a value to define the fitness field for this object
	 * @param averageFitness
	 */
	public void setEvaluation(Double averageFitness){
		evaluation = averageFitness;
	}
	
	/**
	 * Returns a string detailing this object
	 */
	public String toString(){
		String string = "[ ";
		for(int i = 0; i<solution.size();i++){
			string += solution.get(i);
		}
		string += " ]";
		return string;
	}
	
	/**
	 * Returns an exact copy of this object at a different memory location
	 */
	public Solution clone(){
		final Solution clone;
		try{
			clone = (Solution) super.clone();
		}
		catch(CloneNotSupportedException e){
			throw new RuntimeException("Cannot clone",e);
		}
		clone.setEvaluation(this.getEvaluation());
		clone.setSolution(this.getSolution());
		return clone;
	}
	
	/**
	 * Method to set the value of the data structure that holds this solution
	 * @param sol
	 */
	public void setSolution(ArrayList<String> sol){
		solution = sol;
	}
	
	/**
	 * Returns an integer holding the size of the data structure represeing this Solution
	 * @return
	 */
	public int getSize(){
		return solution.size();
	}
}
