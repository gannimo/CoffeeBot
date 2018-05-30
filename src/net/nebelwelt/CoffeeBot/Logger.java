package net.nebelwelt.CoffeeBot;

import java.io.PrintStream;

public class Logger {
	static PrintStream out = System.out;
	static int logLevel = 9;
	static String[] levelName = { "FATAL ERROR: ", "ERROR: ", "WARNING: ", "NOTICE: " };
	public static void log(int level, String message) {
		if (level<=logLevel) {
			int levelIndex = (level>levelName.length-1) ? levelName.length-1 : level;
			out.println(levelName[levelIndex]+message);
		}
	}
	
}
