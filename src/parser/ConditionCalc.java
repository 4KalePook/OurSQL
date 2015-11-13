package parser;
import java.util.HashMap;

import dbTypes.DBTypes;
import dbTypes.VARCHAR;
import table.*;
//import dbTypes.DBTypes;




public class ConditionCalc {
	
	private DBObject mydb;
	private HashMap<String, DBTypes> myrow;
	public ConditionCalc(DBObject inputdb){
		this.mydb = inputdb;
		 myrow = mydb.getRow();
	}
//	public List<String> Fields(String s){
//		List<String> ans;
//		while(s.length()==0){
//			if(!is_letter(s.charAt(0)) && !is_digit(s.charAt(0)))
//				s = s.substring(1,s.length());
//			if(!is_letter(s.charAt(s.length()-1)) && !is_digit(s.charAt(s.length()-1)))
//				s = s.substring(0,s.length()-1);
//			
//		}
//		
//		return null;
//	}
	
	
	
	
	public boolean calculate(String tuple){//Calculate Tuple Condition
		tuple = Clean(tuple);
		if(tuple.equals("FALSE"))
			return false;
		if(tuple.equals("TRUE"))
			return true;
		if(tuple.charAt(0)=='('){
			int j = 0;
			int depth = 1;
			while(depth!=0){
				j++;
				if(j>=tuple.length())
					{System.err.println("MisMatch Parenthesis\n <"+tuple+ ">");return false;}
				if(tuple.charAt(j)=='(')
					depth++;
				if(tuple.charAt(j)==')')
					depth--;
			}
			if(j==tuple.length()-1)
					return calculate(tuple.substring(1,j));
			String sub1 = tuple.substring(1,j);
			String sub2 = tuple.substring(j+1,tuple.length());
			return CalcIn(calculate(sub1),sub2);
		}
		if(tuple.startsWith("NOT"))
			return !(calculate(tuple.substring(3,tuple.length())));
		// if we reach this point this means the tuple_condition starts with COL_NAME
		int i=0;
		while(i<tuple.length() && is_letter(tuple.charAt(i)) ||  is_digit(tuple.charAt(i)))
			i++;
		//System.err.println(tuple+"||\n");
		String ColName = tuple.substring(0,i);
		int type = getType(ColName);
		String sub = Clean(tuple.substring(i,tuple.length()));
		//System.err.println(sub);
		if(type == 0){
			String value = getStrValue(ColName);
			if(sub.startsWith("<=") || sub.startsWith("=<") ){
				return value.compareTo(StrCompVal(sub.substring(2, sub.length() ) )) <= 0;
			}
			if(sub.startsWith(">=") || sub.startsWith("=>") )
				return value.compareTo(StrCompVal(sub.substring(2, sub.length() ) )) >= 0;
			if(sub.startsWith(">") )
				return value.compareTo(StrCompVal(sub.substring(1, sub.length() ) )) <  0;				
			if(sub.startsWith(">") )
				return value.compareTo(StrCompVal(sub.substring(1, sub.length() ) )) >  0;
			if(sub.startsWith("="))
				return value.compareTo(StrCompVal(sub.substring(1, sub.length() ) )) == 0;
		}
		if(type == 1){
			long value = getIntValue(ColName);
			sub = Clean(sub);
			if(sub.startsWith("<=") || sub.startsWith("=<"))
				return (value <= IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith(">=") || sub.startsWith("=>"))
				return (value >= IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith(">") )
				return (value > IntCompVal(sub.substring(1, sub.length())));
			if(sub.startsWith("<"))
				return (value < IntCompVal(sub.substring(1, sub.length())));
			if(sub.startsWith("="))
				return (value == IntCompVal(sub.substring(1, sub.length())));
		}
		//if we reach this point this means the string isn't a standard tuple condition
		System.err.println("(Err)Not a standard Tupple Condition\n <"+tuple+">");
		return false;
	}
	
	private  boolean CalcIn(boolean A, String intuple){//Calculate inner Tuple Condition
		intuple = Clean(intuple);
		if(intuple.startsWith("AND"))
			return (A && calculate(intuple.substring(3,intuple.length())));
		if(intuple.startsWith("OR"))
			return (A || calculate(intuple.substring(2,intuple.length())));
		
		System.err.println("(Err)Not a standard inner Tupple Condition:\n"+intuple);
		return false;
	}
	
	public  String StrCompVal(String str){
		str = Clean(str);
	//	System.err.println(str+"||\n");
		if(str.charAt(0)== '"'){
			int i = 1;
			while(str.charAt(i)!='"')
				i++;
			if(i==str.length()-1)
				return str.substring(1, i);
			String sub1 = str.substring(1, i);
			String sub2 = str.substring(i+1, str.length());
	//		System.err.println(sub1+","+sub2+"|\n");
			return InStrCompVal(sub1, sub2);
		}
		// if we reach this point this means the (int)compare starts with a field
	//	System.err.println(str+"||\n");
		int i = 0;
		while(i<str.length()&& (is_digit(str.charAt(i))||is_letter(str.charAt(i))))
			i++;
		String value = getStrValue(str.substring(0,i));
		if(i==str.length())
			return value;
		if(i!=0)
			return InStrCompVal(value, str.substring(i, str.length()));
		
		
		return "";
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
		int numb = 0;
		while(i<comp.length()&& is_digit(comp.charAt(i))){
			numb*=10;
			numb+=comp.charAt(i)-'0';
			i++;
		}
		if(i==comp.length())
			return numb;
		if(i!=0)
			return inIntCompVal(numb, comp.substring(i, comp.length()));
		// if we reach this point this means the (int)compare starts with a field
		// i == 0
		while(i<comp.length()&& (is_digit(comp.charAt(i))||is_letter(comp.charAt(i))))
			i++;
		long value = getIntValue(comp.substring(0,i));
		if(i==comp.length())
			return value;
		if(i!=0)
			return inIntCompVal(value, comp.substring(i, comp.length()));
		
		//bad int comp val
		return 0;
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
		
		//Bad in int compVal
		
		return 0;
	}
	
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
	private  boolean is_letter(char c){
		if('a'<=c && c<='z')
			return true;
		if('A'<=c && c<='Z')
			return true;
		return false;
	}
	private  boolean is_digit(char c){
		//TODO: How?
		if('0'<=c && c<='9')
			return true;
		return false;
	}
	private  long getIntValue(String a){
		return ((long)myrow.get(a).getValue());
	}
	private  String getStrValue(String a){
		//if(!myrow.containsKey(a))
			//System.out.println(a);
		return (String)myrow.get(a).getValue();
	}
	private  int getType(String a){
		//TODO: How?
		if(!myrow.containsKey(a))
			System.err.println(a+" isn't in hash map");
		if(myrow.get(a).getClass().equals(VARCHAR.class))
			return 0;
		else
			return 1;
			
		
		//return "INT";
	}
	
}