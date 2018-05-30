package net.nebelwelt.CoffeeBot.plugins.Markoff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.nebelwelt.CoffeeBot.Logger;

public class MarkoffChain {
	private HashMap<String,MarkoffElement> brain;
	private String file;
	/* start point for all chains, this is an artificial element */
	private MarkoffElement magicStart;
	
	/**
	 * Constructor for the MarkoffChain with a given configuration database (aka brain)
	 * @param file
	 */
	public MarkoffChain(String file) {
		this.file = file;
		this.magicStart = new MarkoffElement("");
		this.brain = new HashMap<String,MarkoffElement>();
		try {
			/*
			 * Reads a file of the Markoff-Format
			 * generates a map-structure for easy access
			 */
			BufferedReader fin = new BufferedReader(new FileReader(this.file));
			while (fin.ready()) {
				String line = fin.readLine();
				parse(line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file '"+file+"' - error in MarkoffChain");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not access file '"+file+"' - error in MarkoffChain");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * This method parses an input line from the database
	 * <nrFirstWord>:WORD:<nrLastWord> NEIGH1:<nrNeigh1> NEIGH2:<nrNeigh2> NEIGH3:<nrNeigh3> ...
	 * @param input
	 */
	private void parse(String input) {
		StringTokenizer tok = new StringTokenizer(input, " ");
		String name = tok.nextToken();
		
		int nrFirst = Integer.parseInt(name.substring(0, name.indexOf(":.:")));
		name = name.substring(name.indexOf(":.:")+3);
		int nrLast = Integer.parseInt(name.substring(name.indexOf(":.:")+3));
		name = name.substring(0, name.indexOf(":.:"));
		
		MarkoffElement root = findOrAddElement(name);
		
		/* set parameters */
		root.setLastWord(nrLast);
		root.setNrFirstWord(nrFirst);
		if (nrFirst!=0) {
			magicStart.addNeighbour(root);
		}
		
		while (tok.hasMoreElements()) {
			String neighbour = tok.nextToken();
			int activity = Integer.parseInt(neighbour.substring(neighbour.indexOf(":.:")+3));
			neighbour = neighbour.substring(0, neighbour.indexOf(":.:"));
			
			MarkoffElement neigh = findOrAddElement(neighbour);
			MarkoffLink link = new MarkoffLink(activity, root, neigh);
			root.addNeighbour(link);
		}
	}
	
	/**
	 * learns a line of text
	 * @param line
	 */
	public void learn(String line) {
		StringTokenizer tok = new StringTokenizer(line, " ");
		MarkoffElement lastElem = magicStart;
		while (tok.hasMoreElements()) {
			String curr = tok.nextToken();

			/* find and add neighbour */
			MarkoffElement thisElem = findOrAddElement(curr);
			
			if (lastElem == magicStart) thisElem.isFirstWord();
			
			lastElem.addNeighbour(thisElem);
			
			lastElem = thisElem;
		}
		lastElem.isLastWord();
	}

	/**
	 * Finds an element in the brain database or adds a new one
	 * @param linkName name of the element
	 * @return the element
	 */
	private MarkoffElement findOrAddElement(String linkName) {
		MarkoffElement thisElem = brain.get(linkName);
		if (thisElem == null) {
			thisElem = new MarkoffElement(linkName);
			brain.put(linkName, thisElem);
		}
		return thisElem;
	}
	
	/**
	 * This function saves the database to the given configuration file
	 *
	 */
	public void saveBrain() {
		try {
			synchronized (file) {
			BufferedWriter fout = new BufferedWriter(new FileWriter(file, false));
			Iterator<MarkoffElement> iter = brain.values().iterator();
			while (iter.hasNext()) {
				/* process one element */
				StringBuffer strOut = new StringBuffer("");
				MarkoffElement current = iter.next();
				if (current.getMember().indexOf(":.:")==-1) {
					strOut.append(current.getNrFirstWord());
					strOut.append(":.:");
					strOut.append(current.getMember());
					strOut.append(":.:");
					strOut.append(current.getNrLastWord());
					Iterator<MarkoffLink> iter2 = current.getNeighbours().iterator();
				
					/* add all our links to the current line */
					while (iter2.hasNext()) {
						MarkoffLink curml = iter2.next();
						if (curml.getDest().getMember().indexOf(":.:")==-1) {
							strOut.append(" ");
							strOut.append(curml.getDest().getMember());
							strOut.append(":.:");
							strOut.append(curml.getActivity());
						}
					}
					
					/* write to the file */
					fout.write(strOut.toString());
					fout.newLine();
				}
			}
			fout.flush();
			fout.close();
			}
		} catch (IOException e) {
			Logger.log(1, "Coult not add braindata - IOException in Markoff");
		}
	}
	
	public String getLine() {
		StringBuffer ret = new StringBuffer();
		MarkoffElement cur = magicStart.getNext();
		while (cur!=null && ret.length()<300) {
			ret.append(cur.getMember());
			ret.append(" ");
			cur = cur.getNext();
		}
		if (ret.length()>=300) ret.append(".");
		return ret.toString();
	}
	
}
