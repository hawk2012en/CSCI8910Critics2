package ut.seal.plugins.utils.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class DeclarationVisitor extends ASTVisitor {
	
	private String pkgName;
	private String className;
	private String methodName;
	private String targetMethodName;
	
	public DeclarationVisitor(String targetMethodName) {
		this.targetMethodName = targetMethodName;
	}

	@Override
	public boolean visit(PackageDeclaration pkgDecl) {
		pkgName = pkgDecl.getName().getFullyQualifiedName();
		//System.out.println("pkgName: " + pkgName);
		return super.visit(pkgDecl);
	}
	
	@Override
	public boolean visit(TypeDeclaration typeDecl) {
		className = typeDecl.getName().getIdentifier();
		//System.out.println("className: " + className);
		return super.visit(typeDecl);
	}
	
	@Override
	public boolean visit(MethodDeclaration methodDecl) {

		methodName = methodDecl.getName().getIdentifier();
		if (methodName.equals(targetMethodName)) {			
			int parmSize = methodDecl.parameters().size();
			int startPos = methodDecl.getStartPosition();
			System.out.println("pkgName: " + pkgName);
			System.out.println("className: " + className);
			System.out.println("methodName: " + methodName);
			System.out.println("parmSize: " + parmSize);
			System.out.println("startPos: " + startPos);		
		}		
		
		return super.visit(methodDecl);
	}
	
//	@Override
//	public boolean visit(MethodInvocation methodInvocation) {
//		if (methodName.equals(targetMethodName)) {
//			System.out.println("methodInvocation: " + methodInvocation.toString());
//			System.out.println("methodInvocation Simple Name: " + methodInvocation.getName().getIdentifier());			
//			System.out.println("Node Type int value: " + methodInvocation.getNodeType());
//			System.out.println("ASTNode.METHOD_INVOCATION int value: " + ASTNode.METHOD_INVOCATION);
//			System.out.println("methodInvocation Start Position: " + methodInvocation.getStartPosition());
//			System.out.println("methodInvocation Length: " + methodInvocation.getLength());
//			System.out.println("methodInvocation Expression: " + methodInvocation.getExpression());
//			System.out.println("methodInvocation Parent: " + methodInvocation.getParent().toString().trim());
//			System.out.println("methodInvocation Location In Parent: " + methodInvocation.getLocationInParent().toString());						
//			System.out.println();
//		}
//		return super.visit(methodInvocation);
//	}
	
}
