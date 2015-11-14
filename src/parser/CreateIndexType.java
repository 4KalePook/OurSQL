package parser;

import java.util.Scanner;

import database.Database;

public class CreateIndexType extends ParserTypes {

	String indexName;
	String tableName;
	String columnName;
	
	public String getIndexName() {
		return indexName;
	}


	public String getColumnName() {
		return columnName;
	}


	public String getTableName() {
		return tableName;
	}


	public CreateIndexType() {
		this.commandType = CommandTypes.CREATE_INDEX;
		
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // create
		scanner.next(); // index
		indexName = scanner.next();  //indexName
		scanner.next(); // on
		tableName = scanner.next();  //tabelName
		columnName = scanner.next();  //columnName
		scanner.close();
	}

	@Override
	public String action(Database database) {
		database.addIndex(indexName, tableName, columnName);
		return "INDEX CREATED";
	}
	
	

}
