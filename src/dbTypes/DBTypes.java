package dbTypes;

public interface DBTypes extends Comparable<DBTypes>{
	public Object getValue();
	public DBTypes toValue(String string);
	public String toStr();
}


