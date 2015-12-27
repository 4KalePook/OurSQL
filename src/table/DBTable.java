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
import parser.Segment;

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
		fkInvTables = new LinkedList<>();
		fkTables = new HashMap<>();
		
		schema=new HashMap<String, DBTypes>();
		for(String columnName : createTable.getSchema().keySet()){
			schema.put(columnName,
					createTable.getTypes().elementAt(createTable.getSchema().get(columnName)));
		}

		if(!createTable.getPK().equals("")){
			primaryKey = createTable.getPK();
			this.addIndex("primary_key", primaryKey);
			//TODO: edit the code for following reason: primary_key may not exist, (check the sample).
		}
		else
			primaryKey =null;

		// TODO: implement FK functionality
		for(ForeignKey fk: createTable.getFKs())
		{
			fkTables.put(fk.tableName, fk);
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
	
	public void insertRow(List<DBTypes> values) throws Constraint {
		// TODO: check types before adding
		DBObject row = new DBObject();
		for(int i=0; i<createTable.getNames().size(); i++)
			row.insertField(createTable.getNames().get(i), values.get(i));
		insertRow(row);
	}
	
//	public void makeSchema(HashMap<String, DBTypes> schema){
//		this.schema = schema;
//	}
	
	public void insertRow(DBObject row) throws Constraint
	{ 
		if( primaryKey != null && !primaryKey.equals("") && checkPKValueExists(row.getField(primaryKey)) )
			throw new C1Constraint();
		
		for(Map.Entry<String, ForeignKey> entry : fkTables.entrySet()) {
			ForeignKey fk = entry.getValue();

			if( !database.getTable(fk.tableName).checkPKValueExists(row.getField(fk.columnName)) )
				throw new C2Constraint();
		}
		
		tableObjects.add(row);
		for(String indexName: indices.keySet()) {
			indices.get(indexName).insert(row.getField(indexName), row);
		}
	}
	

	public List<DBObject> selectRows(String whereClause){
		List<DBObject> result = new LinkedList<DBObject>();
		

		List<DBObject> rows=tableObjects;
		
		for(String key: indices.keySet()){
			ConditionSegCalc calc=new ConditionSegCalc();
			int type=(schema.get(key).getClass().equals(INT.class)?ConditionCalc.TYPE_INT:ConditionCalc.TYPE_VARCHAR);
			Segment range = calc.calculate(whereClause, key,type);
			System.err.println(range);
			if(range.getBegin().getValue().equals(range.getEnd().getValue())){
				rows=getRowByIndex(key,range.getBegin().getValue()); //TODO check
				break;
			}
		}
		for(DBObject row : rows){	//TODO make sure this is the correct order for the result
			ConditionCalc calc=new ConditionCalc(row);
			if(calc.calculate(whereClause)){   //TODO
				result.add(row);
				
			}
		}
		return result;
	}
	public DBObject join(DBObject row1, DBObject row2, String Name1, String Name2, boolean isjoin){
			DBObject ret=new DBObject();
			for(String key:row1.getRow().keySet()){
				ret.insertField(Name1+"."+key, row1.getField(key));
			}
			for(String key:row2.getRow().keySet()){
				ret.insertField(Name2+"."+key, row2.getField(key));
			}
			return ret;
	}
	public List<DBObject> selectRows(String Name1,String Name2,DBTable table2, String whereClause,boolean isjoin){
		if(Name2.equals("")){
			return selectRows(whereClause);
		}
		if(!isjoin){
			List<DBObject> result = new LinkedList<DBObject>();
			
			List<DBObject> rows1=tableObjects;
			
			for(String key: indices.keySet()){
				ConditionSegCalc calc=new ConditionSegCalc(Name1,Name2);
				String col = Name1+"."+key;
				int type=(schema.get(key).getClass().equals(INT.class)?ConditionCalc.TYPE_INT:ConditionCalc.TYPE_VARCHAR);
				Segment range = calc.calculate(whereClause, col,type);
				if(range.getBegin().getValue().equals(range.getEnd().getValue())){
					rows1=getRowByIndex(key,range.getBegin().getValue()); 
					break;
				}
			}
			
			List<DBObject> rows2=table2.tableObjects;
			
			for(String key: table2.indices.keySet()){
				ConditionSegCalc calc=new ConditionSegCalc(Name1,Name2);
				String col = Name2+"."+key;
				int type=(table2.schema.get(key).getClass().equals(INT.class)?ConditionCalc.TYPE_INT:ConditionCalc.TYPE_VARCHAR);
				Segment range = calc.calculate(whereClause, col,type);
				if(range.getBegin().getValue().equals(range.getEnd().getValue())){
					rows2=table2.getRowByIndex(key,range.getBegin().getValue()); 
					break;
				}
			}
			
			
			
			for(DBObject row1 : rows1){	//TODO make sure this is the correct order for the result
				
				for(DBObject row2 : rows2){	//TODO make sure this is the correct order for the result
					ConditionCalc calc=new ConditionCalc(row1, row2, Name1, Name2);
//					System.err.println(row1.toString());
//					System.err.println(row2.toString());
//					System.err.println(Name1.toString());
//					System.err.println(Name2.toString());
					if(calc.calculate(whereClause)){   //TODO
						result.add(join(row1,row2,Name1,Name2,false));
					}
				}
				
			}
			
			return result;
		}else{
			
			if(fkTables.get(Name2)==null){
				return table2.selectRows(Name2,Name1,this,whereClause,isjoin);
			}
			
		
			List<DBObject> result = new LinkedList<DBObject>();
			System.err.println(Name2);
			String fkcol = fkTables.get(Name2).columnName;
			
			List<DBObject> rows1=tableObjects;
			
			for(String key: indices.keySet()){
				ConditionSegCalc calc=new ConditionSegCalc(Name1,Name2);
				String col = Name1+"."+key;
				int type=(schema.get(key).getClass().equals(INT.class)?ConditionCalc.TYPE_INT:ConditionCalc.TYPE_VARCHAR);
				Segment range = calc.calculate(whereClause, col,type);
				if(range.getBegin().getValue().equals(range.getEnd().getValue())){
					rows1=getRowByIndex(key,range.getBegin().getValue()); //TODO check
					break;
				}
			}
			
			for(DBObject row1 : tableObjects){	//TODO make sure this is the correct order for the result
				String pkcol=table2.primaryKey;	
				DBTypes value = row1.getField(fkcol);
		
				Set<Map.Entry<DBTypes, ArrayList<DBObject>>> seg= table2.indices.get(pkcol).getSegment(value, true, value, true);
				for(Map.Entry<DBTypes, ArrayList<DBObject>> entry : seg){	//TODO make sure this is the correct order for the result
					for(DBObject row2 : entry.getValue()) {
						ConditionCalc calc=new ConditionCalc(row1, row2, Name1, Name2);
						if(calc.calculate(whereClause)){   //TODO
							result.add(join(row1,row2,Name1,Name2,false));
						}
					}
				}
				
			}
			return result;
		}
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
			if(!updateRow(row, value, columnName))
				return false;
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
	
	public void updateFK(String tableName, DBTypes oldVal, DBTypes newVal)
	{
		ForeignKey fk = fkTables.get(tableName);
		List<DBObject> rows = getRowByIndex(fk.columnName, oldVal);
		updateSelf(rows, fk.columnName, newVal);
	}
	
	private void updateInvFK(DBTypes oldVal, DBTypes newVal)
	{
		for(String fk: fkInvTables)
			 database.getTable(fk).updateFK(createTable.getTableName(), oldVal, newVal);
	}
	
	public boolean checkPKValueExists(DBTypes value){
		if( primaryKey.equals("") || primaryKey == null )
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
		if(!fk.columnName.equals(primaryKey))
			return true;
		List<DBObject> rows = getRowByIndex(fk.columnName, value);
//		System.err.println(fk.onDelete.name());
//		System.err.println(fk.onUpdate.name());
		if(fk.onUpdate == Action.RESTRICT){
			if(rows.size() > 0)
				return false;
			return true;
		}
		else{
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
