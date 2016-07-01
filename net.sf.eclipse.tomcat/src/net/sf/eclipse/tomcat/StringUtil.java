/* The MIT License
 * (c) Copyright Sysdeo SA 2001-2002
 * (c) Copyright Eclipse Tomcat Plugin 2014-2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sf.eclipse.tomcat;
 
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class StringUtil {

	/**
	 * Concat two arrays of Strings,
	 * part2 is appended to part1
	 */
	public static String[] concat(String[] part1, String[] part2) {
		String[] full = new String[part1.length + part2.length];
		System.arraycopy(part1, 0, full, 0, part1.length);
		System.arraycopy(part2, 0, full, part1.length, part2.length);
		return full;	
	}

	/**
	 * Concat two arrays of Strings, and prevent that duplicate
	 * Strings are present in result
	 * part2 is appended to part1
	 */
	public static String[] concatUniq(String[] part1, String[] part2) {
		ArrayList nlist = new ArrayList();

		for (int i = 0; i < part1.length; i++) {
			if (! nlist.contains(part1[i]))
				nlist.add(part1[i]);
		}
        
		for (int i = 0; i < part2.length; i++) {
			if (! nlist.contains(part2[i]))
				nlist.add(part2[i]);
		}
        
		return (String[])nlist.toArray(new String[0]);      
	}


	/**
	 * See StringTokenizer for delim parameter format 
	 */
	public static String[] cutString(String str, String delim) {
		ArrayList strings = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(str, delim);
		while (tokenizer.hasMoreTokens()) {
			strings.add( URLDecoder.decode( tokenizer.nextToken()) );	
		}
		
		return (String[])strings.toArray(new String[0]);
	}

}
