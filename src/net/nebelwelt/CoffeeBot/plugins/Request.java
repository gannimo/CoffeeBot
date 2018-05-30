package net.nebelwelt.CoffeeBot.plugins;

import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Request extends BasicPlugin {
	
	public Request(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = msg.getText();
			if (text.length()>(this.command.length()+1)) 
				text = text.substring(this.command.length()+1);
			else
				text = "nothing. Go home and cry, emo kid";
			text = text.trim();
			String response = "You get "+text+".";
			boolean handled = false;
			if (text.toLowerCase().equals("help")) {
				response="This is the request plugin. It returns simply your request. Special commands are: ";
				response+="coffee, espresso, beer, cocktail, pizza and nuke. Try them :)";
			}
			if (text.toLowerCase().equals("coffee")) {
				response="Here, a cup of strong, hot coffee c[\"]. Enjoy!";
			}
			if (text.toLowerCase().equals("chocolate")) {
				response="Here, a cup of sweet, hot chocolate c[\"]. Enjoy!";
			}
			if (text.toLowerCase().equals("espresso")) {
				response="Here, a strong espresso. Caution: Hot!";
			}
			if (text.toLowerCase().equals("free lunch")) {
				response="There is no free lunch. But you can ask the !mensa plugin for help!";
			}
			if (text.toLowerCase().equals("lunch") || text.toLowerCase().equals("food")) {
				response="I don't have anything to eat. But you can ask the !mensa plugin for help!";
			}
			if (text.toLowerCase().equals("round")) {
				String[] rounds = { "Beer", "Sirup", "Cocktails", "Shots", "Wine" };
				response="The bar sponsors a round of "+rounds[(int)(Math.random()*rounds.length)]+". Enjoy!";
			}
			if (text.toLowerCase().equals("beer")) {
				String[] beers = { "Corona", "VB", "Guinness", "Tell", "Murpheys Stout", "Becks", "Hoegaarden", "Leffe Brune", "1664", "Heineken" };
				response="Here, a nice bottle of "+beers[(int)(Math.random()*beers.length)]+". Enjoy!";
			}
			if (text.toLowerCase().equals("cocktail")) {
				String[] cocktails = { "Pina Colada", "White Russian", "Long Island Ice Tea", "Vodka Orange", "Whiskey Cola", "Whiskey Sour", "Bloody Mary", "Vodka Lemon", "Caipirinha" };
				response = "The bartender serves you a "+cocktails[(int)(Math.random()*cocktails.length)]+". Enjoy ;)";
			}
			if (text.toLowerCase().equals("pizza")) {
				String[] cocktails = { "Margeritha", "Quattro Stagioni", "Calzone", "Napoli", "Proscuitto", "Tonno", "Hawaii", "Cipolla", "Funghi", "Calabrese", "Quattro Formaggi", "Gorgonzola", "Padrone" };
				response = "The pizza maker brings you a Pizza "+cocktails[(int)(Math.random()*cocktails.length)]+" fresh out of the oven. Enjoy ;)";
			}
			if (text.toLowerCase().startsWith("nuke")) {
				text = text.substring(4);
				if (text.indexOf("gannimo")!=-1 || text.indexOf(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot"))!=-1) text = msg.getShortNick();
				response = "Launched "+(int)(Math.random()*100)+" nukes, all heading at"+text+". Head to the next shelter NOW!";
			}
			if (text.toLowerCase().startsWith("kick") || text.toLowerCase().startsWith("slap")) {
				String action = text.substring(0,4)+'s';
				text = text.substring(4);
				text = text.trim();
				handled=true;
				String[] kicks = {"with a large, old, smelly trout.", "with a large bottle of Vodka.", "in the privates.", "", "with a large wooden stick.", "for no reason at all"};
				if (text.indexOf("gannimo")!=-1 || text.indexOf(Main.getConfiguration("CoffeeBot.nick", "CoffeeBot"))!=-1) text = msg.getShortNick();
				response = "ACTION "+action+" "+text+" "+kicks[(int)(Math.random()*kicks.length)]+"";
			}
			if (!handled) response = msg.getShortNick()+": "+response;
			sendResponse(new Response(msg, response));
		}
	}
}
