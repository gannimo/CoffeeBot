package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Echo extends BasicPlugin {
	
	public Echo(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "No echo";
			if (msg.getText().length()>this.command.length())
				text = msg.getText().substring(this.command.length()+1);
			sendResponse(new Response(msg, text));
		}
	}
}
