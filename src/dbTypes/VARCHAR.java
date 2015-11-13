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
	public String toStr(){
		if(value!=null)
			return "\""+value+"\"";
		else
			return "NULL";
	}
	public DBTypes toValue(String string){
		if(string == "NULL"){
			return null;
		}
		int begin = string.indexOf("\"");
		int end = string.indexOf("\"", begin+1);
		if(end==-1 || begin ==-1){
			return null;
		}
		return new VARCHAR(string.substring(begin+1,end));
	}
	@Override
	public int compareTo(DBTypes o) {
		return getValue().compareTo((String)o.getValue());
	}
}


