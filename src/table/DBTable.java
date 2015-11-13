package table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dbTypes.DBTypes;
import parser.CreateTableType;

public class DBTable {
	LinkedList<DBObject> tableObjects;
	HashMap<String, TableIndex<DBTypes> > indices;
	
//	HashMap<String, DBTypes> schema; //HashMap<String, DBTypes> DBObject = new HashMap<String, DBTypes>(schema);
	CreateTableType createTable;
	
	public DBTable(CreateTableType createTable) {
		tableObjects = new LinkedList<>();
		this.createTable = createTable;
		System.out.println(createTable.getSchema());
		System.out.println(createTable.getTableName());
	}
	
	public void addIndex(String indexName, String columnName) {
		indices.put(columnName, new TableIndex<>());
		TableIndex<DBTypes> index = indices.get(columnName);
		for(DBObject obj: tableObjects)
			index.insert(obj.getField(columnName), obj);
	}
	
	public void insertRow(List<DBTypes> values) {
		// TODO: check types before adding
		DBObject row = new DBObject();
		for(int i=0; i<createTable.getNames().size(); i++)
			row.insertField(createTable.getNames().get(i), values.get(i));
		insertRow(row);
	}
	
//	public void makeSchema(HashMap<String, DBTypes> schema){
//		this.schema = schema;
//	}
	
	public void insertRow(DBObject row)
	{ 
		tableObjects.add(row);
		for(String indexName: indices.keySet()) {
			indices.get(indexName).insert(row.getField(indexName), row);
		}
	}
	

	public List<DBObject> selectRows(String whereClause){
		List<DBObject> result = new LinkedList<DBObject>();
		for(DBObject row : tableObjects){	//TODO make sure this is the correct order for the result
			if(/** misgar condition **/true){   //TODO
				result.add(row);
			}
		}
		return row;
	}
	
	public void update(String columnName,String whereClause,String valueClause){
		List<DBObject> rows= selectRows(whereClause);
	}
	
	public void delete(String whereClause){
		//TODO
	}
	
	
	public void DBTableGet()
	{
		;
	}
	public List<DBTypes> getColumnTypes(){
		return createTable.getTypes();
	}
	
	
	
}
