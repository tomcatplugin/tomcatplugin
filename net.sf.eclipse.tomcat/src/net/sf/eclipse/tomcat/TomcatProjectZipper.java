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
 */package net.sf.eclipse.tomcat;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class TomcatProjectZipper extends Zipper {

	private boolean acceptSource = false;
	
	/**
	 * Constructor for TomcatProjectZipper.
	 *
	 * @param outputFile
	 * @param directory
	 * @throws IOException
	 */
	public TomcatProjectZipper(File outputFile, File directory, boolean acceptSource)
		throws IOException {
			super(outputFile, directory);
			this.acceptSource = acceptSource;
	}

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
