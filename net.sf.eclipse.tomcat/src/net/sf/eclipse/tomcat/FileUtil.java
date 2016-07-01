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
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class for Files operation
 * 
 * Use UTF-8 encoding for text file.
 */
public class FileUtil {

	public static String readTextFile(File f) throws IOException {
		
		StringBuffer buf = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			buf.append(inputLine);
			buf.append('\n');
		}

		in.close();
		return buf.toString();
	}
	
	public static void toTextFile(File f, String content) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		out.write(content.getBytes("UTF-8"));
		out.close(); 		
	}

	public static void copy(String inputFilename, String outputFilename) throws IOException {
		FileUtil.copy(new File(inputFilename), new File(outputFilename));
	}

	/**
	 * Copie un fichier vers un autre fichier ou un r�pertoire vers un autre r�pertoire
	 */
	public static void copy(File input, File output) throws IOException {
		if(input.isDirectory() && output.isDirectory()) {
			FileUtil.copyDir(input, output);	
		} else {
			FileUtil.copyFile(input, output);	
		}
	}
	
	/**
	 * Copie un fichier vers un autre
	 */	
	public static void copyFile(File inputFile, File outputFile) throws IOException {
		BufferedInputStream fr = new BufferedInputStream(new FileInputStream(inputFile));
		BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(outputFile));
		byte[] buf = new byte[8192];
		int n;
		while((n = fr.read(buf)) >= 0)
			fw.write(buf,0,n);
		fr.close();
		fw.close();
    }

	/**
	 * Copie un rpertoire dans un autre
	 */    
    public static void copyDir(File inputDir, File outputDir) throws IOException {
     	File[] files = inputDir.listFiles();
    	for(int i=0; i<files.length; i++) {
    		File destFile = new File(outputDir.getAbsolutePath() + File.separator + files[i].getName());
    		if(!destFile.exists()) {
				if(files[i].isDirectory()) {
		    		destFile.mkdir();
				}
    		}
    		FileUtil.copy(files[i], destFile);	
    	}
    }
    

	/**
	 * return true if the directory contains files with the extension
	 */
	public static boolean dirContainsFiles(File dir, String extension, boolean recursive) {	
		File[] files = dir.listFiles();
		for(int i=0; i<files.length; i++) {
			if(files[i].isFile() && files[i].getName().endsWith(extension))
				return true;
			if(recursive && files[i].isDirectory())
				return FileUtil.dirContainsFiles(files[i], extension, recursive);
		}
		
		return false;	
	}

	public static String readPropertyInXMLFile(File file, String property) throws IOException {
		String content = FileUtil.readTextFile(file);
		int startTagIdx = content.indexOf("<" + property + ">");
		int endTagIdx = content.indexOf("</" + property + ">");
		if (startTagIdx == -1)
			throw new IOException("Property " + property + " not found in file " + file);
			
		return content.substring(startTagIdx + property.length() + 2, endTagIdx);	
	}
	
	/**
	 * Recursive delete of a directory.<br>
	 * The directory itself will be deleted
	 */
	public static void removeDir(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				FileUtil.removeDir(files[i]);
			} else {
				files[i].delete();	
			}
		}
		dir.delete();
	}

}
