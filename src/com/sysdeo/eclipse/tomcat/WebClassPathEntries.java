package com.sysdeo.eclipse.tomcat;

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
	
	
	/** 
	 * main method yust for some simple tests - should be in a Junit Testclass
	 * but I don't want to add the junit reference to this project
	 */
	public static void main(String[] arguments) {
		String xml = "";
		WebClassPathEntries entries = xmlUnmarshal(xml);
		
		if (entries != null) {
			System.err.println("invalid xml must result in null object !");
			System.exit(1);
		}
		
		xml = "<webClassPathEntries></webClassPathEntries>";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			System.err.println("valid xml must result in an object !");
			System.exit(1);			
		}
		if (entries.size() != 0) {
			System.err.println("expected size 0 but was " + entries.size());
			System.exit(1);						
		}
		xml = "<root><webClassPathEntries>\n</webClassPathEntries>\n</root>";		
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			System.err.println("valid xml must result in an object !");
			System.exit(1);			
		}
		if (entries.size() != 0) {
			System.err.println("expected size 0 but was " + entries.size());
			System.exit(1);						
		}
		
		xml = "<webClassPathEntries><webClassPathEntry>abc</webClassPathEntry></webClassPathEntries>";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			System.err.println("valid xml must result in an object !");
			System.exit(1);			
		}
		if (entries.size() != 1) {
			System.err.println("expected size 1 but was " + entries.size());
			System.exit(1);						
		}		
		if (!entries.getWebClassPathEntry(0).equals("abc")) {
			System.err.println("expected 'abc' but was '" + entries.getWebClassPathEntry(0) + "'");
			System.exit(1);						
		}				
		
		xml = "<webClassPathEntries>\n<webClassPathEntry>abc</webClassPathEntry>\n<webClassPathEntry>def</webClassPathEntry>\n<webClassPathEntry>123</webClassPathEntry>\nxxxxx</webClassPathEntries>\n";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			System.err.println("valid xml must result in an object !");
			System.exit(1);			
		}
		if (entries.size() != 3) {
			System.err.println("expected size 1 but was " + entries.size());
			System.exit(1);						
		}		
		if (!entries.getWebClassPathEntry(0).equals("abc")) {
			System.err.println("expected 'abc' but was '" + entries.getWebClassPathEntry(0) + "'");
			System.exit(1);						
		}				
		if (!entries.getWebClassPathEntry(1).equals("def")) {
			System.err.println("expected 'def' but was '" + entries.getWebClassPathEntry(1) + "'");
			System.exit(1);						
		}				
		if (!entries.getWebClassPathEntry(2).equals("123")) {
			System.err.println("expected '123' but was '" + entries.getWebClassPathEntry(2) + "'");
			System.exit(1);						
		}
		
		xml = "<webClassPathEntries>\n<webClassPathEntry>abc</webClassPathEntry>\n<webClassPathEntry>def</webClassPathEntry>\n<webClassPathEntry>123</webClassPathEntry>\n</webClassPathEntries>\n";
		String gen = entries.xmlMarshal();
		if (gen.equals(xml) == false) {
			System.err.println("generated xml is incorrect:\n!" + gen + "!");
			System.err.println("expected xml is :\n!" + xml + "!");			
			System.exit(1);									
		}
		
		System.out.println("All okay !");
	}
}
