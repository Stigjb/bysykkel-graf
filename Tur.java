import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class Tur {
	final String start;
	final String slutt;
	public final long varighet;
	private static String dateString = "yyyy-MM-dd HH:mm:ss ZZZZZ";
	private static DateFormat df = new SimpleDateFormat(dateString);
	
	public Tur(String start, String t0, String slutt, String t1) {
		this.start = start;
		this.slutt = start;
		long tid = Long.MAX_VALUE;
		try {
			Date tid0 = df.parse(t0);
			Date tid1 = df.parse(t1);
			tid = tid1.getTime() - tid0.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.varighet = tid * tid;
	}
}
