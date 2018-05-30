package net.nebelwelt.CoffeeBot.plugins.Eliza;
/***************************************************************************
                   Replace.java  -  replace string-occurences
                             -------------------
    begin                : Wed Mai  9 09:04:04 UTC 2001
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
public class Replace {
	 public static String all( String text,
										String oldSubString,
										String newSubString ) {
		  String retVal;
		  int index = text.indexOf( oldSubString );
		  if ( index == -1 )
				retVal = text;
		  else
 				retVal = all( text.substring( 0, index ) + newSubString +
								  text.substring( index +
														oldSubString.length() ),
								  oldSubString, newSubString );
		  return retVal;
	 }
}
