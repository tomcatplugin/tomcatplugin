package com.sysdeo.eclipse.tomcat;

/*
 * (c) Copyright Sysdeo SA 2001, 2002.
 * All Rights Reserved.
 */
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
//		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
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
	 * Copie un fichier vers un autre fichier ou un répertoire vers un autre répertoire
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
	 * Copie un répertoire dans un autre
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

	/**
	 * 
	 */
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
