package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Dice extends BasicPlugin {
	
	public Dice(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = msg.getText().substring(this.command.length());
			if (text.toLowerCase().trim().equals("help")) {
				sendHelp(msg);
				return;
			}
			boolean error = false;
			if (text.indexOf("d")!=-1) {
				String strNrDice = text.substring(0,text.indexOf("d")).trim();
				String strNrEyes = text.substring(text.indexOf("d")+1).trim();
				if (strNrDice.length()>0 && strNrEyes.length()>0) {
					try {
						int nrdice = Integer.parseInt(strNrDice);
						int nreyes = Integer.parseInt(strNrEyes);
						if (nrdice>10) {
							error = true;
							sendResponse(new Response(msg, "You are using too many dices (try less than 10)!"));
							return;
						}
						if (nrdice<1) {
							error = true;
							sendResponse(new Response(msg, "You are using too few dices (try more than 1)!"));
							return;
						}
						if (nreyes>100) {
							error = true;
							sendResponse(new Response(msg, "More than 100 eyes, are you serious?"));
							return;
						}
						if (nreyes<2) {
							error = true;
							sendResponse(new Response(msg, "Do you know of any dices with less than 2 eyes?"));
							return;
						}
						int sum = 0;
						for (int i=0; i<nrdice; i++) {
							int cur = (int)(Math.round(Math.random()*(nreyes-1))+1);
							sum += cur;
							sendResponse(new Response(msg, "Rolling dice #"+i+": "+cur));
						}
						sendResponse(new Response(msg, "This gives you a total of "+sum+" of "+nrdice*nreyes));
					} catch (Exception e) { error = true; }
				} else error = true;
			} else error = true;
			if (error) sendHelp(msg);
		}
	}
	
	public void sendHelp(Query msg) {
		sendResponse(new Response(msg, "This is the dice script. Use '"+this.command+" IdJ' to roll some dice, where I is the number of dice with J eyes."));
	}
}
