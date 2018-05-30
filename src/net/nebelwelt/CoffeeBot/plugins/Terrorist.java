package net.nebelwelt.CoffeeBot.plugins;

import java.util.Vector;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Terrorist extends BasicPlugin {
	
	public Terrorist(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	String terroristWords[] = {"Terrorist", "Bomb", "President", "Allah", "Koran", "9/11", "Iran", "Irak", "Ambassador", "Killings", "Attack", "Al Quida", "Osama", "Cell", "Sleeper", "NSA", "Stock market", "CIA", "Mossad"};
	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = msg.getShortNick()+":";
			Vector<String> localList = new Vector<String>();
			for (int i=0; i<terroristWords.length; i++) localList.add(terroristWords[i]);
			int max = (int)(Math.floor(Math.random()*terroristWords.length))+1;
			for (int i=0; i<max; i++) {
				int pick = (int)(Math.floor(Math.random()*localList.size()));
				text+=" "+localList.elementAt(pick);
				localList.remove(pick);
			}
			sendResponse(new Response(msg, text));
		}
	}
}
