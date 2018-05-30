package net.nebelwelt.CoffeeBot.plugins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Quote extends BasicPlugin {

	String file;
	Map<String,Vector<String>> quotes;
	Vector<String> allquotes;
	
	public Quote(String name, String command, IRCHandler handler) {
		super(name, command, handler);
		this.file = Main.getConfiguration("CoffeeBot.plugin."+name+".file", "");
		quotes = Collections.synchronizedMap(new HashMap<String,Vector<String>>());
		allquotes = new Vector<String>();
		
		try {
			/*
			 * Reads a file of the format <nick> <quote> and
			 * generates a map-structure for easy access
			 */
			BufferedReader fin = new BufferedReader(new FileReader(file));
			while (fin.ready()) {
				String line = fin.readLine();
				localAdd(line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file '"+file+"' - error in Quote plugin ("+name+")");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not access file '"+file+"' - error in Quote plugin ("+name+")");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void localAdd(String line) {
		String nick = line.substring(0,line.indexOf(" "));
		//line = line.substring(line.indexOf(" ")+1);
		Vector<String> uquotes;
		if (quotes.containsKey(nick)) {
			uquotes = quotes.get(nick);
		} else {
			uquotes = new Vector<String>();
			quotes.put(nick, uquotes);
		}
		uquotes.add(line);
		allquotes.add(line);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			/* get command */
			String text = msg.getText();
			if (text.length()>this.command.length())
				text = text.substring(this.command.length()+1);
			else
				text = "";
			String command;
			if (text.indexOf(" ")!=-1)
				command = text.substring(0, text.indexOf(" "));
			else
				command = text;
			/* no input - simulate a rand command */
			if (command.equals("")) { command="rand"; text="rand"; }
			
			String rest = text.substring(command.length()).trim();
			
			boolean handled = false;
			// prints a random quote (global or from a user)
			if (command.equals("rand")) { printQuote(msg, rest, -1); handled=true; }
			// adds a new quote
			if (command.equals("add")) { addQuote(msg, rest); handled=true; }
			// shows all available nicks
			if (command.equals("stats")) { showStats(msg, rest); handled=true; }
			// shows the help message
			if (command.equals("help")) { printHelp(msg); handled=true; }
			// now we try to get an actual quote (eg !quote 12 or !quote 12 gannimo)
			try {
				int nr = Integer.parseInt(command)-1;
				printQuote(msg, rest, nr);
				handled=true;
			} catch (Exception e) {}
			if (!handled) {
				sendResponse(new Response(msg, "Could not understand your command!"));
				printHelp(msg);
			}
		}
	}

	private void showStats(Query msg, String rest) {
		String response = msg.getShortNick()+": "+allquotes.size()+" Quotes are available for the following nicks: ";
		for (String item: quotes.keySet()) {
			response+=item+" ("+quotes.get(item).size()+"), ";
		}
		if (response.endsWith(", ")) response = response.substring(0, response.length()-2);
		sendResponse(new Response(msg, response));
	}

	private void addQuote(Query msg, String rest) {
		if (rest.indexOf(" ")!=-1) {
			localAdd(rest);
			try {
				synchronized (file) {
				File tester = new File(file);
				if (tester.length()>10*1024*1024) throw new IOException("Quote file '"+file+"' is too large.");
				BufferedWriter fout = new BufferedWriter(new FileWriter(file, true));
				fout.write(rest);
				fout.newLine();
				fout.flush();
				fout.close();
				}
			} catch (IOException e) {
				Logger.log(1, "Coult not add quote - IOException in Quote plugin ("+name+")");
				sendResponse(new Response(msg, msg.getShortNick()+": Could not add quote - check logs"));
			}
			sendResponse(new Response(msg, msg.getShortNick()+": Added new quote for user "+rest.substring(0, rest.indexOf(" "))));
		} else {
			sendResponse(new Response(msg, msg.getShortNick()+": Could not add quote - wrong number of arguments!"));
		}
	}

	private void printHelp(Query msg) {
		sendResponse(new Response(msg, msg.getShortNick()+": This is the quote plugin. Usage:"));
		sendResponse(new Response(msg, this.command+" [rand <?nick> | add <nick> <quote> | ## <?nick> | stats | help]"));
		sendResponse(new Response(msg, this.command+" rand <?nick>       : prints a random quote (if specified from a specific user, otherwise global)."));
		sendResponse(new Response(msg, this.command+" add <nick> <quote> : adds a new quote of the specified user to the db."));
		sendResponse(new Response(msg, this.command+" ## <?nick>         : prints the ##-quote (if specified from a specific user, otherwise global)."));
		sendResponse(new Response(msg, this.command+" stats              : shows all nicks with quotes."));
		sendResponse(new Response(msg, this.command+" help               : prints this help message."));
	}

	private void printQuote(Query msg, String rest, int index) {
		String quote;
		Vector<String> lquotes = allquotes;
		if (rest.length()!=0) {
			lquotes = quotes.get(rest);
		}
		if (lquotes!=null && index<lquotes.size()) {
			if (index==-1) index = (int)(Math.random()*lquotes.size());
			quote = lquotes.get(index);
			if (lquotes==allquotes)
				quote = quote.substring(0, quote.indexOf(" "))+" said (global "+(index+1)+"/"+allquotes.size()+"): "+quote.substring(quote.indexOf(" ")+1);
			else
				quote = quote.substring(0, quote.indexOf(" "))+" said ("+(index+1)+"/"+lquotes.size()+"): "+quote.substring(quote.indexOf(" ")+1);
		} else {
			if (lquotes==null)
				quote = "Could not find a quote by that nickname.";
			else
				quote = "Wrong index or could not find a quote by that nickname.";
		}
		sendResponse(new Response(msg, quote));
	}

}
