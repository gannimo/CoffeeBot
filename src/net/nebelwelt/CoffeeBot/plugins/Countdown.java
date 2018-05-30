package net.nebelwelt.CoffeeBot.plugins;

import java.util.Calendar;

import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

/**
 * This is an easy countdown plugin.
 * It listens to the defined countdown-command in the properties
 * and tries to get the difference between the configured date and the
 * current time.
 * It then prints the predefined message and the time-difference
 * @author gannimo
 *
 */
public class Countdown extends BasicPlugin {
	String message;
	Calendar date;
	
	public Countdown(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		this.message = Main.getConfiguration("CoffeeBot.plugin."+name+".message", "Countdown:");
		this.date = Calendar.getInstance();
		String strDate = Main.getConfiguration("CoffeeBot.plugin."+name+".date", "1981-04-29 7:20:00");
		int year=1981, month=4, day=29;
		int hour=7, minute=20, second=0;
		try {
			year = Integer.parseInt(strDate.substring(0,strDate.indexOf('-')));
			strDate = strDate.substring(strDate.indexOf('-')+1);
			month = Integer.parseInt(strDate.substring(0,strDate.indexOf('-')));
			strDate = strDate.substring(strDate.indexOf('-')+1);
			day = Integer.parseInt(strDate.substring(0,strDate.indexOf(' ')));
			strDate = strDate.substring(strDate.indexOf(' ')+1);
			hour = Integer.parseInt(strDate.substring(0,strDate.indexOf(':')));
			strDate = strDate.substring(strDate.indexOf(':')+1);
			minute = Integer.parseInt(strDate.substring(0,strDate.indexOf(':')));
			strDate = strDate.substring(strDate.indexOf(':')+1);
			second = Integer.parseInt(strDate);
		} catch (Exception e) {
			System.err.println("Misconfiguration of the Countdown plugin - "+name);
			System.err.println("Could not parse date ("+strDate+")!");
			e.printStackTrace();
			System.exit(1);
		}
		this.date.set(year, month-1, day, hour, minute, second);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String past = "";
			long target = date.getTimeInMillis();
		    long current = Calendar.getInstance().getTimeInMillis();
		    long diff = target - current;
		    if (diff<0) {
		    	past = "-";
		    	diff*=-1;
		    }
		    long diffSeconds = (diff / 1000) % 60;
		    long diffMinutes = (diff / (60 * 1000)) % 60;
		    long diffHours = (diff / (60 * 60 * 1000)) % 24;
		    long diffDays = diff / (24 * 60 * 60 * 1000);
		    
			sendResponse(new Response(msg, this.message +" "+ past+ diffDays+"d "+diffHours+"h "+diffMinutes+"m "+diffSeconds+"s"));
		}
	}
	
}
