package dbTypes;

public class INT implements DBTypes{
	private Long value;
	
	public INT(){
		this.value = null;
	}
	public INT(Long value) {
		this.value = value;
	}
	
	public Long getValue(){
		return value;
	}
}
