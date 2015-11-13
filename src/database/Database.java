package database;

import java.util.HashMap;
import java.util.List;

import table.DBTable;
import table.DBtable;

public class Database {
	HashMap<String, DBTable> tables;
	public Database(){
		tables = new HashMap<>();
	}
	public void addTables(String tableName, DBTable dbTable){
		tables.put(tableName, dbTable);
	}

	public void addIndex(String indexName, String tableName, String columnName){
		//TODO
	}
	
	public void insert(String tableName ,List<Object> values){
		//TODO
	}
	
	public DBTable getTable(String tableName){
		return (DBTable)tables.get(tableName);	
	}
}
