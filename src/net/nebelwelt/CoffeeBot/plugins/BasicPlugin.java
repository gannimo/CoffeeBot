package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

/**
 * This is a basic plugin with all important methods that can be extended
 * by other plugins.
 * The idea of this plugin is that the first word of the message will be the
 * command that triggers this plugin and the checkMessage function is
 * programmed likewise
 * @author gannimo
 *
 */
public abstract class BasicPlugin {
	String name;
	String command;
	IRCHandler handler;
	
	public BasicPlugin(String name, String command, IRCHandler handler) {
		this.name = name;
		this.command = command;
		this.handler = handler;
	}
	private BasicPlugin() {}
	
	public void initialize(String configPreface) {
		Logger.log(8, "Plugin "+name+" initialized");
	}
	public void afterConnect() {
		Logger.log(8, "Plugin "+name+" - after connect");
	}
	public void afterJoin() {
		Logger.log(8, "Plugin "+name+" - after join");
	}
	public void beforeLeave() {
		Logger.log(8, "Plugin "+name+" - before leave");
	}
	public void beforeQuit() {
		Logger.log(8, "Plugin "+name+" - before quit");
	}
	public boolean checkMessage(Message msg) {
		if (!(msg instanceof Query)) return false;
		if (((Query)msg).getFirstWord().equals(this.command)) return true;
		return false;
	}
	public void handleMessage(Message msg) {
		Logger.log(8, "Plugin "+name+" - handle message");
		if (msg instanceof Query) {
			handleMessage((Query)msg);
		}
	}
	
	public void handleMessage(Query query) {
		Logger.log(9, "Plugin "+name+" - handle message (query)");
	}
	
	void sendResponse(Message msg) {
		handler.putResponse(msg);
	}
	
	public String getName() {
		return name;
	}

	public String getCommand() {
		return command;
	}
}
