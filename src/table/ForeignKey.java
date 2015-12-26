package table;

public class ForeignKey {
	String tableName;
	String columnName;
	Action onUpdate, onDelete;

	public ForeignKey(String tableName, String columnName, Action onUpdate, Action onDelete) {
		this.tableName = tableName;
		this.columnName = columnName; 
		this.onUpdate = onUpdate;
		this.onDelete = onDelete;
	}
}
