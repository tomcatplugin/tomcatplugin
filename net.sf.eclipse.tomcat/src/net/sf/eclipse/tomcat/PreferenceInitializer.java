package net.sf.eclipse.tomcat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore prefStore = TomcatLauncherPlugin.getDefault().getPreferenceStore();
		prefStore.setDefault(TomcatLauncherPlugin.TOMCAT_PREF_CONFMODE_KEY, TomcatLauncherPlugin.CONTEXTFILES_MODE);		
	}

}
