package taipower_Jsoup;
import java.net.URL;
import java.sql.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class testconsole {

	
	//SSL硈絬
	final static String[] URL= {"https://www.taipower.com.tw/tc/rssNews.ashx","https://www.taipower.com.tw/tc/rssEvents.ashx","https://www.taipower.com.tw/tc/rssMeasures.ashx"};
	
    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
 
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
 
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
 
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
	
    //磅︽
	public static void main(String[] JsoupURL) throws Exception {
		// TODO Auto-generated method stub
		enableSSLSocket();
		//Parsing_banner();
		
		
		/*for(int i=0;i< URL.length;i++)
		{
			Parsing_Insert(URL[i],i+1);
		}*/
		
		
		/*for(int i=0;i< URL.length;i++)
		{
			Parsing_Update(URL[i],i+1);
		}*/
		String ID="117";
		//Parsing_Delete(ID);
		Parsing_Select(ID);
		
	}
	
	//parsing絤策-瓜
	public static void Parsing_banner() throws Exception{
		Document doc = Jsoup.connect("https://www.taipower.com.tw/tc/index.aspx").get();
		System.out.print(doc.title());
		Elements newsHeadlines = doc.select("#pic a,#pic img");
		for (Element headline : newsHeadlines) {
			System.out.print(headline.absUrl("src")+" "+headline.absUrl("href")+"\n");
		}
	}
	//Parsing絤策-Insert
	public static void Parsing_Insert(String URL,int count) throws Exception{
		Document doc = Jsoup.connect(URL).get();
		Elements title = doc.select("title");
		Elements pubDate = doc.select("pubDate");
		Elements description = doc.select("description");
		Elements tcNewsID = doc.select("tcNewsID");
		Locale local = new Locale("TRADITIONAL_CHINESE");
		DateFormat df = new SimpleDateFormat("E, dd MMM yyy HH:mm:ss z",local);
		DateFormat insertdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            Connection conn = DriverManager.getConnection(url,"show0308","show0308"); 
            Statement st = conn.createStatement(); 
            for(int i=0 ; i<pubDate.size();i++) {
    			Date date = df.parse(pubDate.get(i).text());
    			
    			
    				String formatdate = insertdf.format(date);
    				
    				String sql = "INSERT INTO dbo.NEWS (Title"
    						+ ",Pubtime"
    						+ ",Description"
    						+ ",tcNewsID"
    						+ ",Type)" + 
    		                "VALUES ('"+title.get(i+1).text()
    		                +"', '"+formatdate
    		                +"', '"+description.get(i+1).text()
    		                +"', '"+tcNewsID.get(i).text()
    		                +"','"+count+"');";
    				//System.out.println(sql);
    				st.executeUpdate(sql);
    		}
            conn.close(); 
        } catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
	}
	
	//Parsing絤策-update
	public static void Parsing_Update(String URL,int count) throws Exception{
		Document doc = Jsoup.connect(URL).get();
		Elements title = doc.select("title");
		Elements pubDate = doc.select("pubDate");
		Elements description = doc.select("description");
		Elements tcNewsID = doc.select("tcNewsID");
		Locale local = new Locale("TRADITIONAL_CHINESE");
		DateFormat df = new SimpleDateFormat("E, dd MMM yyy HH:mm:ss z",local);
		DateFormat insertdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            Connection conn = DriverManager.getConnection(url,"show0308","show0308"); 
            Statement st = conn.createStatement(); 
            for(int i=0 ; i<pubDate.size();i++) {
    			Date date = df.parse(pubDate.get(i).text());
    			
    			
    				String formatdate = insertdf.format(date);
    				
    				String sql = "UPDATE dbo.NEWS "
    						+ "SET Title = '" + title.get(i+1).text()
    						+ "', Pubtime ='"+formatdate
    						+"',Description='"+description.get(i+1).text()
    						+"'WHERE tcNewsID = "+tcNewsID.get(i).text()+";";
    				System.out.println(sql);
    				st.executeUpdate(sql);

    		}
            conn.close(); 
        } catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
	}
	
	//Parsing絤策-Delete
	public static void Parsing_Delete(String ID) throws Exception{
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            Connection conn = DriverManager.getConnection(url,"show0308","show0308"); 
            Statement st = conn.createStatement(); 

    		String sql ="DELETE FROM dbo.NEWS\n"
    				+"WHERE tcNewsID = "+ ID +";";
    		System.out.println(sql);
    		st.executeUpdate(sql);

    		
            conn.close(); 
        } catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
	}
	
	//Parsing絤策-Select
public static void Parsing_Select(String ID) throws Exception{
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            Connection conn = DriverManager.getConnection(url,"show0308","show0308"); 
            Statement st = conn.createStatement(); 

    		String sql ="SELECT *\n"
    				+ "FROM dbo.NEWS\n"
    				+ "WHERE tcNewsID= "+ ID +";";
    		System.out.println(sql);
    		st.executeUpdate(sql);

    		
            conn.close(); 
        } catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
	}
	
	
}