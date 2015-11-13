package parser;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import database.Database;
import dbTypes.DBEnumTypes;
import dbTypes.DBTypes;
import table.DBTable;

public class CreateTableType extends ParserTypes {

	String tableName;
	HashMap<String, Integer> schema;
	Vector<String> names;
	Vector<DBTypes> types;
	
	public String getTableName() {
		return tableName;
	}
	
	public Vector<DBTypes> getTypes() {
		return types;
	}

	public HashMap<String, Integer> getSchema() {
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
			names.add(key);
			types.add(dbType);
			schema.put(key, names.size()-1);
		}
		scanner.close();
	}

	@Override
	public String action(Database database) {
		database.addTables(tableName, new DBTable(this));
		return "TABLE CREATED";
	}
	
	

}
