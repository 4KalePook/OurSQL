package database;

import java.util.HashMap;
import java.util.List;

import table.DBTable;

public class Database {
	HashMap<String, DBTable> tables;
	public Database(){
		tables = new HashMap<>();
	}
	public void addTables(String tableName, DBTable dbTable){
		tables.put(tableName, dbTable);
	}

	public void addIndex(String indexName, String tabelName, String columnName){
		//TODO
	}
	
	public void insert(String tabelName ,List<Object> values){
		//TODO
	}
}
