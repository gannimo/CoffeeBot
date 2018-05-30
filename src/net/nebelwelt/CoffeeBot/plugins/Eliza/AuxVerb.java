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

public class AuxVerb {

	 protected String original;
	 protected String switched;

	 public AuxVerb( BufferedReader reader ) throws IOException {
		  original = reader.readLine();
		  switched = reader.readLine();
	 }

	 public String getOriginal() {
		  return original;
	 }

	 public String getSwitched() {
		  return switched;
	 }

}

