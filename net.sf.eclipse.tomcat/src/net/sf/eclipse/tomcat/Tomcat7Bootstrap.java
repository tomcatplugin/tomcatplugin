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
import java.util.ArrayList;

/**
 * Bootstrap specifics for Tomcat 7
 */
public class Tomcat7Bootstrap extends Tomcat6Bootstrap {

    @Override
    public String[] getClasspath() {
        ArrayList<String> classpath = new ArrayList<String>();
        classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "bootstrap.jar");
        classpath.add(getTomcatDir() + File.separator + "bin" + File.separator + "tomcat-juli.jar");

        // Add tools.jar JDK file to classpath
        String toolsJarLocation = VMLauncherUtility.getVMInstall().getInstallLocation() + File.separator + "lib" + File.separator + "tools.jar";
        if(new File(toolsJarLocation).exists()) {
            classpath.add(toolsJarLocation);
        }
        return (classpath.toArray(new String[0]));
    }

    @Override
    public String getLabel() {
        return "Tomcat 7.x";
    }
}
