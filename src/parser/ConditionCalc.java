package parser;
import database.Database;

public class ConditionCalc {
	private Database database;
	public ConditionCalc(Database database){
		this.database = database;
	}
	public static boolean calculate(String tuple){//Calculate Tuple Condition
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
			if(sub.startsWith("="))
				return value.equals( StrCompVal(sub.substring(2, sub.length() ) ) );
		}
		if(type == 1){
			int value = getIntValue(ColName);
			sub = Clean(sub);
			if(sub.startsWith("<=") || sub.startsWith("=<"))
				return (value <= IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith(">=") || sub.startsWith("=>"))
				return (value >= IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith(">") )
				return (value > IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith("<"))
				return (value < IntCompVal(sub.substring(2, sub.length())));
			if(sub.startsWith("="))
				return (value == IntCompVal(sub.substring(2, sub.length())));
		}
		//if we reach this point this means the string isn't a standard tuple condition
		System.out.println("(Err)Not a standard Tupple Condition\n <"+tuple+">");
		return false;
	}
	
	private static boolean CalcIn(boolean A, String intuple){//Calculate inner Tuple Condition
		intuple = Clean(intuple);
		if(intuple.startsWith("AND"))
			return (A && calculate(intuple.substring(3,intuple.length())));
		if(intuple.startsWith("OR"))
			return (A || calculate(intuple.substring(2,intuple.length())));
		
		System.out.println("(Err)Not a standard inner Tupple Condition:\n"+intuple);
		return false;
	}
	
	private static String StrCompVal(String str){
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
		
		
		//TODO: why reach here
		return "";
	}
	private static String InStrCompVal(String str1, String str2){
		str2 = Clean(str2);
		if(str2.startsWith("+"))
			return str1 + StrCompVal(str2.substring(1,str2.length()));
		
		System.out.println("(Err)Not a standard inner str comp :\n <"+str2+">");
		return "";
	}
	
	private static int IntCompVal(String comp){
		//TODO:field of record
		int i = 0;
		int numb = 0;
		while(is_digit(comp.charAt(i))){
			numb*=10;
			numb+=comp.charAt(i)-'0';
			i++;
		}
		if(i!=0)
			return inIntCompVal(numb, comp.substring(i, comp.length()));
		
		
		//TODO: why reach here
		return 0;
	}
	
	private static int inIntCompVal(int numb, String incomp){
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
	
	private static String Clean(String input){
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
	private static boolean is_letter(char c){
		if('a'<=c && c<='z')
			return true;
		if('A'<=c && c<='Z')
			return true;
		return false;
	}
	private static boolean is_digit(char c){
		if('0'<=c && c<='9')
			return true;
		return false;
	}
	private static int getIntValue(String a){
		return 0;
	}
	private static String getStrValue(String a){
		return "";
	}
	private static String getType(String a){
		return "STRING";
		//return "INT";
	}
}
