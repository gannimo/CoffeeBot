package net.nebelwelt.CoffeeBot.plugins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Wikipedia extends URLGetterPlugin {
	String baseurl;
	
	public Wikipedia(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		this.baseurl = Main.getConfiguration("CoffeeBot.plugin."+name+".baseurl", "");
		if (this.baseurl.equals("")) {
			Logger.log(3, "Could not initialize plugin "+name+" - no baseurl given!");
		}
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "No echo";
			String page = "No request given";
			String url = baseurl;
			if (msg.getText().length()>this.command.length()) {
				text = msg.getText().substring(this.command.length()+1);
				if (text.startsWith("en ")) {
					text = text.substring(3);
				}
				if (text.startsWith("de ")) {
					text = text.substring(3);
					url = Main.getConfiguration("CoffeeBot.plugin."+name+".de.baseurl", baseurl);
				}
				text = text.replaceAll(" ", "_");
				try {
					page = getRequest(url + URLEncoder.encode( text, "UTF-8" ));
				} catch (UnsupportedEncodingException e) {
					page = getRequest(url + text);
					Logger.log(3, "Could not translate string '"+text+"' into url-encoded form");
				}
			}
			if (page.indexOf("<!-- start content -->")!=-1) {
				int position = page.indexOf("<!-- start content -->");
				int begin = page.indexOf("<p>", position);
				int end = page.indexOf("</p>", position);
				if (begin!=-1 && end!=-1) {
					page = page.substring(begin+3, end);
				}
			}
			String stripped = stripHTML(page);
			/* hacky way to stay in the limit of max 512 chars per msg */
			int len = stripped.length()+url.length()+text.length()+msg.getShortNick().length();
			if (len>400) {
				len-=stripped.length();
				stripped = stripped.substring(0,400-len)+"...";
			}
			sendResponse(new Response(msg, msg.getShortNick()+", ("+url+text+"): "+stripped));
		}
	}
	
}
