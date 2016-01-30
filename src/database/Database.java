package database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import parser.ConditionCalc;
import dbTypes.DBTypes;
import dbTypes.INT;
import errors.Constraint;
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
	
	public boolean update(String tableName, String columnName, String valueClause, String whereClause  ){
		return tables.get(tableName).update(columnName,valueClause,whereClause);
	}
	
	public boolean deleteFrom(String tableName, String whereClause  ){
		return tables.get(tableName).delete(whereClause);
	}
	public List<DBObject> selectToRows(String tableName1,String tableName2,List<String> columnNames, String whereClause, boolean isjoin,List<String> groupBy,String havingClause  ){
		//TODO: optimize with indices
				DBTable table1 = tables.get(tableName1);
				
				DBTable table2 = table1;
				if(!tableName2.equals("")){
						table2=tables.get(tableName2);
				}
				System.err.println(table1==null);
				System.err.println("RAFT TOO");
				List<DBObject> rows=table1.selectRows(tableName1,tableName2,table2,whereClause,isjoin);
				System.err.println("OOMAD BIROON");
				System.err.println(rows == null);
				if(rows.isEmpty()){
					return new LinkedList<DBObject>();    
				}
				
				if(!groupBy.isEmpty()){
					HashMap <List<DBTypes>,DBObject> groups;
					groups=new HashMap<List<DBTypes>, DBObject>();
					
			//		System.err.println("HIIII");
					for(DBObject obj: rows){
						DBObject group=null;
						List<DBTypes> groupName=new LinkedList<DBTypes>();
						for(String str:groupBy){
							groupName.add(obj.getField(str));
							obj.DeleteField(str);
						}
						if(groups.containsKey(groupName)){
							group=groups.get(groupName);
						}
						boolean newGroup;
						if(group==null){
							newGroup=true;
							group=new DBObject();
						}else{
							newGroup=false;
						}
					//	System.err.println("test");
						for(String col:obj.getDataSet().keySet()){
							
							if(newGroup){
								DBTypes val = obj.getField(col);
								group.insertField("MIN("+col+")", val);
								group.insertField("MAX("+col+")", val);
								group.insertField("COUNT("+col+")",new INT(new Long(1)));
								group.insertField("SUM("+col+")", val);
								group.insertField("AVG("+col+")", val);
								

								Object[] names=groupBy.toArray();
								Object[] values=groupName.toArray();
								for(int i = 0 ; i < names.length ; ++i){
									group.insertField((String)names[i], (DBTypes)values[i]);
								}
							}else{
						//		System.err.println(group.getField("MIN("+col+")"));
								DBTypes val = obj.getField(col);
								group.updateField("MIN("+col+")", (val.compareTo(group.getField("MIN("+col+")")) < 0 ?val:group.getField("MIN("+col+")")));
								group.updateField("MAX("+col+")", (val.compareTo(group.getField("MAX("+col+")")) > 0 ?val:group.getField("MAX("+col+")")));
								group.updateField("COUNT("+col+")",new INT((new Long(1+(Long)(group.getField("COUNT("+col+")")).getValue()))));
								if(val.getClass().equals(INT.class)){
									group.updateField("SUM("+col+")",new INT((Long)val.getValue()+(Long)(group.getField("SUM("+col+")")).getValue()));
									group.updateField("AVG("+col+")",new INT((Long)group.getField("SUM("+col+")").getValue()/(Long)group.getField("COUNT("+col+")").getValue()));
								}
							}
						}
						groups.put(groupName, group);
					}
				//	System.err.println("!@#$");
					rows=new LinkedList<DBObject>();
					for(DBObject group:groups.values()){
						System.err.println("!@#$!@#$!@#@$");
						ConditionCalc calc=new ConditionCalc(group, group, tableName1, tableName2);
						if(calc.calculate(havingClause)){
							rows.add(group);
						}
					}
				}
			return rows;
	}
	
	public void selectFrom(String tableName1,String tableName2,List<String> columnNames, String whereClause, boolean isjoin,List<String> groupBy,String havingClause  ){
		List<DBObject> rows = selectToRows(tableName1, tableName2, columnNames, whereClause, isjoin, groupBy, havingClause);
		if(rows.isEmpty()){
			System.out.println("NO RESULTS");
			return;
		}
		
		/*************************
		 * Displaying the header *
		 *************************/
		boolean first = true;
		for(String str: columnNames){
			if(!first)
				System.out.print(',');
			
			String col;
			if(str.indexOf(".")!=-1){
				col=(str.substring(str.indexOf('.')+1));
			}else{
				col=str;
			}
			
			System.out.print(col);
			
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
				if(value==null){
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
	
	public void insert(String tableName, List<DBTypes> values) throws Constraint{
		tables.get(tableName).insertRow(values);
	}
}
