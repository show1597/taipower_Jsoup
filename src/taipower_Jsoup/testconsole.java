package taipower_Jsoup;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class testconsole {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Parsing_test();
	}
	public static void Parsing_test() throws Exception{
		Document doc = Jsoup.connect("http://www.taipower.com.tw/tc/index.aspx").get();
		System.out.print(doc.title());
		Elements newsHeadlines = doc.select("#pic img");
		for (Element headline : newsHeadlines) {
		  System.out.printf("%s\n\t%s", 
		    headline.attr("title"),headline.absUrl("src"));
		}
	}

}
