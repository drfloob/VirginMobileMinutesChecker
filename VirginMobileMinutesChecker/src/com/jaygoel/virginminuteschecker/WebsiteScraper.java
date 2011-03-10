package com.jaygoel.virginminuteschecker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class WebsiteScraper {
	
    public static String fetchScreen(String username, String password) {
	String html= WebsiteScraper.getHTML(username, password);
	String line= WebsiteScraper.getLine(html);
	return line;
   }
	    
	
    public static Map<String, String> parseInfo(String line) {
	Map<String, String> rc = new HashMap<String, String>();
		   
	if (line == null) {
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
		    public void checkClientTrusted(
						   java.security.cert.X509Certificate[] certs, String authType) {
		    }
		    public void checkServerTrusted(
						   java.security.cert.X509Certificate[] certs, String authType) {
		    }
		}
	    };
			 
	    String url = "https://www1.virginmobileusa.com/login/login.do";    
	    //String url = "https://www.triniluidades.qwerty/";    
	   	   

		    
	    try {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch (Exception e) {
		e.getMessage();
	    }
	    	    
	    //HttpsURLConnection.setFollowRedirects(true);
	    	    
	    HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
	    ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
	    	   
	    //connection.setFollowRedirects(true);
	    	    
	    	    
	    connection.setDoOutput(true);
	    	    
	    OutputStreamWriter out = new OutputStreamWriter(
							    connection.getOutputStream());

	    out.write("loginRoutingInfo=&min=" + username + "&vkey=" + password + "&submit=submit");
	    out.close();
	    	    
	    //connection.connect();

	    Log.d(Globals.NAME, "Getting content");

	    InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());

	    Log.d(Globals.NAME, "Finished getting content");

	    BufferedReader buff = new BufferedReader(in);
	    line = buff.readLine();
	    	    
	    while (line != null) {
		if (line.contains("mainContent")) {
		    break;
		}
		line = buff.readLine();
	    }

	    connection.disconnect();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("exception 83");
	    System.err.println(e.getMessage());
	    System.err.println(line);
	    return line;
	    //rc.put("isValid", "FALSE");
	}
	//line = null;
	if (line == null) {
	    line = "";
	}
	//System.err.println(line);
	return line;
     }



    private static String getLine(String html) {
	return html;
    }
   
}