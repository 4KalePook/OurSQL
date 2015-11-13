package parser;
import database.Database;

public class ConditionCalc {
	private Database database;
	public ConditionCalc(Database database){
		this.database = database;
	}
	public  boolean calculate(String tuple){//Calculate Tuple Condition
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
					{System.out.println("MisMatch Parenthesis\n <"+tuple+ ">");return false;}
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
		String ColName = tuple.substring(0,i);
		int type = getType(ColName)=="String"?0:1;
		String sub = Clean(tuple.substring(i,tuple.length()));
		if(type == 0){
			String value = getStrValue(ColName);
			int compareFrom2 = value.compareTo(StrCompVal(sub.substring(2, sub.length() ) )) ;
			int compareFrom1 = value.compareTo(StrCompVal(sub.substring(1, sub.length() ) )) ;
			//TODO: compare < negative == 0 > positive ?
			if(sub.startsWith("<=") || sub.startsWith("=<") )
				return compareFrom2 <= 0;
			if(sub.startsWith(">=") || sub.startsWith("=>") )
				return compareFrom2 >= 0;
			if(sub.startsWith(">") )
				return compareFrom1 <  0;				
			if(sub.startsWith(">") )
				return compareFrom1 >  0;
			if(sub.startsWith("="))
				return compareFrom1 == 0;
		}
		if(type == 1){
			int value = getIntValue(ColName);
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
		System.out.println("(Err)Not a standard Tupple Condition\n <"+tuple+">");
		return false;
	}
	
	private  boolean CalcIn(boolean A, String intuple){//Calculate inner Tuple Condition
		intuple = Clean(intuple);
		if(intuple.startsWith("AND"))
			return (A && calculate(intuple.substring(3,intuple.length())));
		if(intuple.startsWith("OR"))
			return (A || calculate(intuple.substring(2,intuple.length())));
		
		System.out.println("(Err)Not a standard inner Tupple Condition:\n"+intuple);
		return false;
	}
	
	private  String StrCompVal(String str){
		//TODO: field of record
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
		// if we reach this point this means the (int)compare starts with a field
		int i = 0;
		while(i<str.length()&& (is_digit(str.charAt(i))||is_letter(str.charAt(i))))
			i++;
		String value = getStrField(str.substring(0,i));
		if(i==str.length())
			return value;
		if(i!=0)
			return InStrCompVal(value, str.substring(i, str.length()));
		
		
		//TODO: why reach here
		return "";
	}
	private  String InStrCompVal(String str1, String str2){
		str2 = Clean(str2);
		if(str2.startsWith("+"))
			return str1 + StrCompVal(str2.substring(1,str2.length()));
		
		System.out.println("(Err)Not a standard inner str comp :\n <"+str2+">");
		return "";
	}
	
	private  int IntCompVal(String comp){
		//TODO:field of record
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
		int value = getIntField(comp.substring(0,i));
		if(i==comp.length())
			return value;
		if(i!=0)
			return inIntCompVal(value, comp.substring(i, comp.length()));
		
		//TODO: why reach here
		return 0;
	}
	
	private  int inIntCompVal(int numb, String incomp){
		incomp = Clean(incomp);
		if(incomp.startsWith("+"))
			return numb+IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("-"))
			return numb-IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("*"))
			return numb*IntCompVal(incomp.substring(1,incomp.length()));
		if(incomp.startsWith("/"))
			return numb/IntCompVal(incomp.substring(1,incomp.length()));
		
		//TODO:why reach here
		return 0;
	}
	
	private  String Clean(String input){
		int i =0;
		while(input.charAt(i)==' ')
			i++;
		int j = input.length();
		while(input.charAt(j-1)==' ')
			j--;
		if(j<i)
			{System.out.println("(Err)Empty string to clean!");return "";}
		
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
	private  int getIntValue(String a){
		//TODO: How?
		if(a.equals("IK"))
			return 3;
		if(a.equals("IS"))
			return 7;
		return 0;
	}
	private  String getStrValue(String a){
		//TODO: How?
		if(a.equals("SS"))
			return "salam";
		if(a.equals("SK"))
			return "bye";
		return "";
	}
	private  String getType(String a){
		//TODO: How?
		if(a.equals("IK")
		|| a.equals("IS")
		)
			return "INT";
		if(a.equals("SK")
		|| a.equals("SS")
		)
			return "STRING";
		return "STRING";
		//return "INT";
	}
	private  int getIntField(String a){
		if(a.equals("i5"))
			return 5;
		if(a.equals("i1"))
			return 1;
		return 0;
	}
	private  String getStrField(String a){
		if(a.equals("sb"))
			return "b";
		if(a.equals("st"))
			return "t";
		return "";
	}
}