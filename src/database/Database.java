package database;

import java.util.HashMap;
import java.util.List;

import dbTypes.DBTypes;
import table.DBTable;

public class Database {
	HashMap<String, DBTable> tables;
	public Database(){
		tables = new HashMap<>();
	}
	
	public DBTable getTable(String tableName){
		return tables.get(tableName);
	}
	
	public void addTables(String tableName, DBTable dbTable){
		tables.put(tableName, dbTable);
	}

	public void addIndex(String indexName, String tableName, String columnName){
		tables.get(tableName).addIndex(indexName, columnName);
	}
	
	public void update(String tableName, String columnName, String valueClause, String whereClause  ){
		//TODO use misgar as value and whereClause parser for every table row (stupid... but otherwise we need function tensors)
	}
	
	public void deleteFrom(String tableName, String whereClause  ){
		//TODO 
	}
	
	public void selectFrom(String tableName,List<String> columnNames, String whereClause  ){
		//TODO 
	}
	
	public void insert(String tableName, List<DBTypes> values){
		tables.get(tableName).insertRow(values);
	}
}
