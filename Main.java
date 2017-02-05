import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.io.PrintWriter;

public class Main {
	private static HashMap<String, Stativ> turer = new HashMap<>();
	private static HashMap<String, String> navn = new HashMap<>();
	private static OutputFormat outputFormat;
	private static String outFileName;
	private static String outFileStart;
	private static String outFileEnd;
	private static String outFilePairSep;
	private static String outFileLineSep;
	private static String startNode;
	
	public static void main(String[] args) {
		parseArgs(args);
		
		File f = null;
		Scanner in = null;
		byggNavn();
		
		try {
			f = new File("trips-2016.8.1-2016.8.31.csv");
			in = new Scanner(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		in.useDelimiter("[,\\r\\n]");
		
		System.out.println("Leser inn data ...");
		
		in.nextLine();
		while(in.hasNext()) {
			String start = in.next();
			String t0 = in.next();
			String slutt = in.next();
			String t1 = in.next();
			Tur t = new Tur(start, t0, slutt, t1);
			if (t.varighet > 30000 && !start.equals(slutt)) {
				// Bare turer over 30 sekunder som ikke slutter der de startet
				if (!turer.containsKey(start)) {
					turer.put(start, new Stativ(start, getNavn(start)));
				}
				if (!turer.containsKey(slutt)) {
					turer.put(slutt, new Stativ(slutt, getNavn(slutt)));
				}
				turer.get(start).addTrip(slutt, t.varighet);
				turer.get(slutt).addTrip(start, t.varighet);
			}
		}
		
		System.out.println("Beregner nabodata ...");
		
		for (Stativ s : turer.values()) {
			s.beregnNaboer();
		}
		
		System.out.println("Genererer grafrepresentasjon ...");
		
		// Beregn korteste sti startnode til alle
		turer.values().forEach((s) -> s.unvisit());
		ArrayList<String> nodes = getTur(startNode).dijkstra();
		
		System.out.println("Skriver ut til " + outFileName + " ...");
		
		PrintWriter ut = null;
		try {
			ut = new PrintWriter(new File(outFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ut.println(outFileStart);
		for (int i = 0; i < nodes.size(); i += 2) {
			ut.printf(outFilePairSep, nodes.get(i), nodes.get(i + 1));
			if (nodes.size() - i > 2) {
				ut.println(outFileLineSep);
			}
		}
		ut.println(outFileEnd);
		
		ut.close();
	}
	
	private static void parseArgs(String[] args) {
		try {
			if (args[0].equals("--json")) {
				outFileName = "ut.json";
				outFileStart = "[";
				outFilePairSep = "  [[\"%s\"], [\"%s\"]]";
				outFileLineSep = ",";
				outFileEnd = "\n]";
			} else {
				outFileName = "ut.gv";
				outFileStart = "digraph G {";
				outFilePairSep = "  \"%s\" -> \"%s\"";
				outFileLineSep = ",";
				outFileEnd = "\n}";
			}
			startNode = args[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			String[] defaultArgs = {"--gv", "246"};
			parseArgs(defaultArgs);
		}
	}
	
	public static Stativ getTur(String s) {
		return turer.get(s);
	}
	
	private static void byggNavn() {
		try {
			Scanner inn = new Scanner(new File("stasjoner.txt"));
			while (inn.hasNextLine()) {
				String id = inn.nextLine();
				id = id.substring(6, id.length() - 1);
				String tittel = inn.nextLine();
				tittel = tittel.substring(10, tittel.length() - 2);
				navn.put(id, tittel);
			}
		} catch (IOException e) {
			// pass
		}
	}
	
	public static String getNavn(String id) {
		return navn.get(id);
	}
}