package parser;

public class Segment {
	private SegmentPoint begin, end;
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
	
	public SegmentPoint getBegin() {
		return begin;
	}
	
	public SegmentPoint getEnd() {
		return end;
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
