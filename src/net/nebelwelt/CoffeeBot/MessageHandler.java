package net.nebelwelt.CoffeeBot;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.network.IRCHandler;
import net.nebelwelt.CoffeeBot.plugins.BasicPlugin;

public class MessageHandler extends Thread {
	List<BasicPlugin> registeredPlugins;
	IRCHandler handler;
	
	public MessageHandler(IRCHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * This method tries to resolve, load and initialize all plugins given in the confguration
	 * @throws ConfigurationException
	 */
	public void loadPlugins() throws ConfigurationException {
		/* Get parameters */
		Logger.log(7, "Loading plugins...");
		registeredPlugins = new ArrayList<BasicPlugin>();
		String loadablePlugins = Main.configuration.getProperty("CoffeeBot.plugins");
		if (loadablePlugins == null) throw new ConfigurationException("No plugins defined or misconfiguration in configfile");
		StringTokenizer plugins = new StringTokenizer(loadablePlugins,", ");
		/* Iterate through all plugins and initialize them */
		while (plugins.hasMoreTokens()) {
			String currentPlugin = plugins.nextToken();
			Logger.log(8, "Loading plugin "+currentPlugin);
			String command = Main.configuration.getProperty("CoffeeBot.plugin."+currentPlugin+".command");
			String klass = Main.configuration.getProperty("CoffeeBot.plugin."+currentPlugin+".class", "");
			/* Use reflection to instantiate the current plugin */
			Constructor pluginConstructor = null;
			try {
				pluginConstructor = Class.forName(klass).getConstructor(new Class[] { String.class, String.class, IRCHandler.class });
			} catch (SecurityException e) {
				throw new ConfigurationException("Could not initialize plugin - SecurityException ("+e.getMessage()+"). Check plugin '"+currentPlugin+"' with class '"+klass+"'");
			} catch (NoSuchMethodException e) {
				throw new ConfigurationException("Could not initialize plugin - NoSuchMethodException ("+e.getMessage()+"). Check plugin '"+currentPlugin+"' with class '"+klass+"'");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ConfigurationException("Could not initialize plugin - ClassNotFoundException ("+e.getMessage()+"). Check plugin '"+currentPlugin+"' with class '"+klass+"'");
			}
			BasicPlugin plugin = null;
			try {
				plugin = (BasicPlugin)(pluginConstructor.newInstance(new Object[] { currentPlugin, command, handler }));
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConfigurationException("Could not initialize plugin - Instantioationproblem ("+e.getMessage()+"). Check plugin '"+currentPlugin+"' with class '"+klass+"'");
			}
			
			/* initialize it */
			plugin.initialize("CoffeeBot.plugin."+currentPlugin);
			
			/* and register it to our list of active plugins */
			registeredPlugins.add(plugin);
		}
		
	}

	public void afterConnect() {
		Logger.log(7, "After connect, informing plugins");
		for (BasicPlugin plugin : registeredPlugins)
			plugin.afterConnect();
	}

	public void afterJoin() {
		Logger.log(7, "After join, informing plugins");
		for (BasicPlugin plugin : registeredPlugins)
			plugin.afterJoin();
	}

	public void beforeLeave() {
		Logger.log(7, "Before leave, informing plugins");
		for (BasicPlugin plugin : registeredPlugins)
			plugin.beforeLeave();
	}

	public void beforeQuit() {
		Logger.log(7, "Before quit, informing plugins");
		for (BasicPlugin plugin : registeredPlugins)
			plugin.beforeQuit();
	}
	
	public void run() {
		Logger.log(7, "Started MessageHandler thread");
		/* loop until there are no new messages */
		while (true) {
			Message query = handler.getNextQuery();
			if (query==null) {
				Logger.log(6,"No new messages, exiting MessageHandler thread...");
				return;
			}
			Logger.log(8, "Got a new message - dispatching");
			/* we got a message, check all plugins for a match */
			for (BasicPlugin plugin : registeredPlugins) {
				try {
					if (plugin.checkMessage(query)) {
						plugin.handleMessage(query);
					}
				} catch (Exception e) {
					Logger.log(2,"Plugin ("+plugin.getName()+")caused exception ("+e.getMessage()+")!");
				}
			}
		}
	}
}
