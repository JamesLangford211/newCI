import java.util.ArrayList;
import java.util.Arrays;
/**
 * Row
 * A class to hold a row of a data table as recrds
 * @author James Langford
 *
 */
public class Row {
	
	//A record to hold the data this row represents
	ArrayList<String> data = new ArrayList<String>();
	//The first cell in every row is the expected and will be stored here before being removed
	private Double expected = 0.0;
	
	/**
	 * Constructor method for a row where the array has already been defined.
	 * @param row
	 */
	public Row(String[] row){
		ArrayList<String> rowList = new ArrayList<String>(Arrays.asList(row)); 
		expected = Double.parseDouble(rowList.get(0));
    	rowList.remove(0);
		for(int i = 0; i<rowList.size();i++){
			data.add(rowList.get(i));
		}
	}
	
	/**
	 * 	Method to return the array this object holds
	 * @return
	 */
	public ArrayList<String> getRow(){
		return data;
	}
	
	/**
	 * Method to get the expected value of this function
	 * @return
	 */
	public Double getExpected(){
		return expected;
	}
	
	/**
	 * Will return a string detailing information about this object.
	 */
	public String toString(){
		String str = "\nExpected Value: "+expected+" \n Data: ";
		str+=data.toString();
		return str;
	}
	

}
