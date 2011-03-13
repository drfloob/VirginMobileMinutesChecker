package com.jaygoel.virginminuteschecker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;

public class WebsiteScraper {
	
    public static String fetchScreen(String username, String password) {
	String html= getHTML(username, password);
	String line= WebsiteScraper.getLine(html);
	return line;
   }
	    
	
    public static Map<String, String> parseInfo(String line) {
	Map<String, String> rc = new HashMap<String, String>();
		   
	if (line == null) {
	    Log.d(Globals.NAME, "WebsiteScraper.parseInfo: scraped string is null");
	    rc.put("isValid", "FALSE");
	    return rc;
	}
		   

	String srch;
	int start;
	int end;
   	    
	srch = "<p class=\"tel\">";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	if (start < 0) {
	    rc.put("isValid", "FALSE");
	    Log.d(Globals.NAME, "WebsiteScraper.parseInfo: scraped string is invalid:");
	    //Log.d(Globals.NAME, line);
	    return rc;
	} else {
	    rc.put("isValid", "TRUE");
	}
   	    
	//   	    virginInfo.append("Phone Number: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");

	rc.put("Phone Number", line.substring(start + srch.length(), end));
   	    
	srch = "<h3>Monthly Charge</h3><p>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
     	    
	//   	    virginInfo.append("Monthly Charge: ");    	    
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");

	rc.put("Monthly Charge", line.substring(start + srch.length(), end));

	srch = "<h3>Current Balance</h3><p>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	//   	    virginInfo.append("Current Balance: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");

	rc.put("Current Balance", line.substring(start + srch.length(), end));
   	    
	srch = "<h3>Min. Amount Due</h3><p>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	//   	    virginInfo.append("Amount Due: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");

	if ((start > 0) && (end > 0)) {
	    rc.put("Amount Due", line.substring(start + srch.length(), end));
	}
	srch = "<h3>Date Due</h3><p>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	//   	    virginInfo.append("Due Date: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");
  	    
	if ((start > 0) && (end > 0)) {
	    rc.put("Date Due", line.substring(start + srch.length(), end));
	}

	srch = "<h3>You will be charged on</h3><p>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	//   	    virginInfo.append("Due Date: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end));
	//   	    virginInfo.append("\n");
  	    
	if ((start > 0) && (end > 0)) {
	    rc.put("Charged on", line.substring(start + srch.length(), end));
	}
   	
	//	   rc.put("Charged on", "02/05/11");
   	    
   	    
	srch = "<p id=\"remaining_minutes\"><strong>";
	start = line.indexOf(srch);
	end = line.indexOf("</p>", start);
   	    
	//   	    virginInfo.append("Minutes Used: ");
	//   	    virginInfo.append(line.substring(start + srch.length(), end).replaceFirst("</strong>", ""));
	//   	    virginInfo.append("\n");
 
	rc.put("Minutes Used", line.substring(start + srch.length(), end).replaceFirst("</strong>", ""));
	   
	// rc.put("info", virginInfo.toString());
	return rc;
    }

    public static Map<String, String> getInfo(String username, String password) {

	String line = fetchScreen(username, password);
	// Log.d("DEBUG", "Line: "+line);
	   

	return parseInfo(line);
       
    }







    private static String getHTML(String username, String password) {
	String line = "";
	try {
	    TrustManager[] trustAllCerts = new TrustManager[]{
		new X509TrustManager() {
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		    }
		    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		}
	    };
			 
	    String url = Globals.URL;    
	    //String url = "https://www.triniluidades.qwerty/";    
	   	   

		    
	    try {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch (Exception e) {
		//System.out.println(e.getMessage());
		Log.e(Globals.NAME, "error from SSLContext (or cohorts)");
		e.printStackTrace();
		return null;
	    }
	    	    
	    //HttpsURLConnection.setFollowRedirects(true);
	    	    
	    HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
	    ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
	    	   
	    //connection.setFollowRedirects(true);
	    connection.setInstanceFollowRedirects(false);
	    	    
	    	    
	    connection.setDoOutput(true);

	    OutputStream co;
	    try {
		co= connection.getOutputStream();
	    } catch(IOException e) {
		// @todo ui alert, maybe retry a set number of times
		Log.e(Globals.NAME, "error from connection.getOutputStream");
		e.printStackTrace();
		return null;
	    }
	    
	    OutputStreamWriter out = new OutputStreamWriter(co);

	    out.write("loginRoutingInfo=&min=" + username + "&vkey=" + password + "&submit=submit");
	    out.close();
	    	    
	    //connection.connect();

	    // detect specifically if login failed
	    if (connection.getResponseCode() != 200) {
		Log.e(Globals.NAME, "bad response code: " + Integer.toString(connection.getResponseCode()));
		return null;
	    }


	    InputStreamReader in;
	    try {
		in = new InputStreamReader((InputStream) connection.getContent());
	    } catch(IOException e) {
		Log.e(Globals.NAME, "error from connection.getContent");
		e.printStackTrace();
		return null;
	    }

	    /*
	    BufferedReader buff = new BufferedReader(in);
	    line = buff.readLine();
	    	    
	    while (line != null) {
		if (line.contains("mainContent")) {
		    break;
		}
		line = buff.readLine();
	    }
	    */

	    // reading entire page into `line`, as a test, and for debugging purposes.
	    // @todo parsing speed tests on various devices

	    final char[] buffer = new char[0x10000];
	    StringBuilder builder = new StringBuilder();
	    int read;
	    do {
		read = in.read(buffer, 0, buffer.length);
		if (read>0) {
		    builder.append(buffer, 0, read);
		}
	    } while (read>=0);
	    line = builder.toString();


	    connection.disconnect();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("exception 83");
	    System.err.println(e.getMessage());
	    System.err.println(line);
	    return line;
	    //rc.put("isValid", "FALSE");
	}


	if (line == null) {
	    line = "";
	}
	return line;
     }


    private static String getLine(String html) {
	return html;
    }
   
}