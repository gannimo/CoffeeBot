package net.nebelwelt.CoffeeBot.Messages;

/**
 * Simples IRC message that is sent over the network
 * The IRCWriter uses the getMessage to send this Object
 * to the network
 * @author gannimo
 *
 */
public class Message {
	public static final int MESSAGE = 0;
	public static final int QUERY = 1;
	public static final int RESPONSE = 2;
	
	private String message = null;
	
	public Message(String message) {
		this.message = message;
	}
	public Message() {}
	
	public String getMessage() {
		return message;
	}
	public int getType() {
		return Message.MESSAGE;
	}
}
