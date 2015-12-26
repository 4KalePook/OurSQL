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
	
	public void selectFrom(String tableName1,String tableName2,List<String> columnNames, String whereClause, boolean isjoin  ){

		//TODO: optimize with indices
		DBTable table1 = tables.get(tableName1);
		
		DBTable table2 = table1;
		if(!tableName2.equals("")){
				table2=tables.get(tableName2);
		}
		List<DBObject> rows=table1.selectRows(tableName1,tableName2,table2,whereClause,isjoin);
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
			for(String str: columnNames){
				String name;
				String col;
				if(str.indexOf(".")!=-1){
					name=(str.substring(0, str.indexOf('.')));
					col=(str.substring(str.indexOf('.')+1));
				}else{
					name=tableName1;
					col=str;
				}
				
				DBTypes value = row.getField(name+"."+col);
				if(value==null && name.equals("")){
					value= row.getField(col);
				}
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
