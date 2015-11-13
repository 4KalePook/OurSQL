package parser;

import java.util.Scanner;

import database.Database;

public class UpdateType extends ParserTypes {

	String tableName;
	String columnName;
	String stringValue;
	String whereClause;
	

	public String getColumnName() {
		return columnName;
	}


	public String getTableName() {
		return tableName;
	}


	public UpdateType() {
		this.commandType = CommandTypes.UPDATE;
		
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // update
		tableName = scanner.next();  //table name
		scanner.next(); // set
		columnName = scanner.next();  //columnName
		String rest=scanner.nextLine();
		int valueBegin=0;
		int valueEnd=rest.indexOf("WHERE");
		int end = rest.indexOf(";");
		if(end==-1)
			end=rest.length();
		
		if(valueEnd==-1)
			valueEnd=end;
		if(valueBegin==-1)
			stringValue="NULL";
		else{
			stringValue = rest.substring(valueBegin+1, valueEnd);  //value
		}
		if(valueEnd==end){
			whereClause="TRUE";
		}else{
			whereClause = rest.substring(valueEnd+"WHERE".length(), end);  //where clause
		}
		System.err.println(rest+"\n" + valueBegin + " " + valueEnd + " " + end);

		System.err.println(stringValue + ";");
		System.err.println(whereClause + ";");
		scanner.close();
	}

	@Override
	public String action(Database database) {
		database.update(tableName, columnName, stringValue, whereClause);
		return null;
	}
	
	

}
