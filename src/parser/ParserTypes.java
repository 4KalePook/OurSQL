package parser;

import database.Database;

abstract public class ParserTypes {
	protected String command;
	protected CommandTypes commandType;
	
	public CommandTypes getCommandType(){
		return commandType;
	}
	
	public String getCommand(){
		return command;
	}
	
	public void setCommand(String command){
		this.command = command;
	}
	public abstract void parse();
	public abstract String action(Database database);
}
