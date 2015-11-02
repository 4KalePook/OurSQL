package parser;

public enum CommandTypes{
	CREATE_TABLE("CREATE TABLE", CreateTableType.class.getName()); //, CREATE_INDEX("CREATE INDEX"), INSERT_INTO("INSERT INTO"); //TODO: add other type
	
	private String commandText;
	private String commandClass;
	private CommandTypes(String commandText, String commandClass){
		this.commandText = commandText;
		this.commandClass = commandClass;
	}
	
	public String getCommandText(){
		return commandText;
	}
	public String getCommandClass(){
		return commandClass;
	}
}