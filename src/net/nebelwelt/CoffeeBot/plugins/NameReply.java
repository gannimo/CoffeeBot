package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class NameReply extends BasicPlugin {
	
	public NameReply(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public boolean checkMessage(Message msg) {
		if (!(msg instanceof Query)) return false;
		if (((Query)msg).getText().indexOf(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot"))!=-1) return true;
		return false;
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = msg.getShortNick()+": Are you talking to me?";
			sendResponse(new Response(msg, text));
		}
	}
}
