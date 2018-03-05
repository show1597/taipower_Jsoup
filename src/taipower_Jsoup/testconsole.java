package taipower_Jsoup;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class testconsole {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] URL= {"http://www.taipower.com.tw/tc/rssNews.ashx","http://www.taipower.com.tw/tc/rssEvents.ashx","http://www.taipower.com.tw/tc/rssMeasures.ashx"};
		Parsing_banner();
		for(int i=0;i< URL.length;i++)
		{
			Parsing_news(URL[i]);
		}
	}
	//parding½m²ß-¹Ï¤ù
	public static void Parsing_banner() throws Exception{
		Document doc = Jsoup.connect("http://www.taipower.com.tw/tc/index.aspx").get();
		System.out.print(doc.title());
		Elements newsHeadlines = doc.select("#pic a,#pic img");
		for (Element headline : newsHeadlines) {
			System.out.print(headline.absUrl("src")+" "+headline.absUrl("href")+"\n");
		}
	}
	//Parsing½m²ß-·s»D
	public static void Parsing_news(String URL) throws Exception{
		Document doc = Jsoup.connect(URL).get();
		//Elements newsHeadlines = doc.select("title,pubDate,description,tcNewsID");
		Elements title = doc.select("title");
		Elements pubDate = doc.select("pubDate");
		Elements description = doc.select("description");
		Elements tcNewsID = doc.select("tcNewsID");
		Locale local = new Locale("TRADITIONAL_CHINESE");
		DateFormat df = new SimpleDateFormat("E, dd MMM yyy HH:mm:ss z",local);
		DateFormat nowdf = new SimpleDateFormat("dd/MM/yyyy",local);
		Date sdate = new Date();
		Calendar cal = Calendar.getInstance();
		Calendar nowcal = Calendar.getInstance();
		nowcal.setTime(sdate);
		String matdate = df.format(nowcal.getTime());
		int helfmonth = nowcal.get(Calendar.MONTH);
		int helfyear = nowcal.get(Calendar.YEAR);
		if(helfmonth<=6) {
			helfyear = helfyear-1;
			helfmonth=12 + helfmonth - 6;
		}
		else {
			helfmonth=helfmonth-6;
		}
		for(int i=0 ; i<pubDate.size();i++) {
			Date date = df.parse(pubDate.get(i).text());
			cal.setTime(date);
			if(((cal.get(Calendar.YEAR) == helfyear)&&cal.get(Calendar.MONTH) >= helfmonth)|| cal.get(Calendar.YEAR)>helfyear) {
				cal.add(Calendar.YEAR, -1911);
				String formatdate = df.format(cal.getTime());
				System.out.println(title.get(i+1).text());
				System.out.println(formatdate);
				System.out.println(description.get(i+1).text());
				System.out.println(tcNewsID.get(i).text());
			}
		}
	}
		
}