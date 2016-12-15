import java.util.ArrayList;

public class SolutionRecord {
	private ArrayList<String> actualFunction;
	private int foundAt;
	private double averageFitness;
	
	public SolutionRecord(ArrayList<String> actualFunction, int foundAt, double averageFitness){
		this.actualFunction = actualFunction;
		this.foundAt = foundAt;
		this.averageFitness = averageFitness;
	}
	
	public String toString(){
		String returnString = "\n BEST RESULT:"
				+ "\n Function: " + actualFunction.toString()
				+ "\n Found at generation: " + foundAt
				+ "\n Average fitness: " + averageFitness + "\n";
		
		
		return returnString;
	}
	
	public ArrayList<String> getActualFunction() {
		return actualFunction;
	}

	public void setActualFunction(ArrayList<String> actualFunction) {
		this.actualFunction = actualFunction;
	}

	public int getFoundAt() {
		return foundAt;
	}

	public void setFoundAt(int foundAt) {
		this.foundAt = foundAt;
	}

	public double getAverageFitness() {
		return averageFitness;
	}

	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}


	
}
