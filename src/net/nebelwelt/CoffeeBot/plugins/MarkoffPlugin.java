package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;
import net.nebelwelt.CoffeeBot.plugins.Markoff.MarkoffChain;

public class MarkoffPlugin extends BasicPlugin {
	
	private String file;
	private MarkoffChain brain;
	public MarkoffPlugin(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		this.file = Main.getConfiguration("CoffeeBot.plugin."+name+".brain", "");
		brain = new MarkoffChain(file);
	}

	public boolean checkMessage(Message msg) {
		if (!(msg instanceof Query)) return false;
		
		/* learn this line */
		String line = ((Query)msg).getText();
		if (!(line.startsWith("---") || line.indexOf("-!-")!=-1 || line.indexOf(">>>")!=-1) && ((Query)msg).isPrivmsg()==false)
			brain.learn(line);
		
		if (((Query)msg).getFirstWord().equals(this.command)) return true;
		if (line.toLowerCase().startsWith(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot").toLowerCase())) return true;

		/* let's say something with 60% probability, if our name drops up */
		if (line.toLowerCase().indexOf(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot").toLowerCase())!=-1 && !line.startsWith("!")) {
			if (Math.random()>0.4)
				return true;
		}
		
		/* otherwise, we say some response with 5% probability */
		return Math.random()>0.95;
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "";
			if (((Query)msg).getText().startsWith(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot"))) {
				text = msg.getShortNick()+": ";
			}
			text += brain.getLine();
			sendResponse(new Response(msg, text));
		}
	}
	
	public void beforeQuit() {
		Logger.log(6, "Saving brain database...");
		brain.saveBrain();
		Logger.log(8, "Plugin "+name+" - before quit");
	}

}
