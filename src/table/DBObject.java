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
		
		//TODO: decide if this if is necessary 
		if(!dataSet.containsKey(fieldName))
			dataSet.put(fieldName, value);
	}
	
	public void updateField(String fieldName, DBTypes value){
//		if(dataSet.containsKey(fieldName))
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
	
	@Override
	public boolean equals(Object obj){
		if( !(obj instanceof DBObject) )
			return false;
		if( obj == this )
			return true;
		// TODO: check if we can have duplicates
		return false;
	}
}
