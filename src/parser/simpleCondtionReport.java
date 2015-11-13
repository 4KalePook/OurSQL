package parser;
import java.util.List;

public class simpleCondtionReport {
	public List<Integer> List;
	
	
	
	public boolean calculate(String tuple){//Calculate Tuple Condition
		List.clear();
		
		tuple = Clean(tuple);
		if(tuple.startsWith("("))
			
			
			;
		return false;
//			
//		
////		if(tuple.equals("FALSE"))
//			return false;
//		if(tuple.equals("TRUE"))
//			return true;
//		if(tuple.charAt(0)=='('){
//			int j = 0;
//			int depth = 1;
//			while(depth!=0){
//				j++;
//				if(j>=tuple.length())
//					{System.out.println("MisMatch Parenthesis\n <"+tuple+ ">");return false;}
//				if(tuple.charAt(j)=='(')
//					depth++;
//				if(tuple.charAt(j)==')')
//					depth--;
//			}
//			if(j==tuple.length()-1)
//					return calculate(tuple.substring(1,j));
//			String sub1 = tuple.substring(1,j);
//			String sub2 = tuple.substring(j+1,tuple.length());
//			return CalcIn(calculate(sub1),sub2);
//		}
//		if(tuple.startsWith("NOT"))
//			return !(calculate(tuple.substring(3,tuple.length())));
//		// if we reach this point this means the tuple_condition starts with COL_NAME
//		int i=0;
//		while(i<tuple.length() && is_letter(tuple.charAt(i)) ||  is_digit(tuple.charAt(i)))
//			i++;
//		String ColName = tuple.substring(0,i);
//		int type = getType(ColName)=="String"?0:1;
//		String sub = Clean(tuple.substring(i,tuple.length()));
//		if(type == 0){
//			String value = getStrValue(ColName);
//			int compareFrom2 = value.compareTo(StrCompVal(sub.substring(2, sub.length() ) )) ;
//			int compareFrom1 = value.compareTo(StrCompVal(sub.substring(1, sub.length() ) )) ;
//			//TODO: compare < negative == 0 > positive ?
//			if(sub.startsWith("<=") || sub.startsWith("=<") )
//				return compareFrom2 <= 0;
//			if(sub.startsWith(">=") || sub.startsWith("=>") )
//				return compareFrom2 >= 0;
//			if(sub.startsWith(">") )
//				return compareFrom1 <  0;				
//			if(sub.startsWith(">") )
//				return compareFrom1 >  0;
//			if(sub.startsWith("="))
//				return compareFrom1 == 0;
//		}
//		if(type == 1){
//			int value = getIntValue(ColName);
//			sub = Clean(sub);
//			if(sub.startsWith("<=") || sub.startsWith("=<"))
//				return (value <= IntCompVal(sub.substring(2, sub.length())));
//			if(sub.startsWith(">=") || sub.startsWith("=>"))
//				return (value >= IntCompVal(sub.substring(2, sub.length())));
//			if(sub.startsWith(">") )
//				return (value > IntCompVal(sub.substring(1, sub.length())));
//			if(sub.startsWith("<"))
//				return (value < IntCompVal(sub.substring(1, sub.length())));
//			if(sub.startsWith("="))
//				return (value == IntCompVal(sub.substring(1, sub.length())));
//		}
//		//if we reach this point this means the string isn't a standard tuple condition
//		System.out.println("(Err)Not a standard Tupple Condition\n <"+tuple+">");
//		return false;
	}

	private  String Clean(String input){
		int i =0;
		while(input.charAt(i)==' ')
			i++;
		int j = input.length();
		while(input.charAt(j-1)==' ' || input.charAt(j-1)==';')
			j--;
		if(j<i)
			{System.err.println("(Err)Empty string to clean!");return "";}
		
		return input.substring(i,j);
	}


}
