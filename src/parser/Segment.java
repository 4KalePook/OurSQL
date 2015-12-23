package parser;

import dbTypes.DBTypes;

class SegmentPoint {
	private boolean isInf, isInc;
	private DBTypes value;
	
	public boolean isInfinity() {
		return isInf;
	}
	
	public boolean isInclusive(){
		return isInc;
	}
	
	public DBTypes getValue() {
		return value;
	}
	
	public SegmentPoint(DBTypes value, boolean isInclusive) {
		this.isInf = false;
		this.isInc = isInclusive;
		this.value = value;
	}
	
	public SegmentPoint() {
		this.isInf = true;
		this.value = null;
		this.isInc = false;
	}
	
}

public class Segment {
	SegmentPoint begin, end;
	
	Segment(SegmentPoint begin, SegmentPoint end) {
		this.begin = begin;
		this.end = end;
	}
	
	public Segment union(Segment a) {
		SegmentPoint newBegin, newEnd;
		if( a.begin.isInfinity() || begin.isInfinity() )
			newBegin = new SegmentPoint();
		else if( a.begin.getValue().compareTo(begin.getValue()) == -1 )
			newBegin = a.begin;
		else
			newBegin = begin;
		
		if( a.end.isInfinity() || end.isInfinity() )
			newEnd = new SegmentPoint();
		else if( a.end.getValue().compareTo(end.getValue()) == 1 )
			newEnd = a.end;
		else
			newEnd = end;
		
		return new Segment(newBegin, newEnd);
	}
	
	public Segment intersect(Segment a) {
		SegmentPoint newBegin, newEnd;
		if( a.begin.isInfinity() && begin.isInfinity() )
			newBegin = new SegmentPoint();
		else if( begin.isInfinity() || (!a.begin.isInfinity() && a.begin.getValue().compareTo(begin.getValue()) == 1 ) )
			newBegin = a.begin;
		else
			newBegin = begin;
		
		if( a.end.isInfinity() && end.isInfinity() )
			newEnd = new SegmentPoint();
		else if( end.isInfinity() || (!a.end.isInfinity() && a.end.getValue().compareTo(end.getValue()) == -1 ) )
			newEnd = a.end;
		else
			newEnd = end;
		
		return new Segment(newBegin, newEnd);
	}
}
