package table;


import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import dbTypes.DBTypes;
import parser.Segment;

public class TableIndex<T> {
	NavigableMap<T, ArrayList<DBObject>> index;
	
	public TableIndex() {
		this.index = new TreeMap<>(); 
	}
	
	public void insert(T key, DBObject obj) {
		if( !index.containsKey(key) )
			index.put(key, new ArrayList<DBObject>());
		index.get(key).add(obj);
	}
	
	public void remove(T key, DBObject obj) {
		index.get(key).remove(obj);
		if( index.get(key).isEmpty() )
			index.remove(key);
	}
	public void updateIndex(T oldKey, T newKey){
		if( !index.containsKey(newKey) )
			index.put(newKey, new ArrayList<DBObject>());
		index.get(newKey).addAll(index.get(oldKey));
		index.remove(oldKey);
		//TODO: merge two index set index.get(key);
	}

	public void updateIndex(T oldKey, T newKey, DBObject newVal){
		//TODO: update Index of this Obj
		remove(oldKey, newVal);
		insert(newKey, newVal);
	}
	
	public Set<Map.Entry<T, ArrayList<DBObject>>> getFirst(T key) {
		return getFirst(key, true);
	}
	
	public Set<Map.Entry<T, ArrayList<DBObject>>> getFirst(T key, boolean inclusive) {
		return index.tailMap(key, inclusive).entrySet();
	}
	
	public Set<Map.Entry<T, ArrayList<DBObject>>> getLast(T key, boolean inclusive) {
		return index.headMap(key, inclusive).entrySet();
	}
	
	public Set<Map.Entry<T, ArrayList<DBObject>>> getSegment(T beg, boolean begInc, T end, boolean endInc) {
		return index.tailMap(beg, begInc).headMap(end, endInc).entrySet();
	}
	
	public Set<Map.Entry<T, ArrayList<DBObject>>> getSegment(Segment seg) {
		if( seg.getBegin().posInfinity() || seg.getEnd().negInfinity() )
			return new TreeSet<Map.Entry<T, ArrayList<DBObject>>>();
		if( seg.getBegin().negInfinity() ) {
			if( seg.getEnd().posInfinity() )
				return index.entrySet();
			return getLast((T)seg.getEnd().getValue(), seg.getEnd().isInclusive());
		}
		if( seg.getEnd().posInfinity() )
			return getFirst((T)seg.getBegin().getValue(), seg.getBegin().isInclusive());
		return getSegment((T)seg.getBegin().getValue(), seg.getBegin().isInclusive(),
							(T)seg.getEnd().getValue(), seg.getEnd().isInclusive());
	}
}
