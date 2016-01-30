package parser;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import database.Database;
import dbTypes.DBEnumTypes;
import dbTypes.DBTypes;
import table.Action;
import table.DBTable;
import table.DBView;
import table.ForeignKey;

public class CreateViewType extends ParserTypes {

	String viewName;
	String sQuary;
	SelectType Quary;
	Vector<DBTypes> types;
	HashMap<String, Integer> schema;
	Vector<String> names;
	String PK;

	
	public Vector<ForeignKey> getFKs() {
		return null;
	}
	
	public String getPK() {
		return PK;
	}

	public String getTableName() {
		return null;
	}

	public Vector<DBTypes> getTypes() {
		return types;
	}

	public Vector<String> getNames() {
		return names ;
	}

	public HashMap<String, Integer> getSchema() {
		return schema;
	}

	public CreateViewType() {
		this.commandType = CommandTypes.CREATE_VIEW;
		Quary=new SelectType();
		schema = new HashMap<>();
		types = new Vector<DBTypes>();
		names = new Vector<String>();

	}

	@Override
	public void parse() {
		Scanner scanner = new Scanner(command);
		scanner.useDelimiter(ParseCommand.DELIMS);
		scanner.next(); // Create
		scanner.next(); // View

		viewName = scanner.next();
		scanner.next(); // As
		sQuary = scanner.nextLine();
		Quary.setCommand(sQuary);
		Quary.parse();
	}

	@Override
	public String action(Database database) {
		DBTable table=database.getTable(Quary.getTableName1());
		for(String name: Quary.getFullNames())
			names.add(name);
		for(String name: getNames())
		{
			types.add(table.getSchema().get(name));
			schema.put(name, types.size()-1);
			if(table.getPrimaryKey().equals(name))
				PK = name;
		}
		
		database.addTables(viewName, new DBView(this, database, Quary));
		return "VIEW CREATED";
	}



}
