package net.nebelwelt.CoffeeBot.plugins;

import java.util.Calendar;

import net.nebelwelt.CoffeeBot.Messages.Query;
import net.nebelwelt.CoffeeBot.Messages.Response;
import net.nebelwelt.CoffeeBot.network.IRCHandler;

/**
 * This is a bad example of a plugin because everything is hardcoded.
 * But it would not make much sense to code all parameters dynamically
 * because the mensa-page changes way to often and it is easier to
 * adapt it here! 
 * @author gannimo
 *
 */
public class Mensa extends URLGetterPlugin {

	public Mensa(String name, String command, IRCHandler handler) {
		super(name, command, handler);
	}

	public void handleMessage(Query msg) {
		if (checkMessage(msg)) {
			String text = "No echo";
			String page = "Keine mensa ausgewaehlt";
			if (msg.getText().length()>this.command.length()) {
				text = msg.getText().substring(this.command.length()+1);
			}
			if (text.equals("poly") || text.equals("infobar") || text.equals("cafeteria") || text.equals("clausius") || text.equals("gloria") || text.equals("poly-abend") || text.equals("cafeteria-abend")) {
				int modifier=0;
				String url = "http://www.gastro.ethz.ch/menuela_overview?viewType=daily";
				if (text.equals("poly")) {
					text = "Mensa Polyterasse";
					url+="&facility=11&language=DE";
				}
				if (text.equals("poly-abend")) {
					text = "Mensa Polyterasse (Abend)";
					url+="&facility=11&language=DE";
				}
				if (text.equals("infobar")) {
					text = "Informatikbar";
					url+="&facility=6&language=DE";
				}
				if (text.equals("gloria")) {
					text = "Gloriabar";
					url+="&facility=9&language=DE";
				}
				if (text.equals("cafeteria")) {
					text = "Cafeteria";
					url+="&IdMensa=4&language=DE";
				}
				if (text.equals("cafeteria-abend")) {
					text = "Cafeteria (Abend)";
					url+="&IdMensa=4&language=DE";
				}
				if (text.equals("clausius")) {
					text = "Clausiusbar";
					url+="&facility=13&language=DE";
				}
				page = getRequest(url);
				if (text.equals("Mensa Polyterasse (Abend)") || text.equals("Cafeteria (Abend)")) {
					modifier = page.indexOf("Essensausgabe: 17:30");
				}
				int suppe = page.indexOf("Tagessuppe", modifier);
				int menu1 = page.indexOf("Menu 1", modifier);
				int vegi = page.indexOf("Vegi", modifier);
				int spezial = page.indexOf("Menu Spezial", modifier);
				int bio = page.indexOf("Bio Menu", modifier);
				String res = "";
				if (suppe!=-1) {
					suppe = page.indexOf("<td>", suppe);
					res+="[Suppe]: "+page.substring(suppe, page.indexOf("</", suppe))+" ";
				}
				if (menu1!=-1) {
					menu1 = page.indexOf("<td>", menu1);
					res+="[Menu 1]: "+page.substring(menu1, page.indexOf("</", menu1))+" ";
				}
				if (vegi!=-1) {
					vegi = page.indexOf("<td>", vegi);
					res+="[Vegi]: "+page.substring(vegi, page.indexOf("</", vegi))+" ";
				}
				if (spezial!=-1) {
					spezial = page.indexOf("<td>", spezial);
					res+="[Spezial]: "+page.substring(spezial, page.indexOf("</", spezial))+" ";
				}
				if (bio!=-1) {
					bio = page.indexOf("<td>", bio);
					res+="[Bio]: "+page.substring(bio, page.indexOf("</", bio))+" ";
				}
				page = res;
			}
			if (text.equals("uni") || text.equals("uni-abend")) {
				int modifier=0;
				//String url = "http://www.zfv.ch/bhb/menuadmin/displaymenu.aspx"; change on 081010 to new system (pdf)
				String url = "http://www.zfv.ch/menuplan/menuxml.aspx";
				if (text.equals("uni")) {
					text = "Unimensa (unten)";
					url+="?id=148";
				}
				if (text.equals("uni-abend")) {
					text = "Unimensa (unten, Abend)";
					url+="?id=149";
				}
				// this might break if the date is 09.01 instead of 9.1, but cannot check now
				String date;
				if ((Calendar.getInstance().get(Calendar.MONTH)+1)<10)
					date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".0"+(Calendar.getInstance().get(Calendar.MONTH)+1);
				else
					date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"."+(Calendar.getInstance().get(Calendar.MONTH)+1);
//				switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
//					case Calendar.MONDAY: day = "Montag"; break;
//					case Calendar.TUESDAY: day = "Dienstag"; break;
//					case Calendar.WEDNESDAY: day = "Mittwoch"; break;
//					case Calendar.THURSDAY: day = "Donnerstag"; break;
//					case Calendar.FRIDAY: day = "Freitag"; break;
//					default: day = "No mensa today"; break;
//				}
				page = getRequest(url);
				modifier=page.indexOf(date);
				if (modifier!=-1) {
					modifier = page.indexOf("<menutext", modifier)+10;
					String res = "[Menu mit Fleisch]: "+page.substring(modifier,page.indexOf("</menutext>", modifier));
					modifier=page.indexOf(date,modifier);
					modifier = page.indexOf("<menutext", modifier)+10;
					res += " [Vegi]: "+page.substring(modifier,page.indexOf("</menutext>", modifier));
					page = res;
				} else { page = "Keine Menuinformationen gefunden"; }
			}
			if (text.equals("help")) {
				page = "Mensa plugin. Format: '"+this.command+" mensa', wobei mensa = {poly | infobar | clausius | cafeteria | gloria | poly-abend | cafeteria-abend | uni | uni-abend}";
			}
			String stripped = stripHTML(page);
			/* hacky way to stay in the limit of max 512 chars per msg */
			int len = stripped.length()+text.length()+msg.getShortNick().length();
			if (len>400) {
				len-=stripped.length();
				stripped = stripped.substring(0,400-len)+"...";
			}
			sendResponse(new Response(msg, msg.getShortNick()+", ("+text+"): "+stripped));
		}
	}

}
