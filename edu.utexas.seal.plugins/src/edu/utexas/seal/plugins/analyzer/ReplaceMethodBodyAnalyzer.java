package edu.utexas.seal.plugins.analyzer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.visitor.ReplaceMethodBodyVisitor;


/**
 * @since J2SE-1.8
 */
public class ReplaceMethodBodyAnalyzer {
	private String targetProjectName;
	private String targetPackageName;
	private String targetClassName;
	private String targetMethodName;
	private String updatedMethodBodyStms;	

	public ReplaceMethodBodyAnalyzer(String targetProjectName, String targetPackageName, String targetClassName, 
			String targetMethodName, String updatedMethodBodyStms) {
		this.targetProjectName = targetProjectName;
		this.targetPackageName = targetPackageName;		
		this.targetClassName = targetClassName;
		this.targetMethodName = targetMethodName;
		this.updatedMethodBodyStms = updatedMethodBodyStms;

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			try {
				analyzeJavaProject(project);
			} catch (MalformedTreeException | BadLocationException | CoreException e) {
				e.printStackTrace();
			}
		}
	}

	void analyzeJavaProject(IProject project)
			throws CoreException, JavaModelException, MalformedTreeException, BadLocationException {
		if (!project.isOpen() || !project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			return;
		}
		IJavaProject javaProject = JavaCore.create(project);
		if (!javaProject.getElementName().equals(targetProjectName)) {
			return;
		}
		System.out.println("[DBG ReplaceMethodBodyAnalyzer] Found Project: " + javaProject.getElementName());
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment iPackage : packages) {
			if (iPackage.getKind() == IPackageFragmentRoot.K_SOURCE && //
					iPackage.getCompilationUnits().length >= 1 && //
					iPackage.getElementName().equals(targetPackageName)) {
				System.out.println("[DBG ReplaceMethodBodyAnalyzer] Found Package: " + iPackage.getElementName());
				replaceMethodBody(iPackage);
			}
		}
	}

	void replaceMethodBody(IPackageFragment iPackage)
			throws JavaModelException, MalformedTreeException, BadLocationException {
		//System.out.println("[DBG ReplaceMethodBodyAnalyzer] Target Class Name: " + targetClassName);
		for (ICompilationUnit iCUnit : iPackage.getCompilationUnits()) {
			String nameICUnit = UTStr.getClassNameFromJavaFile(iCUnit.getElementName());
			//System.out.println("[DBG ReplaceMethodBodyAnalyzer] Check Class Name: " + nameICUnit);
			if (nameICUnit.equals(targetClassName) == false) {
				continue;
			}
			System.out.println("[DBG ReplaceMethodBodyAnalyzer] Found Class: " + nameICUnit);
			ICompilationUnit workingCopy = iCUnit.getWorkingCopy(null);
			UTASTParser astParser = new UTASTParser();
			CompilationUnit cUnit = astParser.parse(workingCopy);
			ASTRewrite rewrite = ASTRewrite.create(cUnit.getAST());
			ReplaceMethodBodyVisitor v = new ReplaceMethodBodyVisitor(targetMethodName, updatedMethodBodyStms);
			v.setASTRewrite(rewrite);
			cUnit.accept(v);
			TextEdit edits = null;
			edits = rewrite.rewriteAST(); // Compute the edits
			workingCopy.applyTextEdit(edits, null); // Apply the edits.
			workingCopy.commitWorkingCopy(false, null); // Save the changes.
		}
	}
}
