package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import database.Database;

public class SelectType extends ParserTypes {

	String tableName;
	List<String> columnNames;
	String whereClause;



	public String getTableName() {
		return tableName;
	}


	public SelectType() {
		this.commandType = CommandTypes.SELECT;
		columnNames= new LinkedList<String>();
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // select
		columnNames.clear();
		String token=scanner.next();
		while(token != "FROM"){
			columnNames.add(token);
			token=scanner.next();
		}
		tableName = scanner.next();  //table name
		if(scanner.hasNext()){
			scanner.next();// where
			String rest=scanner.nextLine();
			int begin= 0;
			int end = rest.indexOf(";");
			if(end==-1)
				end=rest.length();
			whereClause = rest.substring(begin+"WHERE".length(), end);  //where clause
		}else{	
			whereClause="TRUE";
		}
		scanner.close();
	}

	@Override
	public String action(Database database) {
		database.selectFrom(tableName, columnNames, whereClause);
		return null;
	}
	
	

}
