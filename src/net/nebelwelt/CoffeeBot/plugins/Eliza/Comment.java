package net.nebelwelt.CoffeeBot.plugins.Eliza;
/***************************************************************************
                   ElizaComments.java  -  Table for eliza-comments.
                             -------------------
    begin                : Tue Mai  8 23:59:12 UTC 2001
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
import java.util.*;

public class Comment {

	 protected Vector<String> phrases;
	 protected Vector<String> answers;
	 protected int answerIndex;

	 public Comment( BufferedReader reader ) throws IOException {
		  phrases = new Vector<String>();
		  answers = new Vector<String>();
		  answerIndex = 0;
		  while ( true ) {
				String line = reader.readLine();
				if ( line.equals( "ANSWER" ) )
					 break;
				phrases.addElement( line.toUpperCase() );
		  };
		  while ( true ) {
				String line = reader.readLine();
				if ( line == null )
					 break;
				if ( line.equals( "" ) )
					 break;
				answers.addElement( line );
		  };
	 }

	 public String getAnswer() {
		  String retVal;
		  if ( answers.size() > 0 ) {
				retVal = (String)answers.elementAt( answerIndex );
				answerIndex++;
				if ( answerIndex >= answers.size() )
					 answerIndex = 0;
		  } else {
				retVal = "";
		  };
		  return retVal;
	 }

	 public int getNumberOfPhrases() {
		  return phrases.size();
	 }

	 public String getPhrase( int i ) {
		  return (String)phrases.elementAt( i );
	 }

	 /*
		public String toString() {
		String retVal = "PHRASE\n";
		for ( int i=0; i<phrases.size(); i++ )
		retVal += (String)phrases.elementAt( i ) + '\n';
		retVal += "ANSWER\n";
		for ( int i=0; i<answers.size(); i++ )
		retVal += (String)answers.elementAt( i ) + '\n';
		return retVal;
		}
	 */
}

