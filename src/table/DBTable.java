package table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dbTypes.DBTypes;
import parser.CreateTableType;

public class DBTable {
	LinkedList<DBObject> tableObjects;
	HashMap<String, TableIndex> indices;
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
	
	public void DBTableInsertRow(DBObject row)
	{ 
		tableObjects.add(row);
		for(String indexName: indices.keySet()) {
			indices.get(indexName).insert(row.getField(indexName), row);
		}
	}
	
	public void DBTableGet()
	{
		;
	}
	public List<DBTypes> getColumnTypes(){
		//TODO
		return null;
	}
	
	
	
}
