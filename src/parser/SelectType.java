package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import database.Database;

public class SelectType extends ParserTypes {

	String tableName1;
	String tableName2;
	List<String> columnNames;
	String whereClause;



	public String getTableName1() {
		return tableName1;
	}
	public String getTableName2() {
		return tableName2;
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
		while(!token.equals("FROM")){
			columnNames.add(token);
			token=scanner.next();
		}
		tableName1 = scanner.next();  //table name
		tableName2 = "";
		if(scanner.hasNext()){
			String next=scanner.next();// where / table 2
			if(next!="WHERE"){  // it was table 2
				tableName2=next;
				if(scanner.hasNext()){
					scanner.next();// where
				}
			}
		}
		if(scanner.hasNext()){
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
		database.selectFrom(tableName1,tableName2, columnNames, whereClause);
		return null;
	}
	
	

}
