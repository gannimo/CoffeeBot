package net.nebelwelt.CoffeeBot.network;

import java.io.BufferedReader;
import java.io.IOException;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;

/**
 * This thread keeps track of the incoming messages and dispatches them to the
 * IRCHandler
 * @author gannimo
 *
 */
public class IRCReader extends Thread {
	private IRCHandler handler;
	private BufferedReader ircin;
	
	public IRCReader(IRCHandler handler, BufferedReader ircin) {
		this.handler = handler;
		this.ircin = ircin;
	}
	
	public void run() {
		Logger.log(7, "Started IRCReader thread");
		try {
			/* wait until motd is displayed */
			while (handler.getState()==IRCHandler.CONNECTING) {
				String newLine = ircin.readLine();
				System.out.println(newLine);
				if (newLine==null || newLine.indexOf("376")!=-1) {
					synchronized (handler) {
						handler.setState(IRCHandler.CONNECTED);
						handler.notify();
					}
				}
				if (newLine.indexOf("433")!=-1) {
					System.err.println("Nickname is already in use, cannot connect");
					handler.shutdown();
					System.exit(1);
				}
				if (newLine.indexOf("468")!=-1) {
					System.err.println("Invalid username");
					handler.shutdown();
					System.exit(1);
				}
				if (newLine.startsWith("PING")) {
					Message msg = new Message("PONG "+newLine.substring(5));
					handler.putResponse(msg);
					Logger.log(7, "Ping/pong");
				}
			}
			while (true) {
				String newLine = ircin.readLine();
				boolean handled = false;
				if (newLine==null) {
					Logger.log(6, "Connection closed, closing IRCReader thread");
					handler.shutdown();
					return;
				}
				if (newLine.indexOf(" PRIVMSG ")!=-1) {
					handled = true;
					boolean forwardToPlugins = true;
					Query query = new Query(newLine);
					if (query.isPrivmsg() && query.getFirstWord().equals("VERSION")) {
						forwardToPlugins = false;
						Response resp = new Response(query, Main.version);
						handler.putResponse(resp);
					}
					if (query.isPrivmsg() && query.getFirstWord().toUpperCase().equals("QUIT")) {
						String message = "QUIT";
						if (Main.getConfiguration("CoffeeBot.password")!=null)
							message += " "+Main.getConfiguration("CoffeeBot.password");
						if (query.getText().toLowerCase().equals(message.toLowerCase())) {
							handler.shutdown();
							Logger.log(6, "QUIT received - shutting down IRCReader thread");
							return;
						} else {
							Logger.log(6, "QUIT with incorrect password: "+query.getText());
						}
					}
					if (forwardToPlugins) {
						handler.putQuery(query);
					}
				}
				
				if (newLine.startsWith("PING")) {
					handled = true;
					Message msg = new Message("PONG "+newLine.substring(5));
					handler.putResponse(msg);
					Logger.log(7, "Ping/pong");
				}
				if (!handled) Logger.log(7,newLine);
			}
		} catch (IOException e) {
			System.err.println("ERROR: IOException in IRCReader thread!");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
