package net.nebelwelt.CoffeeBot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.nebelwelt.CoffeeBot.network.IRCHandler;

public class Main {
	static Properties configuration;
	public static String version = "CoffeeBot v0.01 2007-08-10 ( mathias.payer@nebelwelt.net )";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		configuration = new Properties();
		String configFile;
		if (args.length==1)
			configFile = args[0];
		else
			configFile = "config.properties";
		try {
			configuration.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: Configuration file ("+configFile+") not found!");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR: Configuration file ("+configFile+") could not be read!");
			e.printStackTrace();
			System.exit(1);
		}
		Logger.logLevel = Integer.parseInt(configuration.getProperty("CoffeeBot.logLevel","9"));
		Logger.log(9,"Configuration found and loaded");
		
		IRCHandler irchandler = new IRCHandler();

		MessageHandler msghandler = new MessageHandler(irchandler);
		irchandler.registerMessageHandler(msghandler);
		Logger.log(9, "Started irc and plugin thread, let's go");
		try {
			msghandler.loadPlugins();
		} catch (ConfigurationException e) {
			System.err.println("ERROR: Could not load (all?) plugins!");
			e.printStackTrace();
			System.exit(1);
		}
		
		msghandler.start();
		Logger.log(9, "Plugins loaded, trying to connect");
		try {
			irchandler.connect();
		} catch (ConfigurationException e) {
			System.err.println("ERROR: Could not connect to irc server!");
			e.printStackTrace();
			System.exit(1);
		}
		msghandler.afterConnect();
		/* sleep for a bot so that the irc server knows that we are connected */
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {/* ignore*/ }
		Logger.log(9, "Connected, now trying to authenticate");
		irchandler.registerNick();
		Logger.log(9, "Connected, now trying to join channel");
		try {
			irchandler.joinChannel();
		} catch (ConfigurationException e) {
			System.err.println("ERROR: Could not join channel(s)!");
			e.printStackTrace();
			System.exit(1);
		}
		msghandler.afterJoin();
		Logger.log(9, "Joined channel, startup done.");
	}

	public static String getConfiguration(String config) {
		return configuration.getProperty(config);
	}
	
	public static String getConfiguration(String config, String def) {
		return configuration.getProperty(config, def);
	}
}
