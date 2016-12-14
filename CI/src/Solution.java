import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Solution implements Cloneable {
	private final String[] acceptable = {"+","-","*"};
	private ArrayList<String> solution = new ArrayList<String>();
	private Double evaluation;
	private ArrayList<Integer> mutateShuffle = new ArrayList<Integer>();
	private int columns;
	
	public Solution(int columns){
		this.columns = columns;
		Random ran = new Random();
		for(int i = 0; i<columns-1;i++){
			int random = ran.nextInt(acceptable.length);
			solution.add(acceptable[random]);
			mutateShuffle.add(i);
		}
	}
	
	public Solution(ArrayList<String> premade){
		solution = premade;
	}
	
	public void changeRandomly(int index){
		Random ran = new Random();
		int random = ran.nextInt(acceptable.length);
		solution.set(index, acceptable[random]);

	}
	
	
	public ArrayList<String> getSolution(){
		return solution;
	}
	
	public Double getEvaluation(){
		return evaluation;
	}
	
	public void setEvaluation(Double e){
		evaluation = e;
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
