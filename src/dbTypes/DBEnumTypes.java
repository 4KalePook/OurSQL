package dbTypes;

public enum DBEnumTypes{
	VARCHAR("VARCHAR", VARCHAR.class), INT("INT", INT.class); //, CREATE_INDEX("CREATE INDEX"), INSERT_INTO("INSERT INTO"); //TODO: add other type
	
	private String typeText;
	private Class typeClass;
	private DBEnumTypes(String typeText, Class typeClass){
		this.typeText = typeText;
		this.typeClass = typeClass;
	}
	
	public String getTypeText(){
		return typeText;
	}
	public Class getTypeClass(){
		return typeClass;
	}
}