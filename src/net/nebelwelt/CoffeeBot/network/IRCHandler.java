package net.nebelwelt.CoffeeBot.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.nebelwelt.CoffeeBot.ConfigurationException;
import net.nebelwelt.CoffeeBot.Logger;
import net.nebelwelt.CoffeeBot.Main;
import net.nebelwelt.CoffeeBot.MessageHandler;
import net.nebelwelt.CoffeeBot.Messages.Message;
import net.nebelwelt.CoffeeBot.Messages.Query;

public class IRCHandler {
	public final static int CONNECTING = 1;
	public final static int CONNECTED = 2;
	public final static int JOINED = 3;
	
	private static ConcurrentLinkedQueue<Message> queuedQueries;
	private static ConcurrentLinkedQueue<Message> queuedResponses;
	private int state = 0;
	private static boolean stopSpinning = false;
	
	private Socket ircConnection;
	private int port = 0;
	private String server = null;
	private String username = null;
	private String nickname = null;
	private String realname = null;
	private String channel[] = null;
	
	BufferedWriter ircout;
	BufferedReader ircin;
	
	private IRCReader ircreader;
	private IRCWriter ircwriter;
	private MessageHandler msghandler;
	
	{
		queuedQueries = new ConcurrentLinkedQueue<Message>();
		queuedResponses = new ConcurrentLinkedQueue<Message>();
	}
	
	/**
	 * This method gets a new query from the queue or blocks until
	 * there is a new query.
	 * @return
	 */
	public Message getNextQuery() {
		synchronized (queuedQueries) {
			while (queuedQueries.isEmpty() && stopSpinning==false) {
				try {
					queuedQueries.wait();
				} catch (InterruptedException e) {
					Logger.log(1,"Got interrupted, did not expect this!");
					e.printStackTrace();
				}
			}
			return queuedQueries.poll();
		}
	}

	/**
	 * This method gets a new response from the queue or blocks until
	 * there is a new response.
	 * @return
	 */
	public Message getNextResponse() {
		synchronized (queuedResponses) {
			while (queuedResponses.isEmpty() && stopSpinning==false) {
				try {
					queuedResponses.wait();
				} catch (InterruptedException e) {
					Logger.log(1,"Got interrupted, did not expect this!");
					e.printStackTrace();
				}
			}
			return queuedResponses.poll();
		}
	}
	
	/**
	 * This method enqueues a new response and wakes up all
	 * waiting threads on that queue.
	 * @param response
	 */
	public void putResponse(Message response) {
		synchronized (queuedResponses) {
			boolean wakeUp = queuedResponses.isEmpty();
			queuedResponses.add(response);
			if (wakeUp) queuedResponses.notifyAll();
		}
	}
	
	/**
	 * This method enqueues a new query and wakes up all
	 * waiting threads on that queue.
	 * @param query
	 */
	public void putQuery(Query query) {
		synchronized (queuedQueries) {
			boolean wakeUp = queuedQueries.isEmpty();
			queuedQueries.add(query);
			if (wakeUp) queuedQueries.notifyAll();
		}
	}
	
	/**
	 * stops the queues and notifies all waiting threads
	 *
	 */
	public void shutdown() {
		stopSpinning = true;
		msghandler.beforeLeave();
		try {
			ircout.write("LEAVE #"+channel+" :"+Main.getConfiguration("CoffeeBot.quitmsg","Hasta luego..."));
			ircout.newLine();
			ircout.flush();
		} catch (Exception e) {
			System.err.println("ERROR: Could not shut down connections");
			e.printStackTrace();
			// ignore as we are already closing the connection
		}
		msghandler.beforeQuit();
		synchronized (queuedResponses) {
			queuedResponses.notifyAll();
		}
		synchronized (queuedQueries) {
			queuedQueries.notifyAll();
		}
		try {
			ircwriter.join(1000);
			ircout.write("QUIT :"+Main.getConfiguration("CoffeeBot.quitmsg","Hasta luego..."));
			ircout.newLine();
			ircout.flush();
			Thread.sleep(500);
			ircin.close();
			ircreader.join(1000);
			ircout.close();
			ircConnection.close();
		} catch (Exception e) {
			System.err.println("ERROR: Could not shut down connections");
			e.printStackTrace();
			// ignore as we are already closing the connection
		}
	}
	
	/**
	 * Conntects to the specified IRC network in the configs
	 * @throws ConfigurationException
	 */
	public void connect() throws ConfigurationException {
		state=CONNECTING;
		port = Integer.parseInt(Main.getConfiguration("CoffeeBot.ircport", "6667"));
		server = Main.getConfiguration("CoffeeBot.ircserver");
		username = Main.getConfiguration("CoffeeBot.user", "CoffeeBot");
		nickname = Main.getConfiguration("CoffeeBot.nick", "CoffeeBot");
		realname = Main.getConfiguration("CoffeeBot.realname", "CoffeeBot");
		
		if (server == null) {
			throw new ConfigurationException("No IRC server found in configuration (set CoffeeBot.server)!");
		}
		try {
			this.ircConnection = new Socket(server, port);
		} catch (UnknownHostException e) {
			throw new ConfigurationException("Host not found: "+server+" (port: "+port+")");
		} catch (IOException e) {
			System.err.println("ERROR: IOException during connect.");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			ircout = new BufferedWriter(new OutputStreamWriter(ircConnection.getOutputStream()));
			ircin = new BufferedReader(new InputStreamReader(ircConnection.getInputStream()));
		} catch (IOException e) {
			System.err.println("ERROR: IOException during connect.");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			ircout.write("NICK "+nickname);
			ircout.newLine();
			ircout.write("USER "+username+" "+InetAddress.getLocalHost().getCanonicalHostName()+" "+server+" "+realname);
			ircout.newLine();
			ircout.flush();
		} catch (IOException e) {
			System.err.println("ERROR: IOException during authentication.");
			e.printStackTrace();
			System.exit(1);
		}
		
		ircreader = new IRCReader(this, ircin);
		ircreader.start();
		
		ircwriter = new IRCWriter(this, ircout);
		ircwriter.start();
		
		try {
			synchronized(this) {
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Logger.log(7, "Finished connecting");
	}
	
	/**
	 * joins the specified IRC channel
	 * @throws ConfigurationException 
	 *
	 */
	public void joinChannel() throws ConfigurationException {
		int nrchannel = 0;
		try {
			nrchannel = Integer.parseInt(Main.getConfiguration("CoffeeBot.nrchannels", "1"));
			channel = new String[nrchannel];
			for (int i=0; i<nrchannel; i++) {
				channel[i] = Main.getConfiguration("CoffeeBot.channel"+i, "CoffeeBot");
			}
		} catch (Exception e) {
			throw new ConfigurationException("No channels defined (set CoffeBot.nrchannels and CoffeBot.channel[i], i starts with 0)!");
		}
		try {
			for (int i=0; i<nrchannel; i++) {
				ircout.write("JOIN #"+channel[i]);
				ircout.newLine();
				ircout.flush();
			}
		} catch (IOException e) {
			System.err.println("ERROR: IOException during channel join.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void registerNick() {
		String nickserv = Main.getConfiguration("CoffeeBot.nickserv", "");
		String command = Main.getConfiguration("CoffeeBot.nickservCommand", "");
		if (!nickserv.equals("") && !command.equals("")) {
			try {
				ircout.write("PRIVMSG "+nickserv+" :"+command);
				ircout.newLine();
				ircout.flush();
				Thread.sleep(200);
			} catch (Exception e) {
				System.err.println("ERROR: Exception during nickserv authentication.");
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			Logger.log(3, "No authentication information found");
		}
	}

	/**
	 * registers the MessageHandler so that IRCHandler and MessageHandler
	 * can relay messages
	 * @param msghandler
	 */
	public void registerMessageHandler(MessageHandler msghandler) {
		this.msghandler = msghandler;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
