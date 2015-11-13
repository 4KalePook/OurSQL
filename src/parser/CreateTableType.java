package parser;

import java.util.HashMap;
import java.util.Scanner;

import database.Database;
import dbTypes.DBEnumTypes;
import dbTypes.DBTypes;
import table.DBTable;

public class CreateTableType extends ParserTypes {

	String tableName;
	HashMap<String, DBTypes> schema;
	
	public String getTableName() {
		return tableName;
	}

	public HashMap<String, DBTypes> getSchema() {
		return schema;
	}

	public CreateTableType() {
		this.commandType = CommandTypes.CREATE_TABLE;
		schema = new HashMap<>();
	}
	
	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // create
		scanner.next(); // table
		tableName = scanner.next();
		
		while(scanner.hasNext()){
			String key = scanner.next();
			String type = scanner.next();
			Class<?> c;
			DBTypes dbType = null;
			try {
				c = DBEnumTypes.valueOf(type).getTypeClass();
				dbType = (DBTypes) c.newInstance();
			} catch ( InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			schema.put(key, dbType);
		}
		scanner.close();
	}

	@Override
	public String action(Database database) {
		database.addTables(tableName, new DBTable(this));
		return "TABLE CREATED";
	}
	
	

}
