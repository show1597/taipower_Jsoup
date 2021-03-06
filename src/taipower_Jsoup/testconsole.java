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

	
	//SSL連線
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
	
    //執行
	public static void main(String[] JsoupURL) throws Exception {
		// TODO Auto-generated method stub
		enableSSLSocket();
//		Parsing_banner();
		
		//*************
		//*INSERT NEWS*
		//*************
		Parsing_Reset();
		for(int i=0;i< URL.length;i++)
		{
			Parsing_Insert(URL[i],i+1);
		}
		
		
		/*for(int i=0;i< URL.length;i++)
		{
			Parsing_Update(URL[i],i+1);
		}*/
		/*String ID="10";
		String date_start="2018/03/12";
		String date_end="2018/03/15";
		//Parsing_Delete(ID);
		Parsing_Select(ID,date_start,date_end);*/
		
	}
	
	//parsing練習-圖片
	public static void Parsing_banner() throws Exception{
		Document doc = Jsoup.connect("https://www.taipower.com.tw/tc/index.aspx").get();
		Elements Linkurl = doc.select("#pic a");
		
		Connection conn = null;
		Statement st=null;
		
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            conn = DriverManager.getConnection(url,"show0308","show0308");
            
            conn.setAutoCommit(false);
            
    		String dsql ="DELETE FROM dbo.Banner;";
		st = conn.createStatement();
		st.executeUpdate(dsql);
		for (Element headline : Linkurl) {
			String sql = "INSERT INTO dbo.Banner("
					+"Img,Linkurl)"
					+"VALUES (?,?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, headline.child(0).absUrl("src"));
			pstmt.setString(2, headline.absUrl("href"));
			
			pstmt.executeUpdate();

				
	            conn.commit();
			System.out.print(headline.child(0).absUrl("src")+" "+headline.absUrl("href")+"\n");
		}
		conn.close(); 
        
        }
		catch(Exception e) {
            try {
                 conn.rollback();
            } catch (SQLException e1) {
                 e1.printStackTrace();
            }
            e.printStackTrace(); 
        } 
        finally {
            if(st != null) {
                try {
                    st.close();
                }   
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
		
	}
	
	//Parsing練習-Insert
	public static void Parsing_Insert(String URL,int Type) throws Exception{
		Document doc = Jsoup.connect(URL).get();
		Elements title = doc.select("title");
		Elements pubDate = doc.select("pubDate");
		Elements description = doc.select("description");
		Elements tcNewsID = doc.select("tcNewsID");
		Locale local = new Locale("TRADITIONAL_CHINESE");
		DateFormat df = new SimpleDateFormat("E, dd MMM yyy HH:mm:ss z",local);
		DateFormat insertdf=new SimpleDateFormat("YYYMMdd");
		
		Connection conn = null;
		Statement st=null;
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            conn = DriverManager.getConnection(url,"show0308","show0308"); 
            
            conn.setAutoCommit(false);
            
//          String dsql ="DELETE FROM dbo.NEWS";
    		st = conn.createStatement();
    		
//    		st.executeUpdate(dsql);
            
            for(int i=0 ; i<pubDate.size();i++) {
    			Calendar cal = Calendar.getInstance();
    	        cal.setTime(df.parse(pubDate.get(i).text()));
    	        Calendar Now = Calendar.getInstance();
    	        int Now_Year = Now.get(Calendar.YEAR);
    	        int Data_Year = cal.get(Calendar.YEAR);
    	        int Now_Month = Now.get(Calendar.MONTH)+1;
    	        int Data_Month = cal.get(Calendar.MONTH)+1;
    	        int Helf_Year;
    	        if(Now_Month < 6) {
    	        	Helf_Year=1;
    	        	Now_Year= Now_Year -1;
    	        	Now_Month=12-Now_Month;
    	        }else {
    	        	Helf_Year=2;
    	        	Now_Month = Now_Month-6;
    	        }
    	        if(((Helf_Year==2) && (Data_Month>Now_Month)) || ((Helf_Year==1)&&(Data_Year>Now_Year)&&(Data_Month<=Now_Month)) || ((Helf_Year==1)&&(Data_Year==Now_Year)&&(Data_Month>=Now_Month))) {
    	        cal.add(Calendar.YEAR, -1911);
    	        
    			String formatdate = insertdf.format(cal.getTime());
    			String sql = "INSERT INTO dbo.News ("
    					+"id,Type,Source_type,Title,NewDate,Photo,Content)"
    					+"VALUES (?,?,?,?,?,' ',?);";
    			PreparedStatement pstmt = conn.prepareStatement(sql);
    			pstmt.setString(1, tcNewsID.get(i).text());
    			pstmt.setString(2, String.valueOf(Type));
    			pstmt.setString(3, "1");
    			pstmt.setString(4, title.get(i+1).text());
    			pstmt.setString(5, formatdate);
    			pstmt.setString(6, description.get(i+1).text());
    			pstmt.executeUpdate();
//    			String sql = "INSERT INTO dbo.News ("
//    					+ "id"
//    					+ ",Type"
//    					+ ",Source_type"
//    					+ ",Title"
//    					+ ",NewDate"
//    					+ ",Photo"
//    					+ ",Content)" + 
//    		            "VALUES ('"+tcNewsID.get(i).text()
//    		            +"', '"+Type
//    		            +"', '1"
//    		            +"', '"+title.get(i+1).text()
//    		            +"', '"+formatdate
//    		            +"', ' "
//    		            +"','"+description.get(i+1).text()+"');";
//    				System.out.println(sql);
    				
    				
    	            conn.commit();
    				
    			}
    		}
            conn.close(); 
            
        }
		catch(Exception e) {
            try {
                 conn.rollback();
            } catch (SQLException e1) {
                 e1.printStackTrace();
            }
            e.printStackTrace(); 
        } 
        finally {
            if(st != null) {
                try {
                    st.close();
                }   
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
public static void Parsing_Reset() throws Exception{
		
		try { 
            String url = "jdbc:sqlserver://114.34.8.237;databaseName=Taipower";
            Connection conn = DriverManager.getConnection(url,"show0308","show0308"); 
            Statement st = conn.createStatement(); 

    		String sql ="DELETE FROM dbo.NEWS;";
    		System.out.println(sql);
    		st.executeUpdate(sql);

    		
            conn.close(); 
        } catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        }
	}
}