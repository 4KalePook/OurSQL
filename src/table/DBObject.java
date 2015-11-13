package table;
import java.util.HashMap;
import java.util.List;

import dbTypes.DBTypes;

public class DBObject {
	private HashMap<String, DBTypes> dataSet;
	public DBObject(HashMap<String, DBTypes> dataSet) {
		this.dataSet = dataSet;
	}
	
	public DBObject() {
		this.dataSet = new HashMap<String, DBTypes>();
	}
	
	public void insertRow(List<DBTypes> dataSet){
		//TODO: call garbage COllector on this.dataSet?!
//		this.dataSet = dataSet;
	}
	
	public void insertField(String fieldName, DBTypes value){
		//TODO: maybe change db schema!
		if(dataSet.containsKey(fieldName))
			dataSet.put(fieldName, value);
	}
	
	public DBTypes getField(String fieldName){
		//TODO: maybe fieldName not exist!
		if(dataSet.containsKey(fieldName))
			return dataSet.get(fieldName);
		return null;
	}
	
	public HashMap<String, DBTypes> getRow(){
		return dataSet;
	}
}
