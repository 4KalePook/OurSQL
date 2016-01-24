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

public class DBView extends DBTable {
    Database database;
	HashMap<String, DBTypes> schema; //HashMap<String, DBTypes> DBObject = new HashMap<String, DBTypes>(schema);
	CreateViewType createView;
	
	public DBView(CreateViewType createView, Database database) {
		this.database=database;
		this.createView=createView;
	}

	
	
	public List<DBObject> selectRows(String Name1,String Name2,DBTable table2, String whereClause,boolean isjoin){
		//TODO Create a Virtual table and then pass the quarry
		return null;
	}
	
	
	public void updateAllIndex(String indexName, DBTypes oldVal, DBTypes newVal){
		TableIndex<DBTypes> index=indices.get(indexName);
		if(index != null)
			index.updateIndex(oldVal, newVal);
	}
	public void updateRowIndex(String indexName, DBTypes oldVal, DBTypes newVal, DBObject row){
		TableIndex<DBTypes> index=indices.get(indexName);
		if(index != null)
			index.updateIndex(oldVal, newVal, row);
	}

	public boolean update(String columnName,String valueClause,String whereClause){
		List<DBObject> rows= selectRows(whereClause);
		return updateSelf(rows, columnName, valueClause);
	}
	
	public void updateSelf(List<DBObject> rows, String columnName, DBTypes value){ //this function call recuresive
//		System.err.println(rows.size() + "!@#$");
//		if(!checkInvFKUpdate(rows)) //check inv fk restricting ...
//			return false;
 		for(DBObject row: rows){
	//		System.err.println("UPDATE" + " " + columnName + " " + value.toStr() );
			updateRow(row, value, columnName);
//				return false;
 		}
 		
 		
	}
	
	public boolean updateSelf(List<DBObject> rows, String columnName, String valueClause){ //this function called once
//		System.err.println(rows.size() + "!@#$");
 		if(!checkInvFKUpdate(rows)) //check inv fk restricting ...
 			return false;
		for(DBObject row: rows){
			DBTypes value = getNewVal(row, columnName, valueClause);
	//		System.err.println("UPDATE" + " " + columnName + " " + value.toStr() );
			DBTypes oldVal = row.getField(columnName);
			
			for(ForeignKey fk: createTable.getFKs())
				if( fk.columnName.equals(columnName) )
					if( !database.getTable(fk.tableName).checkPKValueExists(value) ) {
						System.out.println(C2Constraint.Message);
						return true;
					}
			
			if(!updateRow(row, value, columnName))
				return true;
			updateRowIndex(columnName, oldVal, value, row);
		}
		return true;
	}
	
	public boolean updateRow(DBObject row, DBTypes newVal, String columnName){
		DBTypes oldVal = row.getField(columnName);
		if(columnName.equals(primaryKey))
	      if( checkPKValueExists(newVal) ) {
	    	  System.out.println(C1Constraint.Message);
	          return true;
	      }
		
		row.updateField(columnName, newVal);
		if(columnName.equals(primaryKey))
			updateInvFK(oldVal, newVal);
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
//			System.err.println("check on delete false!");
			return false;
		}
//		System.err.println("check on delete ok!");
		deleteInvFK(rows);
//		System.err.println("delete INVFK ok");
		deleteSelf(rows);
		return true;
	}
	
	public void deleteSelf(List<DBObject> rows){
		tableObjects.removeAll(rows);
		for(String indexName : indices.keySet()){
			TableIndex<DBTypes> index=indices.get(indexName);
			for(DBObject row : rows){
				index.remove(row.getField(indexName), row);
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
