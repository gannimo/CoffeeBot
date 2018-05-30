package net.nebelwelt.CoffeeBot.plugins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class URLGetterPlugin extends BasicPlugin {

	public URLGetterPlugin(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	String getRequest(String surl) {
    	StringBuffer ret = new StringBuffer();
        try {
        	URL url = new URL(surl);

        	URLConnection urlconn = url.openConnection();
        	BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
        	String inputLine;
        	while ((inputLine = in.readLine()) != null) { 
        		ret.append(inputLine);
        		ret.append(' ');
        	}
			in.close();
		} catch (Exception e) {
			ret = new StringBuffer("ERROR: Could not fetch requested page.");
			Logger.log(4,"Plugin-Error: Could not fetch requested page: "+e.getMessage());
		}
		return ret.toString();
	}
	
	String stripHTML(String code) {
		StringBuffer ret = new StringBuffer();
		int index = code.indexOf('<');
		int lastpos=0;
		while (index!=-1) {
			ret.append(code.substring(lastpos,index));
			ret.append(' ');
			lastpos = code.indexOf('>',index)+1;
			index = code.indexOf('<',lastpos);
		}
		ret.append(code.substring(lastpos));
		String res = ret.toString();
		res = res.replaceAll("&nbsp;", " ");
		res = res.replaceAll("&uuml;", "ü");
		res = res.replaceAll("&auml;", "ä");
		res = res.replaceAll("&ouml;", "ö");
		res = res.replaceAll("&egrave;", "è");
		res = res.replaceAll("\r", " ");
		res = res.replaceAll("\n", " ");
		res = res.replaceAll("\t", " ");
		res = res.replaceAll("\\s{2,}", " ");
		return res;
	}
}
