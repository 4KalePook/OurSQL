package parser;

import dbTypes.*;


public class ConditionSegCalc {
	
	Segment inf = new Segment(new SegmentPoint(false), new SegmentPoint(true));
	Segment empty_varchar = new Segment(new SegmentPoint(new VARCHAR (""), false), new SegmentPoint(new VARCHAR (""), false) );
	Segment empty_int = new Segment(new SegmentPoint(new INT((long)0),false), new SegmentPoint(new INT((long)0),false));
	private final static long Null = -2029391002;
	
	private boolean tables;	//more than one table then is true
	private String table_name1, table_name2;
	public ConditionSegCalc(){
		tables = false;
	}
	public ConditionSegCalc(String table_name1, String table_name2){
		this.table_name1 = table_name1;
		this.table_name2 = table_name2;
		if(table_name2==null || table_name2.equals(""))
			tables = false;
		else{
			tables = true;
		}
		
	}
	
	
public Segment calculate(String tuple, String field_name ,int type){//Calculate Tuple Condition
		tuple = Clean(tuple);
		if(tuple.equals("FALSE"))
			return null;
		if(tuple.equals("TRUE"))
			return null;
		if(tuple.charAt(0)=='('){
			int j = 0;
			int depth = 1;
			while(depth!=0){
				j++;
				if(j>=tuple.length())
					{System.err.println("MisMatch Parenthesis\n <"+tuple+ ">");return null;}
				if(tuple.charAt(j)=='(')
					depth++;
				if(tuple.charAt(j)==')')
					depth--;
			}
			if(j==tuple.length()-1)
					return calculate(tuple.substring(1,j), field_name, type);
			String sub1 = tuple.substring(1,j);
			String sub2 = tuple.substring(j+1,tuple.length());
			return CalcIn(calculate(sub1, field_name, type),sub2,  field_name, type);
		}
		if(tuple.startsWith("NOT"))
			return inf;
		
		// if we reach this point this means the tuple_condition starts with COL_NAME
		int i=0;
		int j=0;
		while(i<tuple.length() && (is_letter(tuple.charAt(i)) ||  is_digit(tuple.charAt(i) ) ) )
			i++;
		String table_name=table_name1;
		if(i<tuple.length() && tuple.charAt(i)=='.'){
			table_name = tuple.substring(0,i);
			j=i+1;
			i++;
			while(i<tuple.length() && (is_letter(tuple.charAt(i)) ||  is_digit(tuple.charAt(i) ) ) )
				i++;
		}
		String ColName = tuple.substring(j,i);
		if(!Clean(ColName).equals(field_name))
			return inf;
		
		String sub = Clean(tuple.substring(i,tuple.length()));
		if(type == 0){
			String Calc = null;
			if(sub.startsWith("<=")||sub.startsWith(">="))
				Calc = StrCompVal(sub.substring(2, sub.length() ) );
			if(sub.startsWith("<")||sub.startsWith(">")||sub.startsWith("="))
				Calc = StrCompVal(sub.substring(1, sub.length() ) );
			if(Calc == null)
				return inf;
			
			if(sub.startsWith("<=") || sub.startsWith("=<") )
				return new Segment(new SegmentPoint(false), new SegmentPoint(new VARCHAR(Calc), true));
			if(sub.startsWith(">=") || sub.startsWith("=>") )
				return new Segment(new SegmentPoint(new VARCHAR(Calc), true),new SegmentPoint(true));
			if(sub.startsWith("<") )
				return new Segment(new SegmentPoint(false), new SegmentPoint(new VARCHAR(Calc), false));				
			if(sub.startsWith(">") )
				return new Segment(new SegmentPoint(new VARCHAR(Calc), false),new SegmentPoint(true));
			if(sub.startsWith("="))
				return new Segment(new SegmentPoint(new VARCHAR(Calc), true),
									new SegmentPoint(new VARCHAR(Calc), true));				
		}
		if(type == 1){
			sub = Clean(sub);
			long Calc = Null;
			if(sub.startsWith("<=")||sub.startsWith(">="))
				Calc = IntCompVal(sub.substring(2, sub.length() ) );
			if(sub.startsWith("<")||sub.startsWith(">")||sub.startsWith("="))
				Calc = IntCompVal(sub.substring(1, sub.length() ) );
			if(Calc == Null)
				return inf;
			
			if(sub.startsWith("<=") || sub.startsWith("=<") )
				return new Segment(new SegmentPoint(false), new SegmentPoint(new INT(Calc), true));
			if(sub.startsWith(">=") || sub.startsWith("=>") )
				return new Segment(new SegmentPoint(new INT(Calc), true),new SegmentPoint(true));
			if(sub.startsWith("<") )
				return new Segment(new SegmentPoint(false), new SegmentPoint(new INT(Calc), false));				
			if(sub.startsWith(">") )
				return new Segment(new SegmentPoint(new INT(Calc), false),new SegmentPoint(true));
			if(sub.startsWith("="))
				return new Segment(new SegmentPoint(new INT(Calc), true),
									new SegmentPoint(new INT(Calc), true));
		}
		//if we reach this point this means the string isn't a standard tuple condition
		System.err.println("(Err)Not a standard Tupple Condition\n <"+tuple+">: ["+ColName+"]+["+sub+"] type:"+type);
		if(type==0)
			return empty_varchar;
		else
			return empty_int;
	}
	
	private  Segment CalcIn(Segment A, String intuple, String field_name ,int type){//Calculate inner Tuple Condition
		intuple = Clean(intuple);
		if(intuple.startsWith("AND"))
			return A.intersect(calculate(intuple.substring(3,intuple.length()),field_name, type));
		if(intuple.startsWith("OR"))
			return A.union(calculate(intuple.substring(3,intuple.length()),field_name, type));
		
		System.err.println("(Err)Not a standard inner Tupple Condition:\n"+intuple);
		if(type==0)
			return empty_varchar;
		else
			return empty_int;
			
	}
	
	public  String StrCompVal(String str){
		str = Clean(str);
		if(str.charAt(0)== '"'){
			int i = 1;
			while(str.charAt(i)!='"')
				i++;
			if(i==str.length()-1)
				return str.substring(1, i);
			String sub1 = str.substring(1, i);
			String sub2 = str.substring(i+1, str.length());
			return InStrCompVal(sub1, sub2);
		}
		return null;
	}
	private  String InStrCompVal(String str1, String str2){
		str2 = Clean(str2);
		if(str2.startsWith("+"))
			return str1 + StrCompVal(str2.substring(1,str2.length()));
		
		System.err.println("(Err)Not a standard inner str comp :\n <"+str2+">");
		return "";
	}
	
	public long IntCompVal(String comp){
		comp = Clean(comp);
		int i = 0;
		int sign = 1;
		if(comp.startsWith("-")){
			i++;
		}
		int numb = 0;
		while(i<comp.length()&& is_digit(comp.charAt(i))){
			numb*=10;
			numb+=comp.charAt(i)-'0';
			i++;
		}
		if(i==comp.length())
			return sign * numb;
		if(i!=0)
			return inIntCompVal(sign * numb, comp.substring(i, comp.length()));
		
		
		///////////////////////////
		return Null;
	}
	
	private long inIntCompVal(long numb, String incomp){
		incomp = Clean(incomp);
		if(incomp.startsWith("+"))
			return numb+IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("-"))
			return numb-IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("*"))
			return numb*IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("/"))
			return numb/IntCompVal(incomp.substring(1,incomp.length()));
		
		System.err.println("(Err)Not a standard inner int comp :\n <"+incomp+">");
		
		return 0;
	}
////////////////////////////////////////////////////////////////////////////	
	private  String Clean(String input){
		int i =0;
		while(i<input.length() && input.charAt(i)==' ')
			i++;
		if(i==input.length())
			{System.err.println("(Err)Empty string to clean!");return "";}
		int j = input.length();
		while(input.charAt(j-1)==' ' || input.charAt(j-1)==';' )
			j--;
			
		
		return input.substring(i,j);
	}
//////////////////////////////////////////////////////////////////////////	
	private  boolean is_letter(char c){
		if('a'<=c && c<='z')
			return true;
		if('A'<=c && c<='Z')
			return true;
		return false;
	}
	private  boolean is_digit(char c){
		if('0'<=c && c<='9')
			return true;
		return false;
	}
//////////////////////////////////////////////////////////////////////////	
	
//	private  long getIntValue(String a, int table){
//
//		if(table == 1)
//			return ((long)myrow.get(a).getValue());
//		else
//			return ((long)myrow2.get(a).getValue());
//	}
//	
//	private  String getStrValue(String a, int table){
//		if(table==1)
//			return (String)myrow.get(a).getValue();
//		else
//			return (String)myrow.get(a).getValue();
//	}
	
//	private  int getType(String a){
//		return 0;
//		//string 0 int 1
//			
//	}
//	
//	private  int getType(String a, int table){
//		return 0;
//		//string 0 int 1
//		
//			
//	}
}