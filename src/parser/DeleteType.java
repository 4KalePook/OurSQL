package parser;

import java.util.Scanner;

import database.Database;

public class DeleteType extends ParserTypes {

	String tableName;
	String whereClause;
	



	public String getTableName() {
		return tableName;
	}


	public DeleteType() {
		this.commandType = CommandTypes.DELETE_FROM;
		
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // delete
		scanner.next(); // from
		tableName = scanner.next();  //table name
		if(scanner.hasNext()){
			scanner.next();// where
			String rest=scanner.nextLine();
			int begin= 0;
			int end = rest.indexOf(";");
			if(end==-1)
				end=rest.length();
			whereClause = rest.substring(begin, end);  //where clause
		}else{	
			whereClause="TRUE";
		}
		scanner.close();
	}

	@Override
	public String action(Database database) {
		System.err.println(whereClause);

		database.deleteFrom(tableName, whereClause);
		return null;
	}
	
	

}
