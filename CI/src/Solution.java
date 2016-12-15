import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Solution implements Cloneable, Comparable<Solution> {
	final String[] ACCEPTABLE = {"+","-","*"};
	private ArrayList<String> solution = new ArrayList<String>();
	private Double evaluation;
	private ArrayList<Integer> mutateShuffle = new ArrayList<Integer>();
	private int columns;

	Random ran = new Random();
	
	public Solution(int columns){
		this.columns = columns;
		
		for(int i = 0; i<columns-1;i++){
			int random = ran.nextInt(ACCEPTABLE.length);
			solution.add(ACCEPTABLE[random]);
			mutateShuffle.add(i);
		}
	}
	
	public Solution(ArrayList<String> premade){
		solution = premade;
	}
	
	public Solution(Double estimate){
		evaluation = estimate;
	}
	
	public int compareTo(Solution otherSol){
		Double fitness = Math.abs(evaluation);
		Double otherFitness = Math.abs(otherSol.getEvaluation());
		
		return fitness.compareTo(otherFitness);
	}
	
	public void changeRandomly(int index){

		int random = ran.nextInt(ACCEPTABLE.length);
		solution.set(index, ACCEPTABLE[random]);

	}
	
	
	public ArrayList<String> getSolution(){
		return solution;
	}
	
	public Double getEvaluation(){
		return evaluation;
	}
	
	public void setEvaluation(Double averageFitness){
		evaluation = averageFitness;
	}
	
	public String toString(){
		String string = "[ ";
		for(int i = 0; i<solution.size();i++){
			string += solution.get(i);
		}
		string += " ]";
		return string;
	}
	
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
	
	public void setSolution(ArrayList<String> sol){
		solution = sol;
	}
	
	
	public int getSize(){
		return solution.size();
	}
}
