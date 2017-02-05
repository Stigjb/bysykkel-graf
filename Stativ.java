import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.io.PrintWriter;

public class Stativ extends HashMap<String, List<Long>> {
	public final String id;
	private List<Kant> naboer = new ArrayList<>();
	boolean besokt = false;
	long avstand = Long.MAX_VALUE;
	private String navn;
	private Stativ parent;
	
	private class Kant implements Comparable<Kant> {
		String til;
		Long vekt;
		
		Kant(String til, Long vekt) {
			this.til = til;
			this.vekt = vekt;
		}
		
		@Override
		public int compareTo(Kant k) {
			return Long.compare(Main.getTur(this.til).avstand,
								Main.getTur(k.til).avstand);
		}
	}
	
	public Stativ(String id, String navn) {
		this.id = id;
		this.navn = navn;
		if (navn == null) {
			System.out.println(id + " gir null");
		}
	}
	
	public void addTrip(String slutt, long tid) {
		if (!containsKey(slutt)) {
			put(slutt, new ArrayList<Long>());
		}
		get(slutt).add(tid);
	}
	
	public void beregnNaboer() {
		for (String s : keySet()) {
			long vekt = median(get(s));
			naboer.add(new Kant(s, vekt));
		}
	}
	
	private static long median(List<Long> l) {
		l.sort(null);
		if (l.size() % 2 == 1) {
			return l.get(l.size() / 2);
		}
		return (l.get(l.size() / 2) + l.get(l.size() / 2 - 1)) / 2;
	}
	
	public void dfs() {
		besokt = true;
		for (Kant k : naboer) {
			Stativ s = Main.getTur(k.til);
			if (!s.besokt) {
				s.dfs();
				System.out.printf("%s -> %s%n", id, s.id);
			}
		}
	}
	
	public ArrayList<String> dijkstra() {
		ArrayList<String> nodes = new ArrayList<>();
		
		this.avstand = 0;
		HashSet<Stativ> tentative = new HashSet<>();
		
		besokt = true;
		
		for (Kant k : naboer) {
			Stativ s = Main.getTur(k.til);
			if (!s.besokt) {
				if (this.avstand + k.vekt < s.avstand) {
					s.avstand = this.avstand + k.vekt;
					s.parent = this;
					tentative.add(s);
				}
			}
		}
		
		while (!tentative.isEmpty()) {
			Stativ current = Collections.min(tentative,
				(a, b) -> Long.compare(a.avstand, b.avstand));
			
			current.besokt = true;
			tentative.remove(current);
			
			nodes.add(current.parent.id);
			nodes.add(current.id);
			
			for (Kant k : current.naboer) {
				Stativ s = Main.getTur(k.til);
				if (!s.besokt) {
					if (current.avstand + k.vekt < s.avstand) {
						s.avstand = current.avstand + k.vekt;
						s.parent = current;
						tentative.add(s);
					}
				}
			}
		}
		return nodes;
	}
	
	public void unvisit() {
		besokt = false;
	}
	
	public boolean isVisited() {
		return besokt;
	}
	
	public boolean isNext(Collection<Stativ> c) {
		for (Stativ s : c) {
			if (s.avstand < Long.MAX_VALUE) {
				return true;
			}
		}
		if (c.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
