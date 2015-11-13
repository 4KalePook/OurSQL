package parser;

public enum CommandTypes{
	CREATE_TABLE("CREATE TABLE", CreateTableType.class); //, CREATE_INDEX("CREATE INDEX"), INSERT_INTO("INSERT INTO"); //TODO: add other type
	
	private String commandText;
	private Class commandClass;
	private CommandTypes(String commandText, Class commandClass){
		this.commandText = commandText;
		this.commandClass = commandClass;
	}
	
	public String getCommandText(){
		return commandText;
	}
	public Class getCommandClass(){
		return commandClass;
	}
}