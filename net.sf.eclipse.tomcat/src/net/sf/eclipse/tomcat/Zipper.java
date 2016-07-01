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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Zipper {

	private static final int BUFFER = 2048;

    private static final long EMPTY_CRC = new CRC32 ().getValue ();
    protected String emptyBehavior = "skip";

    protected Hashtable addedDirs = new Hashtable();
	private File outputFile = null;
	private File directory = null;
	private ZipOutputStream zos = null;
	private final String currentDirName;

	public Zipper(File outputFile, File directory) throws IOException {
		this.outputFile = outputFile;
		this.directory = directory;
		currentDirName = directory.getAbsolutePath();
	}

	public void zip() throws IOException {
	    FileOutputStream fos = new FileOutputStream(outputFile);
		zos = new ZipOutputStream(fos);
		zipDir(directory);
		zos.flush();
		zos.close();
		fos.close();
	}


	private void zipDir(File dir) throws IOException {
		if(!dir.getPath().equals(currentDirName)) {
			String entryName = dir.getPath().substring(currentDirName.length()+1);
			entryName = entryName.replace('\\', '/');
	       	ZipEntry ze = new ZipEntry (entryName + "/");
	        if (dir.exists()) {
	            ze.setTime(dir.lastModified());
	        } else {
	            ze.setTime(System.currentTimeMillis());
	        }
	        ze.setSize (0);
	        ze.setMethod (ZipEntry.STORED);
	        // This is faintly ridiculous:
	        ze.setCrc (EMPTY_CRC);
            zos.putNextEntry (ze);
		}

		if (dir.exists() && dir.isDirectory()) {
			File [] fileList = dir.listFiles();

			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory() && this.acceptDir(fileList[i])) {
					zipDir(fileList[i]);
				}
				if (fileList[i].isFile() && this.acceptFile(fileList[i])) {
					zipFile(fileList[i]);
				}
			}
		}

	}


	private void zipFile(File file) throws IOException {
		if(!file.equals(this.outputFile)) {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file),BUFFER);

			String entryName = file.getPath().substring(currentDirName.length()+1);
			entryName = entryName.replace('\\', '/');
			ZipEntry fileEntry = new ZipEntry(entryName);
			zos.putNextEntry(fileEntry);

			byte[] data = new byte[BUFFER];
			int byteCount;
			while ((byteCount = bis.read(data, 0, BUFFER)) != -1) {
				zos.write(data, 0, byteCount);
			}

			bis.close();
		}
	}

	protected boolean acceptDir(File dir) {
		return true;
	}

	protected boolean acceptFile(File file) {
		return true;
	}

}
