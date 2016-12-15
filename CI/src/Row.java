import java.util.ArrayList;
import java.util.Arrays;

public class Row {
	
	ArrayList<String> data = new ArrayList<String>();
	private Double expected = 0.0;
	
	public Row(String[] row){
		ArrayList<String> rowList = new ArrayList<String>(Arrays.asList(row)); 
		//System.out.println(rowList.toString());
		expected = Double.parseDouble(rowList.get(0));
    	rowList.remove(0);
		for(int i = 0; i<rowList.size();i++){
			data.add(rowList.get(i));
		}
	}
	
	public ArrayList<String> getRow(){
		return data;
	}
	
	public Double getExpected(){
		return expected;
	}
	
	public String toString(){
		String str = "\nExpected Value: "+expected+" \n Data: ";
		str+=data.toString();
		return str;
	}
	

}
