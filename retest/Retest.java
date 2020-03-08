/*
	This file is part of jretest.

	 jretest is free software: you can redistribute it and/or modify
	 it under the terms of the GNU General Public License as published by
	 the Free Software Foundation, either version 3 of the License, or
	 (at your option) any later version.

	 retest is distributed in the hope that it will be useful,
	 but WITHOUT ANY WARRANTY; without even the implied warranty of
	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 GNU General Public License for more details.

	 You should have received a copy of the GNU General Public License
	 along with it.  If not, see <https://www.gnu.org/licenses/>.
*/

package retest;

import java.io.EOFException;
import java.util.Scanner;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gnu.readline.*;

public class Retest {
	
	public static final String NOR = "\033[0m";
	public static final String RED = "\033[31m";
	public static final String GREEN = "\033[32m";
	public static final String YELLOW = "\033[33m";
	public static final String CYAN = "\033[36m";

	
	static final String HS0 = YELLOW+"retest"+NOR+": ";
	
	static final String HS1 = "Test Java regular expressions\n"+
		"2020 Manolis Christodoulou (mchris@mobi-doc.com)\n";
	
	static final String HS = YELLOW+"Syntax"+NOR+": retest options\n"+
		YELLOW+"options:"+NOR+
		"-l: (any character) operators don't match newlines\n"+
		"\t-i: ignore case\n"+
		"\t--version\n"+
		"\t--help\n";
	
	static final String HS2 = "Enter string/regexp to test\n"+
		"CTRL-D (Windows: CTRL-Z) to exit.\n";
	
	public static final String VERSION = "1.0";
	
	static private void help(int x) {
		System.out.println(HS0+HS1+HS);
		System.exit(x);
	}
		
	public static void main(String[] args) throws IOException {
		int flags=Pattern.DOTALL;
		for (var n : args) switch (n) {
			case "-i": flags |= Pattern.CASE_INSENSITIVE;
				break;
			case "-l": flags ^= Pattern.DOTALL;			
				break;
			case "--version": System.out.println(VERSION);
				System.exit(0);
			case "--help": help(0);
			default: help(1);
		}

// ReadLine Loading

		try {
			Readline.load(ReadlineLibrary.GnuReadline);
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Couldn't load GNU readline/BSD editline library.\nUsing simple stdin.\n");
		}
		Readline.initReadline("jretest");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> Readline.cleanup()));
		
// END of ReadLine Loading
                
		System.out.print(HS1+"\n"+HS2);
		String line;

		while(true) {
			try {
				line = Readline.readline(CYAN+"\ntest/regexp: "+NOR);
			} catch (EOFException e) {
				break;
			} catch (Exception e) {
				continue;
			}
			if (line == null) continue;

		var s = new Scanner(line).useDelimiter("/");
			String i1,i2;
			try {
				i1 = s.next();
				i2 = s.next();
				i2 += s.hasNextLine() ? s.nextLine() : "";
			} catch(Exception e) {
				continue;
			}
			
			Pattern p;
			try {
				p = Pattern.compile(i2, flags);
			} catch(Exception e) {
				System.out.println(RED+i2+": compiler error."+NOR);
				continue;
			}
			
			Matcher m = p.matcher(i1);
			if (m.find()) {
				System.out.println("/"+YELLOW+i1+NOR+"/"+YELLOW+i2+NOR+"/ "+GREEN+"MATCH!"+NOR);
				for (int n = 0; n <= m.groupCount(); n++) 
					if (m.start(n) == -1 || m.end(n) == -1) System.out.printf(GREEN+"Group %d: <"+RED+"EMPTY"+NOR+">\n",n);
					else System.out.printf(GREEN+"Group %d: "+NOR+"%s\n", n, i1.substring(0, m.start(n))+RED+i1.substring(m.start(n), m.end(n))+NOR+i1.substring(m.end(n), i1.length()));
			} else System.out.println("/"+YELLOW+i1+NOR+"/"+YELLOW+i2+NOR+"/ "+RED+"NO MATCH"+NOR);
		}
		Readline.cleanup();
		System.out.println("\n");
	}
}
