package net.nebelwelt.CoffeeBot.plugins.Markoff;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * One actual Markoff Element in the chain. With Links and Probabilities to the following Elements
 * @author gannimo
 *
 */
public class MarkoffElement {
	public static final int WORD = 1;
	public static final int PUNCTUATION = 2;
	private String member;
	private int type = 0;
	private int nrLastWord = 0;
	private int nrFirstWord = 0;
	private int outgoingEdges = 0;
	private ArrayList<MarkoffLink> neighbours;

	public MarkoffElement(String member) {
		this.member = member;
		this.neighbours = new ArrayList<MarkoffLink>();
		if (member.equals(":") || member.equals(";") || member.equals(".") || member.equals(",") || member.equals("?") || member.equals("!") 
				|| member.equals("\"") || member.equals("'")) {
			type = MarkoffElement.PUNCTUATION;
		} else {
			type = MarkoffElement.WORD;
		}
	}
	
	public int getNrEdges() {
		return outgoingEdges+nrLastWord;
	}
	
	public int getType() {
		return type;
	}
	
	public String getMember() {
		return member;
	}
	
	public void isLastWord() {
		nrLastWord++;
	}
	
	public void setLastWord(int val) {
		nrLastWord = val;
	}
	
	public int getNrLastWord() {
		return nrLastWord;
	}
	
	public void isFirstWord() {
		nrFirstWord++;
	}
	
	public void setNrFirstWord(int val) {
		nrFirstWord = val;
	}
	
	public int getNrFirstWord() {
		return nrFirstWord;
	}
	
	/**
	 * Adds a given neighbour based on a MarkoffElement, constructs a link
	 * @param newElem neighbour element
	 */
	public void addNeighbour(MarkoffElement newElem) {
		addNeighbour(newElem, 1);
	}
	
	/**
	 * Adds a given neighbour based on a MarkoffElement, constructs a link with a given activity
	 * @param newElem neighbour element
	 * @param nrFollow activity of this link
	 */
	public void addNeighbour(MarkoffElement newElem, int nrFollow) {
		/* do we already have such a neighbour - increase activity */
		outgoingEdges+=nrFollow;
		Iterator<MarkoffLink> iter = neighbours.iterator();
		while (iter.hasNext()) {
			MarkoffLink current = iter.next();
			if (current.getDest().getMember().equals(newElem.getMember())) {
				current.increaseActivity(nrFollow);
				return;
			}
		}
		/* otherwise add a new link */
		MarkoffLink link = new MarkoffLink(nrFollow, this, newElem);
		neighbours.add(link);
	}
	
	/**
	 * Adds a new neighbour based on a given link
	 * @param newElem link to neighbour
	 */
	public void addNeighbour(MarkoffLink newElem) {
		outgoingEdges+=newElem.getActivity();
		neighbours.add(newElem);
	}
	
	/**
	 * Returns the next random element (based on a random probability and the neighbours activity)
	 * @return next element
	 */
	public MarkoffElement getNext() {
		return getNext(Math.random());
	}
	
	/**
	 * Returns the next element (based on the neighbours activity and the given probability)
	 * @param probability selection criteria
	 * @return next element
	 */
	public MarkoffElement getNext(double probability) {
		double prob = 0;
		MarkoffLink current = null;
		Iterator<MarkoffLink> iter = neighbours.iterator();
		while (iter.hasNext() && prob < probability) {
			current = iter.next();
			prob += current.getProbability();
		}
		if (prob<probability || current == null)
			return null;
		else
			return current.getDest();
	}
	
	/**
	 * Returns all neighbours of this element
	 * @return all neighbours
	 */
	public ArrayList<MarkoffLink> getNeighbours() {
		return neighbours;
	}
}
