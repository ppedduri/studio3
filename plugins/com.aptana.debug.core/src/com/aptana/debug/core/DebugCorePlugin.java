/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.debug.core.internal.UniformResourceBreakpointChangeNotifier;
import com.aptana.debug.core.sourcelookup.RemoteSourceCacheManager;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Max Stepanov
 */
public class DebugCorePlugin extends Plugin {
	/**
	 * ID
	 */
	public static final String PLUGIN_ID = "com.aptana.debug.core"; //$NON-NLS-1$

	// The shared instance.
	private static DebugCorePlugin plugin;

	private UniformResourceBreakpointChangeNotifier breakpointHelper;
	private RemoteSourceCacheManager remoteSourceCacheManager;

	/**
	 * The constructor.
	 */
	public DebugCorePlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		breakpointHelper = new UniformResourceBreakpointChangeNotifier();
		DebugPlugin.getDefault().addDebugEventListener(remoteSourceCacheManager = new RemoteSourceCacheManager());
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		breakpointHelper.cleanup();
		DebugPlugin.getDefault().removeDebugEventListener(remoteSourceCacheManager);
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return DebugCorePlugin
	 */
	public static DebugCorePlugin getDefault() {
		return plugin;
	}
	
	public RemoteSourceCacheManager getRemoteSourceCacheManager() {
		return remoteSourceCacheManager;
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg) {
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Forces to open source element in default editor
	 * 
	 * @param sourceElement
	 */
	public static void openInEditor(Object sourceElement) {
		IEditorOpenAdapter adapter = (IEditorOpenAdapter) getDefault().getContributedAdapter(IEditorOpenAdapter.class);
		if (adapter != null) {
			adapter.openInEditor(sourceElement);
		}
	}

	private Object getContributedAdapter(Class<?> clazz) {
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName())) {
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null) {
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}

}
