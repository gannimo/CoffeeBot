package net.nebelwelt.CoffeeBot.Messages;
/**
 * This is a response message to a given IRC query.
 * It can either be generated by the system or by a plugin response.
 * @author gannimo
 *
 */
public class Response extends Message {
	private boolean privmsg = false;
	private String target = "";
	private String text = "";
	
	public Response(boolean privmsg, String target, String text) {
		this.privmsg = privmsg;
		this.target = target;
		this.text = text;
	}
	
	public Response(Query query, String text) {
		this.privmsg = query.isPrivmsg();
		this.target = this.privmsg?query.getShortNick():query.getChan();
		this.text = text;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String nick) {
		this.target = nick;
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
	}
	
	public String getMessage() {
		return "PRIVMSG "+getTarget()+" :"+getText();
	}
	public int getType() {
		return Message.RESPONSE;
	}
}
