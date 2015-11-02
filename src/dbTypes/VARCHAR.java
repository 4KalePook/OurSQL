package dbTypes;

public class VARCHAR implements DBTypes{
	private String value;
	
	public VARCHAR(){
		this.value = null;
	}
	public VARCHAR(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}


