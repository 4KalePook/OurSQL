package parser;

public enum CommandTypes{
	CREATE_TABLE("^CREATE TABLE*", CreateTableType.class) ,
	CREATE_INDEX("^CREATE INDEX*", CreateIndexType.class) ,
	INSERT_INTO("^INSERT INTO*",InsertType.class) ,
	UPDATE("^UPDATE*", UpdateType.class) ,
	DELETE_FROM("^DELETE FROM*",DeleteType.class),
	SELECT("^SELECT*",SelectType.class),
	CREATE_VIEW("^CREATE VIEW*",CreateViewType.class) ; 
	
	private String commandText;
	private Class<?> commandClass;
	private CommandTypes(String commandText, Class<?> commandClass){
		this.commandText = commandText;
		this.commandClass = commandClass;
	}
	
	public String getCommandText(){
		return commandText;
	}
	public Class<?> getCommandClass(){
		return commandClass;
	}
}