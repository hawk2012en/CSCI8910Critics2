/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
/*
 * @(#) UTPlugin.java
 *
 */
package edu.utexas.seal.plugins.util.root;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.compare.internal.CompareUIPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import ut.seal.plugins.utils.UTLog;

/**
 * @author Myoungkyu Song
 * @date Oct 18, 2013
 * @since JDK1.7
 */
public class UTPlugin {
	private static final String	PLUGIN_ID						= "org.eclipse.compare";
	private static final String	FS								= System.getProperty("file.separator");

	public static final String	IMG_ADD							= "icons" + FS + "add.gif";
	public static final String	IMG_DEL							= "icons" + FS + "delete.gif";
	public static final String	IMG_CHK							= "icons" + FS + "checking.gif";
	public static final String	IMG_JFILE						= "icons" + FS + "javafile.gif";
	public static final String	IMG_METHOD						= "icons" + FS + "method.png";
	public static final String	IMG_BLOCK						= "icons" + FS + "block.png";
	public static final String	ID_CMD_CRITICS_FIND_CONTEXT		= "edu.utexas.seal.plugins.handler.CriticsFindContext";
	public static final String	ID_CMD_CRITICS_SELECT_CONTEXT	= "edu.utexas.seal.plugins.handler.CriticsSelectContext";
	public static final String	ID_VIEW_SELECTCTX_OLDREV		= "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.OldRev";
	public static final String	ID_VIEW_SELECTCTX_NEWREV		= "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.NewRev";
	public static final String	ID_VIEW_OVERLAY					= "edu.utexas.seal.plugins.overlay.view.CriticsOverlayView";
	public static final String	ID_VIEW_NEW_OVERLAY				= "edu.utexas.seal.plugins.overlay.view.CriticsOverlayNewView";
	public static final String	ID_TEXT_SELECTCON				= "selectContext";
	public static final String	ITEM_TEXT_SELECTCON				= "Select Diff Region";

	/**
	 * 
	 */
	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	/**
	 * 
	 */
	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	/**
	 * @param aRelPath
	 */
	public static String getFilePath(String aRelPath) {
		String strAbsPath = null;
		try {
			Bundle bundle = CompareUIPlugin.getDefault().getBundle();
			URL url = FileLocator.find(bundle, new Path(aRelPath), null);
			strAbsPath = FileLocator.resolve(url).getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strAbsPath;
	}

	public static File getFullFilePath(ICompilationUnit unit) {
		String loc = UTCriticsPairFileInfo.getRightIProject().getLocation().toFile().getAbsolutePath();
		String nm = UTCriticsPairFileInfo.getRightIProject().getName();
		String S = System.getProperty("file.separator");
		String workingDir = loc.replace(S + nm, "");
		//String fileName = unit.getPath().toFile().getAbsolutePath();
		String fileName = unit.getPath().toFile().getPath();
		String fullFileName = workingDir + fileName;
		File fullFilePath = new File(fullFileName);
		if (!fullFilePath.exists()) {
			UTLog.println(true, "[WRN] " + fullFileName);
		}
		return fullFilePath;
	}

	/**
	 * Gets the ICompilationUnit.
	 * 
	 * @param aFilePath the a file path
	 * @return the ICompilationUnit
	 */
	public static ICompilationUnit getICompilationUnit(File aFilePath) {
		try {
			IJavaProject javaProject = (IJavaProject) UTCriticsPairFileInfo.getLeftIProject();
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						File f = unit.getPath().toFile();
						if (f.equals(aFilePath)) {
							return unit;
						}
					}
				}
			}
			javaProject = (IJavaProject) UTCriticsPairFileInfo.getRightIProject();
			packages = javaProject.getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						File f = unit.getPath().toFile();
						if (f.equals(aFilePath)) {
							return unit;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param aPath
	 */
	public static Image getImage(String aPath) {
		return CompareUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, aPath).createImage();
	}

	/**
	 * @param aViewID
	 */
	public static IViewPart getView(String aViewID) {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return page.findView(aViewID);
	}
}
