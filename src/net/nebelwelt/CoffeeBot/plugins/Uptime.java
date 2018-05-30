package net.nebelwelt.CoffeeBot.plugins;

import java.util.Calendar;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

/**
 * This is an easy uptime plugin.
 * It listens to the defined uptime-command in the properties
 * and tries to get the difference between the start date and the
 * current time.
 * It then prints the predefined message and the time-difference
 * @author gannimo
 *
 */
public class Uptime extends BasicPlugin {
	String message;
	Calendar date;
	
	public Uptime(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		this.message = "Up and working since: ";
		this.date = Calendar.getInstance();
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
//			String past = "";
			long target = date.getTimeInMillis();
		    long current = Calendar.getInstance().getTimeInMillis();
		    long diff = target - current;
		    if (diff<0) {
//		    	past = "-";
		    	diff*=-1;
		    }
		    long diffSeconds = (diff / 1000) % 60;
		    long diffMinutes = (diff / (60 * 1000)) % 60;
		    long diffHours = (diff / (60 * 60 * 1000)) % 24;
		    long diffDays = diff / (24 * 60 * 60 * 1000);
		    
			sendResponse(new Response(msg, this.message + diffDays+"d "+diffHours+"h "+diffMinutes+"m "+diffSeconds+"s"));
		}
	}
	
}
