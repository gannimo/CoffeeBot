package net.nebelwelt.CoffeeBot.plugins;

import java.io.IOException;
import java.io.InputStream;

import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;
import net.nebelwelt.CoffeeBot.plugins.Eliza.Eliza;

public class ElizaPlugin extends BasicPlugin {
	
	private Eliza eliza;
	public ElizaPlugin(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		InputStream inputStream = Eliza.class.getClassLoader().getResourceAsStream("net/nebelwelt/CoffeeBot/plugins/Eliza/eliza.dat");
		try {
			this.eliza = new Eliza(inputStream);
		} catch (IOException e) {
			this.eliza = null;
		}
	}

	/*public boolean checkMessage(Message msg) {
		if (!(msg instanceof Query)) return false;
		if (((Query)msg).getText().startsWith(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot"))) return true;
		return false;
	}*/

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = msg.getShortNick()+": ";
			String query = msg.getText().substring(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot").length());
			//if (query.length()>2) query = query.substring(2);
			query = query.trim();
			if (eliza == null) {
				text += "Problem with Eliza...";
			} else {
				text += eliza.getResponse(query);
			}
			sendResponse(new Response(msg, text));
		}
	}
}
