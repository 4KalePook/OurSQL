package parser;

import dbTypes.DBTypes;

class SegmentPoint implements Comparable<SegmentPoint> {
	private boolean isInf, positiveInf, isInc;
	private DBTypes value;
	
	public boolean isInfinity() {
		return isInf;
	}
	
	public boolean posInfinity() {
		return isInf && positiveInf;
	}
	
	public boolean negInfinity() {
		return isInf && (!positiveInf);
	}
	
	public boolean isInclusive(){
		return isInc;
	}
	
	public DBTypes getValue() {
		return value;
	}
	
	public SegmentPoint(DBTypes value, boolean isInclusive) {
		this.isInf = false;
		this.positiveInf = false;
		this.isInc = isInclusive;
		this.value = value;
	}
	
	public SegmentPoint(boolean positive) {
		this.isInf = true;
		this.positiveInf = positive;
		this.value = null;
		this.isInc = false;
	}
	
	@Override
	public int compareTo(SegmentPoint other) {
		if( posInfinity() || other.posInfinity() ) {
			if( !posInfinity() )
				return -1;
			if( !other.posInfinity() )
				return 1;
			return 0;
		}
		
		if( negInfinity() || other.negInfinity() ) {
			if( !negInfinity() )
				return 1;
			if( !other.negInfinity() )
				return -1;
			return 0;
		}
		
		if( value.equals(other.getValue()) )
			return 0;  // this doesn't mean they actually are equal
		
		return value.compareTo(other.getValue());
	}
	
	@Override
	public boolean equals(Object other){
		if( !(other instanceof SegmentPoint) )
			return false;
		if( this == other )
			return true;
		return ( compareTo((SegmentPoint)other) == 0 ); 
	}
	
}

public class Segment {
	public SegmentPoint begin, end;
	public void Print(){
		if(begin.getValue()!=null)
			System.err.println(begin.getValue().getValue());
		else
			System.err.println(begin.getValue());
		if(end.getValue()!=null)
			System.err.println(end.getValue().getValue());
		else
			System.err.println(end.getValue());
	}
	
	Segment(SegmentPoint begin, SegmentPoint end) {
		this.begin = begin;
		this.end = end;
	}
	
	public Segment union(Segment a) {
		SegmentPoint newBegin, newEnd;
		
		int compBegin = begin.compareTo(a.begin);
		if( compBegin == -1 )
			newBegin = begin;
		else if( compBegin == 1 )
			newBegin = a.begin;
		else if( begin.isInclusive() )
			newBegin = begin;
		else
			newBegin = a.begin;
		
		int compEnd = end.compareTo(a.end);
		if( compEnd == 1 )
			newEnd = end;
		else if( compEnd == -1 )
			newEnd = a.end;
		else if( end.isInfinity() )
			newEnd = end;
		else if( end.isInclusive() )
			newEnd = end;
		else
			newEnd = a.end;
		
		return new Segment(newBegin, newEnd);
	}
	
	public Segment intersect(Segment a) {
		SegmentPoint newBegin, newEnd;
		
		int compBegin = begin.compareTo(a.begin);
		if( compBegin == 1 )
			newBegin = begin;
		else if( compBegin == -1 )
			newBegin = a.begin;
		else if( begin.isInfinity() )
			newBegin = begin;
		else if( begin.isInclusive() )  // values are equal
			newBegin = a.begin;
		else
			newBegin = begin;
		
		
		int compEnd = end.compareTo(a.end); 
		if( compEnd == -1 )
			newEnd = end;
		else if( compEnd == 1 )
			newEnd = a.end;
		else if( begin.isInfinity() )
			newEnd = end;
		else if( begin.isInclusive() )  // values are equal
			newEnd = a.end;
		else
			newEnd = end;
		
		return new Segment(newBegin, newEnd);
	}
}
