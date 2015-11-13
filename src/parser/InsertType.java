package parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import database.Database;
import dbTypes.DBEnumTypes;
import dbTypes.DBTypes;
import table.DBTable;

public class InsertType extends ParserTypes {

	String tableName;
	List<String> stringValues;
	List<DBTypes> values;
	
	public List<String> getStringValues() {
		return stringValues;
	}


	public List<DBTypes> getValues() {
		return values;
	}


	public String getTableName() {
		return tableName;
	}


	public InsertType() {
		this.commandType = CommandTypes.INSERT_INTO;
		values = new LinkedList<DBTypes>();
		stringValues=new LinkedList<String>();
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // Insert
		scanner.next(); // Into
		tableName = scanner.next(); // tableName
		
		stringValues.clear();
		while(scanner.hasNext()){
			String value = scanner.next();
			stringValues.add(value);
		}
		scanner.close();
	}

	@Override
	public String action(Database database) {
		values.clear();
		DBTable table = database.getTable(tableName);
		List<DBTypes> types = table.getColumnTypes();
		Iterator<String> itStringValues = stringValues.iterator();
		Iterator<DBTypes> itTypes = types.iterator();
		while(itTypes.hasNext() && itStringValues.hasNext() ){
			DBTypes type = itTypes.next();
			String  stringValue = itStringValues.next();
			values.add(type.toValue(stringValue));
		}
		database.insert(tableName,values);
		return "RECORD INSERTED";
	}
	
	

}
