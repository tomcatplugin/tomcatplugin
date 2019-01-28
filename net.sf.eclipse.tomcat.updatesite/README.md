#Release Notes#

* Release Notes starting with version 3.3.2
* This project is a fork of the Tomcat-Plugin from Eclipse Totale


##Version 9.1.4##
* Bugfix: Persisting root and work directory works now for "Tomcat projects"
* Bugfix: When a project is added to Tomcat's system classpath, add Tomcat's libraries to the system classpath, too.

##Version 9.1.3##
* Signed jars

##Version 9.1.2##
* Bugfix: Github issue https://github.com/his-eg/tomcatplugin/issues/5

##Version 9.1.1##
* Bugfix: [#16] Debug View shows "Tomcat 7.x" for Tomcat 8.x and 9.x
* Bugfix: [#17] DevLoader does not work on Tomcat 8.5.4
* Bugfix: [#18] Update Context fails with error if optional work dir is not specified
* Minor changes: Remove some minor compiler warnings

##Version 9.1.0##
* Bugfix: fix compatibility issue with Neo [#14]
* Feature: Add support for Maven classpath management

##Version 9.0.1##
* Bugfix: fix compatibility issue with Neo [#11]

##Version 9.0.0##
* Feature: Add support for Tomcat 9 [#9]

##Version 3.3.6.1##
* Bugfix: Signing artifacts responsible for [#8] fixed

##Version 3.3.6##
* Bugfix: Buttons were gone missing [#8]
* Bugfix: Dependencies for Neon updated [#7]

##Version 3.3.5.1##
* Signed the Plugin Jars

##Version 3.3.5##
* Bugfix: Tomcat Context menu was gone missing due to nature renaming [#2]
* Implement configuration parameter for work dir of tomcat projects [#4]

##Version 3.3.4##

* Added visible Tomcat 8 support
* Preference variables are resolved for validation to prevent false error messages in preference page

##Version 3.3.3##

* Information about available variables in preferences dialogue

##Version 0.1.0.2014-08-11##

* Added variable support in preferences
* initial release on sf.net
