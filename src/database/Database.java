package database;

import java.util.HashMap;
import java.util.List;

import dbTypes.DBTypes;
import table.DBObject;
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
		tables.get(tableName).update(columnName,valueClause,whereClause);
	}
	
	public void deleteFrom(String tableName, String whereClause  ){
		tables.get(tableName).delete(whereClause);
	}
	
	public void selectFrom(String tableName1,String tableName2,List<String> columnNames, String whereClause  ){
		
		DBTable table = tables.get(tableName1);
		List<DBObject> rows=table.selectRows(whereClause);
		if(rows.isEmpty()){
			System.out.println("NO RESULTS");
			return;    
		}
		/*************************
		 * Displaying the header *
		 *************************/
		boolean first = true;
		for(String columnName: columnNames){
			if(!first)
				System.out.print(',');
			System.out.print(columnName);
			first=false;
		}
		System.out.println();
		
		/*************************
		 * Displaying the table  *
		 *************************/
		for(DBObject row: rows){
			first = true;
			for(String columnName: columnNames){
				DBTypes value = row.getField(columnName);
				if(!first)
					System.out.print(',');
				System.out.print(value.getValue().toString());
				first=false;
			}
			System.out.println();
		}
		
	}
	
	public void insert(String tableName, List<DBTypes> values){
		tables.get(tableName).insertRow(values);
	}
}
