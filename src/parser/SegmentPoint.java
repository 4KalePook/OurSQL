package parser;

import dbTypes.DBTypes;

public class SegmentPoint implements Comparable<SegmentPoint> {
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