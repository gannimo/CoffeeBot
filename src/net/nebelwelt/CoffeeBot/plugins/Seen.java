package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Seen extends BasicPlugin {
	
	public Seen(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "No echo";
			if (msg.getText().length()>this.command.length())
				text = msg.getText().substring(this.command.length()+1);
			if (msg.getShortNick().equals("gannimo")) {
				sendResponse(new Response(false, "#infochat", text));
			} else {
				sendResponse(new Response(msg, text));
			}
		}
	}
}
