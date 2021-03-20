package ut.seal.plugins.utils.visitor;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

public class ReplaceMethodBodyVisitor extends ASTVisitor {
	

	private String targetMethodName;
	private String updatedMethodBodyStms;
	private ASTRewrite rewrite;
	
	public ReplaceMethodBodyVisitor(String targetMethodName, String updatedMethodBodyStms) {
		this.targetMethodName = targetMethodName;
		this.updatedMethodBodyStms = updatedMethodBodyStms;
	}
	
	@Override
	public boolean visit(MethodDeclaration methodDecl) {

		String methodName = methodDecl.getName().getIdentifier();
		if (methodName.equals(targetMethodName)) {	
			System.out.println("[DBG ReplaceMethodBodyVisitor] Found method to be replaced: " + methodName);
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setSource(updatedMethodBodyStms.toCharArray());
			parser.setKind(ASTParser.K_STATEMENTS);
			Block newBlock = (Block) parser.createAST(null);
			Block oldBlock = methodDecl.getBody();
			rewrite.replace(oldBlock, newBlock, null);
		}		
		
		return super.visit(methodDecl);
	}	

	public void setASTRewrite(ASTRewrite rewrite) {
		this.rewrite = rewrite;
	}
		
}
