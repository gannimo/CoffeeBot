package net.nebelwelt.CoffeeBot.Messages;

import net.nebelwelt.CoffeeBot.Logger;

/**
 * This represents a query from the IRC server.
 * It can either be a privmsg or a channelmsg.
 * @author gannimo
 *
 */
public class Query extends Message {
	private boolean privmsg = false;
	private String nick = "";
	private String chan = "";
	private String text = "";
	private String firstWord = "";
	
	public Query(boolean privmsg, String nick, String chan, String text) {
		super(":"+nick+chan+" :"+text);
		this.privmsg = privmsg;
		this.nick = nick;
		this.chan = chan;
		this.text = text;
		setFirstWord();
	}
	
	public Query(String query) {
		super(query);
		/* extract query information and build a new Query object */
		String nick = query.substring(1,query.indexOf(" PRIVMSG "));
		String rest = query.substring(query.indexOf(" PRIVMSG ")+9);
		String chan = rest.substring(0, rest.indexOf(" :"));
		boolean privmsg = chan.indexOf("#")==-1;
		String text = rest.substring(rest.indexOf(" :")+2);
		this.nick = nick;
		this.chan = chan;
		this.text = text;
		this.privmsg = privmsg;
		setFirstWord();
		Logger.log(6,query.toString());

	}
	
	private void setFirstWord() {
		if (text.indexOf(" ")==-1) firstWord = text;
		else firstWord = text.substring(0, text.indexOf(" "));
	}
	
	public String getChan() {
		return chan;
	}
	public void setChan(String chan) {
		this.chan = chan;
	}
	public String getNick() {
		return nick;
	}
	public String getShortNick() {
		String shortNick;
		if (nick.indexOf("!")==-1) {
			shortNick = nick;
		} else {
			shortNick = nick.substring(0, nick.indexOf("!"));
		}
		return shortNick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public boolean isPrivmsg() {
		return privmsg;
	}
	public void setPrivmsg(boolean privmsg) {
		this.privmsg = privmsg;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		setFirstWord();
	}
	public String getFirstWord() {
		return firstWord;
	}
	public String toString() {
		return "Nick:"+nick+" #:"+chan+" text:"+text+(privmsg?" priv":" chan");
	}
	
	public int getType() {
		return Message.QUERY;
	}

}
