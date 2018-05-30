package net.nebelwelt.CoffeeBot.plugins.Eliza;
/***************************************************************************
                   Eliza.java  -  eliza chatterbot
                             -------------------
    begin                : Tue Mai  8 08:07:12 UTC 2001
    copyright            : (C) 2000 by Jan Wedekind
    email                : Jan.Wedekind@stud.uni-karlsruhe.de
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
import java.io.*;

public class Eliza {

	 protected String previousInput;

	 protected ElizaComments elizaComments;

	 public Eliza( InputStream stream ) throws IOException {
		  previousInput = "";
		  
		  elizaComments = new ElizaComments( stream );
	 }

	 

	 public String getGreetString() {
		  return "Hi. I'm Eliza. Tell me your problems!";
	 }

	 public String getSayGoodByeString() {
		  return
				"Bye Bye. It was interesting to talk with a lower intelligence.";
	 }

	 protected String getRepeatString() {
		  return "Please don't say it again!";
	 }

	 public String getResponse( String userInput ) {
		  // Clean the user-input.
		  String cleanedInput = cleanInput( userInput );
		  // Convert to upper-case.
		  String upperInput = userInput.toUpperCase();
		  String retVal;
		  if ( upperInput.equals( previousInput ) )
				retVal = getRepeatString();
		  else {
				previousInput = upperInput;
				retVal = cleanOutput( elizaComments.getAnswer( cleanedInput ) );
		  };
		  return retVal;
	 }

	 protected static String cleanInput( String input ) {
		  return
				Replace.all( Replace.all( Replace.all( Replace.all( Replace.all(
					Replace.all( Replace.all( Replace.all(
						 " " + input + " ", "*", "" ), "#", "" ), ".", "" ),
					"!", "" ), "?", "" ), ",", " # " ), "#"," , " ), "  ", " " );
	 }

	 protected static String cleanOutput( String output ) {
		  return
				Replace.all( Replace.all( Replace.all( Replace.all(
					Replace.all( output, " .", "." ), " !", "!" ),
                  " ?", "?" ), " ,", "," ), "  ", " " );
	 }
}
