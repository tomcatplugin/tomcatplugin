package org.apache.catalina.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.util.ServerInfo;

/**
 * @author Martin Kahr
 *
 */
public class DevLoader extends WebappLoader {
	private static final String info =
        "org.apache.catalina.loader.DevLoader/1.0"; 

	private String webClassPathFile = ".#webclasspath";
	private String tomcatPluginFile = ".tomcatplugin";
	
	public DevLoader() {
		super();		
	}		
	public DevLoader(ClassLoader parent) {
		super(parent);
	}
	
	/**
	 * @see org.apache.catalina.Lifecycle#start()
	 */
	public void startInternal() throws LifecycleException {
	// e.siffert: start() is final in LifecycleBase
	//public void start() throws LifecycleException {
		log("Starting DevLoader modified by e.siffert (August 04 2014) for Tomcat 8: " + ServerInfo.getServerInfo());
		//setLoaderClass(DevWebappClassLoader.class.getName());
		
//		super.start();
		super.startInternal();
		
		ClassLoader cl = super.getClassLoader();
		if (cl instanceof WebappClassLoader == false) {
			logError("Unable to install WebappClassLoader, received ClassLoader was null !");
			return;
		}
		WebappClassLoader devCl = (WebappClassLoader) cl;
		
		List webClassPathEntries = readWebClassPathEntries();
		StringBuilder classpath   = new StringBuilder();
		for (Iterator it = webClassPathEntries.iterator(); it.hasNext();) {
			String entry = (String) it.next();
			File f = new File(entry);
			if (f.exists()) {
				if (f.isDirectory() && entry.endsWith("/")==false) f = new File(entry + "/");
				try {
					URL url = f.toURL();
					//devCl.addUrl(url);
					// removed in Tomcat8
					//	devCl.addRepository(url.toString());
					devCl.addURL(url);
					classpath.append(f.toString()).append(File.pathSeparatorChar);
					log("added to classpath: " + url.toString());
				} catch (MalformedURLException e) {
					logError(entry + " invalid (MalformedURL)");
				}
			} else {
				logError(entry + " does not exist !");
			}
		}

		String cp = (String)getServletContext().getAttribute(Globals.CLASS_PATH_ATTR);
		StringTokenizer tokenizer = new StringTokenizer(cp, File.pathSeparatorChar + "");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			// only on windows 
			if (token.charAt(0)=='/' && token.charAt(2)==':') {
				token = token.substring(1);
			}
			classpath.append(token).append(File.pathSeparatorChar);
		}
		//cp = classpath + cp;
		getServletContext().setAttribute(Globals.CLASS_PATH_ATTR, classpath.toString());
		log("class path for our application class loader = " + classpath);
	}
	
	protected void log(String msg) {
		System.out.println("[DevLoader] " + msg);
	}
	protected void logError(String msg) {
		System.err.println("[DevLoader] Error: " + msg);
	}
	
	protected List readWebClassPathEntries() {
		List rc = null;
				
		File prjDir = getProjectRootDir();
		if (prjDir == null) {
			return new ArrayList();
		}
		log("projectdir=" + prjDir.getAbsolutePath());
		
		// try loading tomcat plugin file
		// DON'T LOAD TOMCAT PLUGIN FILE (DOESN't HAVE FULL PATHS ANYMORE)
		//rc = loadTomcatPluginFile(prjDir);
		
		if (rc ==null) {
			rc = loadWebClassPathFile(prjDir);
		}
		
		if (rc == null) rc = new ArrayList(); // should not happen !
		return rc;
	}
	
	protected File getProjectRootDir() {
		File rootDir = getWebappDir();
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return (file.getName().equalsIgnoreCase(webClassPathFile) ||
				        file.getName().equalsIgnoreCase(tomcatPluginFile));
			}
		};
		while(rootDir != null) {
			File[] files = rootDir.listFiles(filter);
			if (files != null && files.length >= 1) {
				return files[0].getParentFile();
			}
			rootDir = rootDir.getParentFile();
		}
		return null;
	}
	
	protected List loadWebClassPathFile(File prjDir) {
		File cpFile = new File(prjDir, webClassPathFile);
		if (cpFile.exists()) {			
			FileReader reader = null;
			try {
				List rc = new ArrayList();
				reader = new FileReader(cpFile);
				LineNumberReader lr = new LineNumberReader(reader);
				String line = null;
				while((line = lr.readLine()) != null) {
					// convert '\' to '/'
					line = line.replace('\\', '/');
					rc.add(line);
				}
				return rc;
			} catch(IOException ioEx) {
				if (reader != null) try { reader.close(); } catch(Exception ignored) {}
				return null;
			}			
		} else {
			return null;
		}
	}
	
/*
	protected List loadTomcatPluginFile(File prjDir) {
		File cpFile = new File(prjDir, tomcatPluginFile);
		if (cpFile.exists()) {			
			FileReader reader = null;
			try {
				StringBuffer buf = new StringBuffer();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(cpFile)));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					buf.append(inputLine);
					buf.append('\n');
				}
				WebClassPathEntries entries = WebClassPathEntries.xmlUnmarshal(buf.toString());
				if (entries == null) {
					log("no entries found in tomcatplugin file !");
					return null;
				}
				return entries.getList();
			} catch(IOException ioEx) {
				if (reader != null) try { reader.close(); } catch(Exception ignored) {}
				return null;				
			}
		} else {
			return null;			
		}
	}
*/	
	protected ServletContext getServletContext() {
		//return ((Context) getContainer()).getServletContext();
		return getContext().getServletContext();
	}
	
	protected File getWebappDir() {		
		File webAppDir = new File(getServletContext().getRealPath("/"));
		return webAppDir;
	}
}
