package table;

import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.Database;
import dbTypes.DBTypes;
import dbTypes.INT;
import dbTypes.VARCHAR;
import parser.ConditionCalc;
import parser.CreateTableType;
import parser.Segment;

public class DBTable {
	Database database;
	LinkedList<DBObject> tableObjects;
	HashMap<String, TableIndex<DBTypes> > indices;
	String primaryKey;
	HashMap<String, ForeignKeyInv> fkInvTables;  // first object is the tableName
	
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
		if(!createTable.getPK().equals("")){
			this.addIndex("primary_key", createTable.getPK());
			//TODO: edit the code for following reason: primary_key may not exist, (check the sample).
		}
		// TODO: implement FK functionality
		for(ForeignKeyInv fk: createTable.getFKs())
			database.getTable(fk.tableName).addInvFK(createTable.getTableName(), fk);
		
		System.err.println(createTable.getSchema());
		System.err.println(createTable.getTableName());
	}
	
	public void addInvFK(String tableName, ForeignKeyInv fk) {
		fkInvTables.put(tableName, fk);
	}
	
	public void addIndex(String indexName, String columnName) {
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
			for(DBObject row1 : tableObjects){	//TODO make sure this is the correct order for the result
				
				for(DBObject row2 : table2.tableObjects){	//TODO make sure this is the correct order for the result
					ConditionCalc calc=new ConditionCalc(row1, row2, Name1, Name2);
					if(calc.calculate(whereClause)){   //TODO
						result.add(join(row1,row2,Name1,Name2,false));
					}
				}
				
			}
			
			return result;
		}else{
			List<DBObject> result = new LinkedList<DBObject>();
			String fkcol = fkInvTables.get(Name2).;
			for(DBObject row2 : table2.tableObjects){	//TODO make sure this is the correct order for the result
				String col=table2.primaryKey;	
				
				Segment seg=table2.indices.get(col).getSegment(, begInc, end, endInc);
				for(DBObject row : tableObjects){	//TODO make sure this is the correct order for the result
					ConditionCalc calc=new ConditionCalc(row1, row2, Name1, Name2);
					if(calc.calculate(whereClause)){   //TODO
						result.add(join(row1,row2,Name1,Name2,false));
					}
				}
				
			}
			return result;
		}
	}
	public void update(String columnName,String valueClause,String whereClause){
		List<DBObject> rows= selectRows(whereClause);
		System.err.println(rows.size() + "!@#$");
		for(DBObject row: rows){
			ConditionCalc calc=new ConditionCalc(row);
			DBTypes value=null;
			if(row.getField(columnName).getClass().equals(VARCHAR.class)){  
				value = new VARCHAR(calc.StrCompVal(valueClause)); // If it's string
			}else if(row.getField(columnName).getClass().equals(INT.class)){ 
				value = new INT(calc.IntCompVal(valueClause)); // if it's integer 
			}else{
				System.err.println("Undefined type");
			}
	//		System.err.println("UPDATE" + " " + columnName + " " + value.toStr() );
			row.updateField(columnName, value);
		}
	}
	
	public void delete(String whereClause){
		List<DBObject> rows= selectRows(whereClause);
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
	
}
