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

public class ElizaComments {

	 protected Vector<Comment> comments;
	 protected Vector<AuxVerb> auxVerbs;
	 protected Comment lameExcuse;
	 
	 public ElizaComments( InputStream stream ) throws IOException {
		  BufferedReader
				reader = new BufferedReader( new InputStreamReader( stream ) );
		  comments = new Vector<Comment>();
		  auxVerbs = new Vector<AuxVerb>();
		  lameExcuse = null;
		  while( true ) {
				String line = reader.readLine();
				if ( line == null )
					 break;
				if ( line.equals( "PHRASE" ) ) {
					 Comment comment = new Comment( reader );
					 comments.addElement( comment );
					 if ( comment.getNumberOfPhrases() == 0 )
						  lameExcuse = comment;
				} else if ( line.equals( "AUXVERB" ) ) {
					 AuxVerb auxVerb = new AuxVerb( reader );
					 auxVerbs.addElement( auxVerb );
				};
		  }
		  reader.close();
	 }

	 public String getAnswer( String input ) {
		  String upperInput = input.toUpperCase();
		  for ( int i=0; i<comments.size(); i++ ) {
				Comment comment = (Comment)comments.elementAt( i );
				for ( int j=0; j<comment.getNumberOfPhrases(); j++ ) {
					 String phrase = comment.getPhrase( j );
					 int index = upperInput.indexOf( " " + phrase + " " );
					 if ( index != -1 ) {
						  String inputPhrase =
								adaptAuxVerbs( input.substring( index + 2 +
																		  phrase.length() ) );
						  return Replace.all( comment.getAnswer(),
													 "*", inputPhrase );
					 };
				};
		  };
		  return Replace.all( lameExcuse.getAnswer(), "*",
									 adaptAuxVerbs( input ) );
	 }

	 public String adaptAuxVerbs( String input ) {
		  String retVal = input;
		  for ( int i=0; i<auxVerbs.size(); i++ ) {
				AuxVerb auxVerb = (AuxVerb)auxVerbs.elementAt( i );
				retVal = Replace.all( retVal, " " + auxVerb.getOriginal() + " ",
											 " " + auxVerb.getSwitched() + "# " );
				retVal = Replace.all( retVal, " " + auxVerb.getSwitched() + " ",
											 " " + auxVerb.getOriginal() + "# " );
		  };
		  return Replace.all( retVal, "#", "" );
	 }
}
