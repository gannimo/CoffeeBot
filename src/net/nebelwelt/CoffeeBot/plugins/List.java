package net.nebelwelt.CoffeeBot.plugins;

import java.util.StringTokenizer;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class List extends BasicPlugin {
	
	public List(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "";
			String loadablePlugins = Main.getConfiguration("CoffeeBot.plugins");
			StringTokenizer plugins = new StringTokenizer(loadablePlugins,", ");
			/* Iterate through all plugins and initialize them */
			while (plugins.hasMoreTokens()) {
				String currentPlugin = plugins.nextToken();
				Logger.log(8, "Loading plugin "+currentPlugin);
				String command = Main.getConfiguration("CoffeeBot.plugin."+currentPlugin+".command");
				if (text.length()!=0) text += ",";
				if (command!=null) {
					text+=" "+command;
				} else {
					text+=" "+currentPlugin+" (nc)";

				}
			}
			if (text.length()==0) {
				text = "No plugins available.";
			} else {
				text="The following plugins are available: "+text;
			}
			if (msg.getText().length()>this.command.length())
				text = msg.getText().substring(this.command.length()+1);
			sendResponse(new Response(msg, text));
		}
	}
}
