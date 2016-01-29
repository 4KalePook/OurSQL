package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import table.DBObject;
import database.Database;

public class SelectType extends ParserTypes {

	String tableName1;
	String tableName2;
	List<String> fullNames;
	String whereClause;
	boolean isjoin;
	List<String> groupBy;
	String havingClause;



	public String getTableName1() {
		return tableName1;
	}
	public String getTableName2() {
		return tableName2;
	}
	public List<String> getFullNames(){
		return fullNames;
	}


	public SelectType() {
		this.commandType = CommandTypes.SELECT;
		fullNames= new LinkedList<String>();
		groupBy=new LinkedList<String>();
	}
	
	@Override
	public void parse() {

		int cend = command.indexOf(";");
		if(cend==-1)
			cend=command.length();
		
		int gind= command.indexOf("GROUP");
		int hind= command.indexOf("HAVING");
		if(gind==-1)
			gind=cend;
		if(hind==-1)
			hind=cend;
		Scanner scanner = new Scanner(command.substring(0, gind));
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // select
		fullNames.clear();
		String token=scanner.next();
		while(!token.equals("FROM")){
			fullNames.add(token);
			token=scanner.next();
			if(token.equals("MAX") || token.equals("MIN") || token.equals("SUM")|| token.equals("COUNT")|| token.equals("AVG")){
				token=token+"("+scanner.next()+")";
			}
		}
		tableName1 = scanner.next();  //table name
		tableName2 = "";
		isjoin=false;
		
		if(scanner.hasNext()){
			String next=scanner.next();// where / JOIN / table 2
			if(next.equals("JOIN")){
				next=scanner.next(); // table 2
				isjoin=true;
			}
			if(!next.equals("WHERE")){  // it was table 2
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
		if(gind!= cend){
			scanner = new Scanner(command.substring(gind,hind));
			scanner.next(); //Group
			scanner.next(); //By
			while(scanner.hasNext()){
				groupBy.add(scanner.next());
			}
		}
		if(hind!= cend){
			scanner = new Scanner(command.substring(hind,cend));
			scanner.next(); //HAVING
			havingClause=scanner.nextLine();
		}else{
			havingClause="TRUE";
		}
	}

	@Override
	public String action(Database database) {
		database.selectFrom(tableName1,tableName2, fullNames , whereClause,isjoin,groupBy,havingClause);
		return null;
	}
	
	public List<DBObject> toRows(Database database) {
		return database.selectToRows(tableName1,tableName2, fullNames , whereClause,isjoin,groupBy,havingClause);
	}
	
	

}
