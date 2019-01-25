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

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Bootstrap specifics for Tomcat 3
 *
 * See %TOMCAT3_HOME%/bin/tomcat.bat
 */
public class Tomcat3Bootstrap extends TomcatBootstrap {

    Tomcat3Bootstrap(String label) {
		super(label);
	}

    @Override
    public String[] getClasspath() {
        String[] classpath = new String[1];

        String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
        classpath[0] = toolsJarLocation;

        File libDir = new File(getTomcatDir() + File.separator + "lib");
        classpath = this.addJarsOfDirectory(classpath, libDir);

        File containerDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "container");
        classpath = this.addJarsOfDirectory(classpath, containerDir);

        File commonDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "common");
        classpath = this.addJarsOfDirectory(classpath, commonDir);

        File appsDir = new File(getTomcatDir() + File.separator + "lib" + File.separator + "apps");
        classpath = this.addJarsOfDirectory(classpath, appsDir);

        return classpath;
    }

    @Override
    public String getMainClass() {
        return "org.apache.tomcat.startup.Main";
    }

    @Override
    public String getStartCommand() {
        return "start";
    }

    @Override
    public String getStopCommand() {
        return "stop";
    }

    @Override
    public String[] getPrgArgs(String command) {
        String[] prgArgs = null;
        if(command.equals(getStartCommand())) {
            prgArgs = new String[3];
            prgArgs[0] = command;
            prgArgs[1] = "-config";
            prgArgs[2] = TomcatLauncherPlugin.getDefault().getConfigFile();
        } else {
            prgArgs = new String[1];
            prgArgs[0] = command;
        }
        return prgArgs;
    }

    @Override
    public String[] getVmArgs() {
        String[] vmArgs = new String[1];
        vmArgs[0] = "-Dtomcat.home=" + getTomcatDir();

        return vmArgs;
    }

    @Override
    public String getXMLTagAfterContextDefinition() {
        return "</ContextManager>";
    }

    @Override
    public IPath getServletJarPath() {
        return new Path("lib").append("common").append("servlet.jar");
    }

    @Override
    public IPath getJasperJarPath() {
        return new Path("lib").append("common").append("jasper-runtime.jar");
    }

    @Override
    public String getContextWorkDir(String workFolder) {
        StringBuffer workDir = new StringBuffer("workDir=");
        workDir.append('"');
        workDir.append(workFolder);
        workDir.append('"');
        return workDir.toString();
    }

    /*
     * No JSP jar for Tomcat 3, JSP classes are in servlet jar
     */
    @Override
    public IPath getJSPJarPath() {
        return null;
    }

}

