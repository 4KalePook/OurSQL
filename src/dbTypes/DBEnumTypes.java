package dbTypes;

public enum DBEnumTypes{
	VARCHAR("VARCHAR", VARCHAR.class.getName()), INT("INT", INT.class.getName()); //, CREATE_INDEX("CREATE INDEX"), INSERT_INTO("INSERT INTO"); //TODO: add other type
	
	private String typeText;
	private String typeClass;
	private DBEnumTypes(String typeText, String typeClass){
		this.typeText = typeText;
		this.typeClass = typeClass;
	}
	
	public String getTypeText(){
		return typeText;
	}
	public String getTypeClass(){
		return typeClass;
	}
}