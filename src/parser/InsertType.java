package parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import database.Database;
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
	
		scanner.next(); // values
		
		String rest=scanner.nextLine();  // rest of line
		Matcher matcher = Pattern.compile("(\"[^\"]*\")|(\\d+)|(NULL)").matcher(rest); // value patterns
		stringValues.clear();
		int index=0;
		while(matcher.find(index)){
			String value = matcher.group();
			stringValues.add(value);
			if(index==matcher.end())
				break;
			index=matcher.end();
		}

		System.err.println(stringValues);
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
