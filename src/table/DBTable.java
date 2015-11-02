package table;

import java.util.HashMap;
import java.util.LinkedList;

import dbTypes.DBTypes;
import parser.CreateTableType;

public class DBTable {
	LinkedList<DBObject> tableObjects;
//	HashMap<String, DBTypes> schema; //HashMap<String, DBTypes> DBObject = new HashMap<String, DBTypes>(schema);
	CreateTableType createTable;
	
	public DBTable(CreateTableType createTable) {
		tableObjects = new LinkedList<>();
		this.createTable = createTable;
		System.out.println(createTable.getSchema());
		System.out.println(createTable.getTableName());
	}
	
//	public void makeSchema(HashMap<String, DBTypes> schema){
//		this.schema = schema;
//	}
	
	public void DBTableInsertRow(String key, String value)
	{ 
		;
	}
	
	public void DBTableGet()
	{
		;
	}
}
