package dbTypes;

import java.util.Scanner;


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
	public String toStr(){
		if(value==null)
			return "NULL";
		return String.format("%d",value.longValue());
	}
	
	public DBTypes toValue(String string){
		if(string == "NULL")
			return null;
		
		Long value;
		try{
				Scanner scanner= new Scanner(string);
				value = scanner.nextLong();
				scanner.close();
		}catch(Exception e){
			return null;
		}
		return new INT(value);
	}
}
