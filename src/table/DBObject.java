package table;
import java.util.HashMap;

import dbTypes.DBTypes;

class DBObject {
	private HashMap<String, DBTypes> dataSet;
	public DBObject(HashMap<String, DBTypes> dataSet) {
		this.dataSet = dataSet;
	}
	
	public void insertRow(HashMap<String, DBTypes> dataSet){
		//TODO: call garbage COllector on this.dataSet?!
		this.dataSet = dataSet;
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
