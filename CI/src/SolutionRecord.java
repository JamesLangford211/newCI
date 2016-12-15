import java.util.ArrayList;
/**
 * A class to represent a Solution a little more neatly, this could be implemented in the
 * Solution class but I wanted there to be a distinction between what is output
 * as a result and what is being used within the system.
 * @author James Langford
 *
 */


public class SolutionRecord {
	private ArrayList<String> actualFunction;
	private int foundAt;
	private double averageFitness;
	
	/**
	 * Constructor to create this object
	 * @param actualFunction Data structure for function
	 * @param foundAt generation it was found on 
	 * @param averageFitness average fitness.
	 */
	public SolutionRecord(ArrayList<String> actualFunction, int foundAt, double averageFitness){
		this.actualFunction = actualFunction;
		this.foundAt = foundAt;
		this.averageFitness = averageFitness;
	}
	
	/** 
	 * Will return a string detailing this solution.
	 */
	public String toString(){
		String returnString = "\n BEST RESULT:"
				+ "\n Function: " + actualFunction.toString()
				+ "\n Found at generation: " + foundAt
				+ "\n Average fitness: " + averageFitness + "\n";
		
		
		return returnString;
	}
	
	/**
	 * Get the data structure 
	 */
	public ArrayList<String> getActualFunction() {
		return actualFunction;
	}
	
	/**
	 * Set the data structure
	 * @param actualFunction
	 */
	public void setActualFunction(ArrayList<String> actualFunction) {
		this.actualFunction = actualFunction;
	}
	
	/**
	 * Get where this was found
	 * @return
	 */
	public int getFoundAt() {
		return foundAt;
	}

	/**
	 * Set where this record was found
	 * @param foundAt
	 */
	public void setFoundAt(int foundAt) {
		this.foundAt = foundAt;
	}

	/**
	 * Get the average fitness of this solution
	 * @return
	 */
	public double getAverageFitness() {
		return averageFitness;
	}

	/**
	 * Set the average fitness of this solution
	 * @param averageFitness
	 */
	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}


	
}
