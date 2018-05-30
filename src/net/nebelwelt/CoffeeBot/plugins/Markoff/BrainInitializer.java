package net.nebelwelt.CoffeeBot.plugins.Markoff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BrainInitializer {

	private MarkoffChain markoff;
	
	private void constructDatabase(String infile, String outfile) {
		markoff = new MarkoffChain(outfile);
		try {
			/*
			 * Reads a file of the Markoff-Format
			 * generates a map-structure for easy access
			 */
			BufferedReader fin = new BufferedReader(new FileReader(infile));
			int lines = 0;
			while (fin.ready()) {
				String line = fin.readLine();
				if (!(line.startsWith("---") || line.indexOf("-!-")!=-1 || line.indexOf(">>>")!=-1 || line.indexOf(":.:")!=-1)) {
					line = line.substring(5).trim();
					line = line.substring(line.indexOf(">")+1).trim();
					//System.out.println(line);
					lines++;
					if (lines%1000==0)
						System.out.print(".");
					markoff.learn(line);
				}
			}
			markoff.saveBrain();
			for (int i=0; i<20; i++) {
				System.out.println(markoff.getLine());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file '"+infile+"' - error in BrainInitializer");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not access file '"+infile+"' - error in BrainInitializer");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length!=2) {
			System.out.println("BrainInitializer <InFile> <BrainFile>");
			System.exit(1);
		}
		System.out.println("Initializing Markoff Brain...");
		System.out.println("Reading data from "+args[0]+" and saving to "+args[1]);
		BrainInitializer local = new BrainInitializer();
		local.constructDatabase(args[0], args[1]);
		System.out.println("done");
	}

}
