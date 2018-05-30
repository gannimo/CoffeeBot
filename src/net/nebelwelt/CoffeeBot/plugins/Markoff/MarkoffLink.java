package net.nebelwelt.CoffeeBot.plugins.Markoff;

/**
 * This class represents a link from one markoff element to another
 * @author gannimo
 *
 */
public class MarkoffLink {
	private int activity = 0;
	private MarkoffElement source = null;
	private MarkoffElement dest = null;
	
	public MarkoffLink(int activity, MarkoffElement source, MarkoffElement dest) {
		this.activity = activity;
		this.source = source;
		this.dest = dest;
	}
	
	/**
	 * Gets the links destination
	 * @return destination
	 */
	public MarkoffElement getDest() {
		return dest;
	}
	
	/**
	 * Gets the links source
	 * @return source
	 */
	public MarkoffElement getSource() {
		return source;
	}
	
	/**
	 * Probability that this dest should be chosen
	 * @return link probability
	 */
	public double getProbability() {
		return (double)activity/source.getNrEdges();
	}
	
	/**
	 * Number of times this link was followed (in real text)
	 * @return link activity
	 */
	public int getActivity() {
		return activity;
	}
	
	/**
	 * Increases the activity of this link
	 *
	 */
	public void increaseActivity() {
		activity++;
	}
	
	/**
	 * Increases the activity of this link
	 */
	public void increaseActivity(int inc) {
		activity+=inc;
	}
}
