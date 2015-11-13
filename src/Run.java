
import java.util.Scanner;

import database.Database;
import parser.ParseCommand;
import parser.ParserTypes;

public class Run {
	public static void main(String[] arg)
	{
		Database database;
		database = new Database();
		
		Scanner scanner = new Scanner(System.in);
		ParseCommand parser = new ParseCommand();
		ParserTypes parsedCommand;
//		System.out.println(CommandTypes.valueOf("CREATE_TABLE").getCommandText());
		while(true)
		{
			String line =scanner.nextLine();
			if(line=="")
				continue;
			parsedCommand = parser.parseCommand(line);
			String result = parsedCommand.action(database);
			if(result!=null)
				System.out.println(result);
		}
//		scanner.close();

		
//		HashMap<String, DBTypes> x = new HashMap<>();
//		x.put("gav", new INT(5l));
//		HashMap<String, DBTypes> y = new HashMap<String, DBTypes>(x);
//		x.put("khar", new INT(6l));
//		LinkedList<DBTypes> alphaNumerical = new LinkedList<>();
//		alphaNumerical.add(new INT(5l));
//		alphaNumerical.add(new VARCHAR("salam"));
//		System.out.println(x.get("gav").getValue());
//		System.out.println(y.get("gav").getValue());
	}
}