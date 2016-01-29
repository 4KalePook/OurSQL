package table;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.SysexMessage;




import database.Database;
import dbTypes.DBTypes;
import dbTypes.INT;
import dbTypes.VARCHAR;
import errors.C1Constraint;
import errors.C2Constraint;
import errors.Constraint;
import parser.ConditionCalc;
import parser.ConditionSegCalc;
import parser.CreateTableType;
import parser.CreateViewType;
import parser.Segment;
import parser.SelectType;

public class DBView extends DBTable {
    Database database;
	HashMap<String, DBTypes> schema; //HashMap<String, DBTypes> DBObject = new HashMap<String, DBTypes>(schema);
	CreateViewType createView;
	SelectType quary;
	private boolean isUpdatable;  // pazira ye khodemun
	
	public DBView(CreateViewType createView, Database database, SelectType Quary) {
		this.database=database;
		this.createView=createView;
		this.quary = Quary;
		// TODO: implement this
		if( quary.getTableName2() != "" && quary.getTableName2() != quary.getTableName1() )
			this.isUpdatable = false;
		else
			this.isUpdatable = true;
	}

	
	
	public List<DBObject> selectRows(String Name1,String Name2,DBTable table2, String whereClause,boolean isjoin){
		//TODO Create a Virtual table and then pass the quarry
		return null;
	}
	
	
	public void updateAllIndex(String indexName, DBTypes oldVal, DBTypes newVal){
		//nothing to do, no index is defined
		return;
	}
	public void updateRowIndex(String indexName, DBTypes oldVal, DBTypes newVal, DBObject row){
		//nothing to do, no index is defined
		return;
	}

	public boolean update(String columnName,String valueClause,String whereClause){
		if( isUpdatable )
			return database.getTable(quary.getTableName1()).update(columnName, valueClause, whereClause);
		else
			return false;
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
		if( isUpdatable )
			return database.getTable(quary.getTableName1()).delete(whereClause);
		return false;
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
		List<DBObject> rows = new LinkedList<DBObject>();
		
		Set<Map.Entry<DBTypes, ArrayList<DBObject>>> seg= indices.get(indexName).getSegment(value, true, value, true);
		for(Map.Entry<DBTypes, ArrayList<DBObject>> entry : seg)
			for(DBObject row : entry.getValue())
				rows.add(row);
		
		return rows;
	}
	
	public List<DBObject> getRowByRange(String indexName, DBTypes valueBeg, DBTypes valueEnd){
		List<DBObject> rows = new LinkedList<DBObject>();
		
		Set<Map.Entry<DBTypes, ArrayList<DBObject>>> seg= indices.get(indexName).getSegment(valueBeg, true, valueEnd, true);
		for(Map.Entry<DBTypes, ArrayList<DBObject>> entry : seg)
			for(DBObject row : entry.getValue())
				rows.add(row);
		//TODO: Check !!!
		return rows;
	}
	
	public void updateFK(String tableName, DBTypes oldVal, DBTypes newVal)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, oldVal);
		updateSelf(rows, fk.columnName, newVal);
		updateAllIndex(fk.columnName, oldVal, newVal);
	}
	
	private void updateInvFK(DBTypes oldVal, DBTypes newVal)
	{
		for(String fk: fkInvTables)
			 database.getTable(fk).updateFK(createTable.getTableName(), oldVal, newVal);
	}
	
	public boolean checkPKValueExists(DBTypes value){
		if(primaryKey == null || primaryKey.equals(""))
			return false;  // TODO: this means the FK can't insert anything!
		if( getRowByIndex(primaryKey, value).size() == 1 )
			return true;
		return false;
	}
	
	private boolean checkInvFKUpdate(List<DBObject> rows){
	//		System.err.println(primaryKey);
		for(DBObject row: rows)
			for(String fk: fkInvTables)
				if(!database.getTable(fk).checkFKUpdate(createTable.getTableName(), row.getField(primaryKey)))
					return false;
		return true;
	}
	
	public boolean checkFKUpdate(String tableName, DBTypes value)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, value);
//		System.err.println(fk.onDelete.name());
//		System.err.println(fk.onUpdate.name());
		if(fk.onUpdate == Action.RESTRICT){
			if(rows.size() > 0)
				return false;
			return true;
		}
		else{
			if(!fk.columnName.equals(primaryKey))
				return true;
			return checkInvFKUpdate(rows);
		}
	}

	public boolean checkFKDelete(String tableName, DBTypes value)
	{
		ForeignKey fk = fkTables.get(tableName);
		
		List<DBObject> rows = getRowByIndex(fk.columnName, value);
//		System.err.println(fk.onDelete.name());
//		System.err.println(fk.onUpdate.name());
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
//		System.err.println(primaryKey);

		for(DBObject row: rows)
			for(String fk: fkInvTables)
				if(!database.getTable(fk).checkFKDelete(createTable.getTableName(), row.getField(primaryKey)))
					return false;
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
