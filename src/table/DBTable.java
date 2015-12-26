package table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import dbTypes.DBTypes;
import dbTypes.INT;
import dbTypes.VARCHAR;
import parser.ConditionCalc;
import parser.CreateTableType;

public class DBTable {
	Database database;
	LinkedList<DBObject> tableObjects;
	HashMap<String, TableIndex<DBTypes> > indices;
	String primaryKey;
	LinkedList<String> fkInvTables;  // TableName where have foreign key to this
	HashMap<String, ForeignKey> fkTables;  // first object is the table Name
	
	HashMap<String, DBTypes> schema; //HashMap<String, DBTypes> DBObject = new HashMap<String, DBTypes>(schema);
	CreateTableType createTable;
	
	public DBTable(CreateTableType createTable, Database database) {
		this.database = database;
		tableObjects = new LinkedList<>();
		this.createTable = createTable;
		this.indices = new HashMap<String, TableIndex<DBTypes> >(); 
		
		schema=new HashMap<String, DBTypes>();
		for(String columnName : createTable.getSchema().keySet()){
			schema.put(columnName,
					createTable.getTypes().elementAt(createTable.getSchema().get(columnName)));
		}
		
		primaryKey = createTable.getPK();
		
		this.addIndex("primary_key", primaryKey);
		// TODO: implement FK functionality
		for(ForeignKey fk: createTable.getFKs())
		{
			this.addIndex(fk.columnName, fk.columnName);
			database.getTable(fk.tableName).addInvFK(createTable.getTableName());
		}
		
		System.err.println(createTable.getSchema());
		System.err.println(createTable.getTableName());
	}
	
	public void addInvFK(String tableName) {
		fkInvTables.add(tableName);
	}
	
	public void addIndex(String indexName, String columnName) {
		if(indices.containsKey(columnName))
			return;
		indices.put(columnName, new TableIndex<DBTypes>());
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
			ConditionCalc calc=new ConditionCalc(row);
			if(calc.calculate(whereClause)){   //TODO
				result.add(row);
			}
		}
		return result;
	}
	
	public boolean update(String columnName,String valueClause,String whereClause){
		List<DBObject> rows= selectRows(whereClause);
		return updateSelf(rows, columnName, valueClause);
	}
	
	public boolean updateSelf(List<DBObject> rows, String columnName, DBTypes value){
		System.err.println(rows.size() + "!@#$");
		for(DBObject row: rows){
			//		System.err.println("UPDATE" + " " + columnName + " " + value.toStr() );
			if(!updateRow(row, value, columnName))
				return false;
		}
		return true;
	}
	
	public boolean updateSelf(List<DBObject> rows, String columnName, String valueClause){
		System.err.println(rows.size() + "!@#$");
		for(DBObject row: rows){
			DBTypes value = getNewVal(row, columnName, valueClause);
	//		System.err.println("UPDATE" + " " + columnName + " " + value.toStr() );
			if(!updateRow(row, value, columnName))
				return false;
		}
		return true;
	}
	
	public boolean updateRow(DBObject row, DBTypes newVal, String columnName){
		DBTypes oldVal = row.getField(columnName);
		row.updateField(columnName, newVal);
		if(columnName == primaryKey)
			if(!updateInvFK(oldVal, newVal))
				return false;
		return true;
	}
	public DBTypes getNewVal(DBObject row, String columnName, String valueClause){
		ConditionCalc calc=new ConditionCalc(row);
		DBTypes value=null;
		if(row.getField(columnName).getClass().equals(VARCHAR.class)){  
			value = new VARCHAR(calc.StrCompVal(valueClause)); // If it's string
		}else if(row.getField(columnName).getClass().equals(INT.class)){ 
			value = new INT(calc.IntCompVal(valueClause)); // if it's integer 
		}else{
			System.err.println("Undefined type");
		}
		return value;
	}
	
	public boolean delete(String whereClause){
		List<DBObject> rows= selectRows(whereClause);
		if(!checkInvFKDelete(rows))
		{
			System.err.println("check on delete false!");
			return false;
		}
		System.err.println("check on delete ok!");
		deleteInvFK(rows);
		System.err.println("delete INVFK ok");
		deleteSelf(rows);
		return true;
	}
	
	public void deleteSelf(List<DBObject> rows){
		tableObjects.removeAll(rows);
		for(String indexName : indices.keySet()){
			TableIndex<DBTypes> index=indices.get(indexName);
			for(DBObject row : rows){
				index.remove(row.getField(indexName));
			}
		}
	}
	
	public void DBTableGet()
	{
		;
	}
	public List<DBTypes> getColumnTypes(){
		return createTable.getTypes();
	}
	
	public HashMap<String,DBTypes> getSchema(){
		return schema;
	}
	
	
	public List<DBObject> getRowByIndex(String indexName, DBTypes value){
//		List<DBObject> rows = indices.get(indexName).getFirst(value);
//		return rows;
		System.out.println("ERRRRRRRRR getRowByIndex return NULL");
		return null;
		
	}
	
	public boolean updateFK(String tableName, DBTypes oldVal, DBTypes newVal)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, oldVal);
		if(fk.onUpdate == Action.RESTRICT){
			if(rows.size() > 0)
				return false;
			return true;
		}
		else{
			return updateSelf(rows, fk.columnName, newVal);
		}
	}
	
	private boolean updateInvFK(DBTypes oldVal, DBTypes newVal)
	{
		for(String fk: fkInvTables)
			if(!database.getTable(fk).updateFK(createTable.getTableName(), oldVal, newVal))
				return false;
		return true;
	}
	
	public boolean checkFKDelete(String tableName, DBTypes value)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, value);
		if(fk.onDelete == Action.RESTRICT){
			if(rows.size() > 0)
				return false;
			return true;
		}
		else{
			return checkInvFKDelete(rows);
		}
	}
	
	private boolean checkInvFKDelete(List<DBObject> rows){
		
		for(DBObject row: rows){
			for(String fk: fkInvTables)
				database.getTable(fk).checkFKDelete(createTable.getTableName(), row.getField(primaryKey));
		}
		return true;
	}
	
	public void deleteFK(String tableName, DBTypes value)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, value);
		deleteInvFK(rows);
		deleteSelf(rows);
	}
	
	
	private void deleteInvFK(List<DBObject> rows)
	{
		for(DBObject row: rows){
			for(String fk: fkInvTables)
				database.getTable(fk).deleteFK(createTable.getTableName(), row.getField(primaryKey));
		}
	}
}
