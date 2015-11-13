package table;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import dbTypes.DBTypes;

public class TableIndex<T> {
	NavigableMap<T, DBObject> index;
	
	public TableIndex() {
		this.index = new TreeMap<>(); 
	}
	
	public void insert(T key, DBObject obj) {
		index.put(key, obj);
	}
	
	public Set<Map.Entry<T, DBObject>> getFirst(T key) {
		return getFirst(key, true);
	}
	
	public Set<Map.Entry<T, DBObject>> getFirst(T key, boolean inclusive) {
		return index.tailMap(key, inclusive).entrySet();
	}
	
	public Set<Map.Entry<T, DBObject>> getSegment(T beg, boolean begInc, T end, boolean endInc) {
		return index.tailMap(beg, begInc).headMap(end, endInc).entrySet();
	}
}
