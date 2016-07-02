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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * container for managing a number of WebClassPathEntry objects
 * 
 * @version 	1.0
 * @author		Martin Kahr
 */
public class WebClassPathEntries {
	public static final String TAG_NAME = "webClassPathEntries";
	private static final String ENTRY_TAG_NAME = "webClassPathEntry";		
	private List entries;
	
	public WebClassPathEntries() {
		entries = new ArrayList();
	}
	public WebClassPathEntries(List values) {
		entries = values;
	}	
	
	/** returns the number of webclasspath-entries */
	public int size() {
		return entries.size();
	}
	
	/** return the WebClassPathEntry value at the index provided */
	public String getWebClassPathEntry(int index) {
		if (index >= entries.size()) return null;
		String entry = (String) entries.get(index);
		return entry;
	}
	
	/** add a WebClassPathEntry value */
	public void addWebClassPathEntry(String value) {
		if (entries.contains(value)) return;
		entries.add(value);
	}
	
	public List getList() { return entries; }

	/**
	 * transfer the state of this object to an XML string
	 */	
	public String xmlMarshal() {
		return xmlMarshal(0);
	}	

	public String xmlMarshal(int spacesToIntend) {
		String spaces = "";
		for(int i=0; i < spacesToIntend; i++) {
			spaces = spaces+" ";
		}
		String xml = spaces + startTag() + "\n";
		
		for (Iterator it = entries.iterator(); it.hasNext();) {
			String entry = (String) it.next();
			xml += spaces + spaces + startEntryTag() + entry + endEntryTag() + "\n";			
		}		
		
		xml += spaces + endTag() + "\n";
		return xml;
	}
	
	/** 
	 * instantiate a WebClassPathEntries object and intialize
	 * it with the xml data provided
	 *
	 * @return the object if unmarshaling had no errors. returns null
	 *          if the marshaling was unsuccessfully.
	 */
	public static WebClassPathEntries xmlUnmarshal(String xmlString) {
		if (xmlString == null || xmlString.trim().length() == 0) {
			return null;
		}
		int start = xmlString.indexOf(startTag());
		int end   = xmlString.indexOf(endTag());
		if (start < 0 || end <= start) return null;
		
		String value = xmlString.substring(start+startTag().length(), end);
		
		value = value.trim();
		
		WebClassPathEntries webEntries = new WebClassPathEntries();
		while(value != null && value.length() > 0) {
			start = value.indexOf(startEntryTag());
			end   = value.indexOf(endEntryTag());
			if (start >= 0 || end > start) {
				String entryValue = value.substring(start+startEntryTag().length(), end);
				if (entryValue.trim().length() > 0) {
					webEntries.addWebClassPathEntry(entryValue);
				}
				value = value.substring(end + endEntryTag().length());			
			} else {
				value = null;
			}
		}		
		
		return webEntries;
	}
	
	private static String startTag() { return "<" + TAG_NAME + ">"; }
	private static String endTag() { return "</" + TAG_NAME + ">"; }	
	private static String startEntryTag() { return "<" + ENTRY_TAG_NAME + ">"; }
	private static String endEntryTag() { return "</" + ENTRY_TAG_NAME + ">"; }		
}
