package table;

public class ForeignKeyInv {
	String tableName;
	Action onUpdate, onDelete;

	public ForeignKeyInv(String tableName, Action onUpdate, Action onDelete) {
		this.tableName = tableName;
		this.onUpdate = onUpdate;
		this.onDelete = onDelete;
	}
}
