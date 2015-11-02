package database;

import java.util.HashMap;

import table.DBTable;

public class Database {
	HashMap<String, DBTable> tables;
	
	public void addTables(String tableName, DBTable dbTable){
		tables.put(tableName, dbTable);
	}

}
