package com.sysdeo.eclipse.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class TomcatProjectZipper extends Zipper {

	private boolean acceptSource = false;
	
	/**
	 * Constructor for TomcatProjectZipper.
	 * @param outputFile
	 * @param directory
	 * @throws IOException
	 */
	public TomcatProjectZipper(File outputFile, File directory, boolean acceptSource)
		throws IOException {
			super(outputFile, directory);
			this.acceptSource = acceptSource;
	}

	/*
	 * @see Zipper#acceptDir(File)
	 */
	protected boolean acceptDir(File dir) {

		if (dir.getName().equals("jsp")) {
			if(dir.getParentFile().getName().equals("apache")) {
				if(dir.getParentFile().getParentFile().getName().equals("org")) {
					return false;
				}
			}
		}

		
		String excludeString = TomcatPluginResources.PROJECT_WAREXPORT_EXCLUDE_DIRECTORIES;
		StringTokenizer tokenizer = new StringTokenizer(excludeString, ";");
		while (tokenizer.hasMoreTokens()) {
			String eachDir = tokenizer.nextToken();
			if (dir.getName().equals(eachDir)) {
				return false;	
			}
		}
			
		return true;
	}

	/*
	 * @see Zipper#acceptFile(File)
	 */
	protected boolean acceptFile(File file) {
				
		if (file.getName().endsWith(".java")) {
			return acceptSource;	
		}
		
		String excludeString = TomcatPluginResources.PROJECT_WAREXPORT_EXCLUDE_FILES;
		StringTokenizer tokenizer = new StringTokenizer(excludeString, ";");
		while (tokenizer.hasMoreTokens()) {
			if (file.getName().equals(tokenizer.nextToken())) {
				return false;	
			}
		}
						
		return true;
	}

}
