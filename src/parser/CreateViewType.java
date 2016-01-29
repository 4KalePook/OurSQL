package parser;

import java.util.HashMap;
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
	
	public Vector<ForeignKey> getFKs() {
		return null;
	}
	
	public String getPK() {
		return null;
	}

	public String getTableName() {
		return null;
	}

	public Vector<DBTypes> getTypes() {
		return null;
	}

	public Vector<String> getNames() {
		return null;
	}

	public HashMap<String, Integer> getSchema() {
		return null;
	}

	public CreateViewType() {
		this.commandType = CommandTypes.CREATE_VIEW;
		Quary=new SelectType();
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
		database.addTables(viewName, new DBView(this, database, Quary));
		return "VIEW CREATED";
	}



}
