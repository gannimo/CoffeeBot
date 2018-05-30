package net.nebelwelt.CoffeeBot.network;

import java.io.BufferedWriter;
import java.io.IOException;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Messages.Message;

/**
 * This thread keeps track of the outgoing messages and dispatches them to the
 * network
 * @author gannimo
 *
 */
public class IRCWriter extends Thread {
	private IRCHandler handler;
	private BufferedWriter ircout;
	
	public IRCWriter(IRCHandler handler, BufferedWriter ircout) {
		this.handler = handler;
		this.ircout = ircout;
	}
	
	public void run() {
		Logger.log(7, "Started IRCWriter thread");
		try {
			while (true) {
				Message resp = handler.getNextResponse();
				if (resp==null) {
					Logger.log(6, "No new messages available, sthutting down sender thread");
					return;
				}
				Logger.log(8, "Got a new response, sending it to the wire");
				Logger.log(9, resp.getMessage());
				ircout.write(resp.getMessage());
				ircout.newLine();
				ircout.flush();
			}
		} catch (IOException e) {
			System.err.println("ERROR: IOException in IRCWriter thread!");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
