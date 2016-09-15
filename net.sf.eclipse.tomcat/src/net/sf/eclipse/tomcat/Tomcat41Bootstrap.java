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

/*
 * All Rights Reserved.
 */
import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Bootstrap specifics for Tomcat 4.1
 *
 * See %TOMCAT4_HOME%/bin/catalina.bat
 */
public class Tomcat41Bootstrap extends Tomcat4Bootstrap {

    Tomcat41Bootstrap(String label) {
		super(label);
	}


	@Override
    public String[] getVmArgs() {
        ArrayList<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Dcatalina.home=\"" + getTomcatDir() + "\"");

        String commonEndorsedDir = getTomcatDir() + File.separator + "common" + File.separator + "endorsed";
        vmArgs.add("-Djava.endorsed.dirs=\"" + commonEndorsedDir + "\"");

        String catalinaBase = getTomcatBase();
        if(catalinaBase.length() == 0) {
            catalinaBase = getTomcatDir();
        }

        vmArgs.add("-Dcatalina.base=\"" + catalinaBase + "\"");
        vmArgs.add("-Djava.io.tmpdir=\"" + catalinaBase + File.separator + "temp\"");

        if(TomcatLauncherPlugin.getDefault().isSecurityManagerEnabled()) {
            vmArgs.add("-Djava.security.manager");
            String securityPolicyFile = catalinaBase + File.separator + "conf" + File.separator + "catalina.policy";
            vmArgs.add("-Djava.security.policy=\"" + securityPolicyFile + "\"");
        }

        return (vmArgs.toArray(new String[0]));
    }


    @Override
    public IPath getJasperJarPath() {
        return new Path("common").append("lib").append("jasper-runtime.jar");
    }

}

