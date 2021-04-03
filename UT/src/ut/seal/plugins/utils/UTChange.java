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
package ut.seal.plugins.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;

import ut.seal.plugins.utils.UTCriticsDiffUtil.Diff;
import ut.seal.plugins.utils.UTCriticsDiffUtil.Operation;
import ut.seal.plugins.utils.ast.UTASTNodeConverter;
import ut.seal.plugins.utils.ast.UTASTNodeFinder;
import ut.seal.plugins.utils.ast.UTASTParser;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.util.visitor.UTGeneralVisitor;
import edu.utexas.seal.plugins.util.visitor.UTIGeneralVisitor;

/**
 * The Class UTChange.
 */
public class UTChange {

	public UTChange() {
	}

	@Test
	public void testCheckEQByDiff() {
		// SourceCodeEntity entity = new SourceCodeEntity("", JavaEntityType.ASSIGNMENT, new SourceRange(1, 2));
		// if (qNode.toString().contains("(value: pizza = new ClamPizza($var);)(label: ASSIGNMENT)") && //
		// tNode.toString().contains("(value: pizza = new ClamPizza(californiaFactory);)(label: ASSIGNMENT)")) {
		// System.out.print("");
		// }
		Node qNode = new Node(JavaEntityType.ASSIGNMENT, "$var = new ClamPizza($var);");
		Node tNode = new Node(JavaEntityType.ASSIGNMENT, "pizza = new ClamPizza(californiaFactory);");
		boolean checkEQByDiff = UTChange.checkEQByDiff(qNode, tNode);
		System.out.println("[DBG] " + checkEQByDiff);

		qNode = new Node(JavaEntityType.ASSIGNMENT, "$var = new ClamPizza($var);");
		tNode = new Node(JavaEntityType.ASSIGNMENT, "pizza = new ClamPizza2(californiaFactory);");
		checkEQByDiff = UTChange.checkEQByDiff(qNode, tNode);
		System.out.println("[DBG] " + checkEQByDiff);
	}

	/**
	 * Check eq by diff.
	 * 
	 * @param aQNode the a q node
	 * @param aTNode the a t node
	 * @return true, if successful
	 */
	public static boolean checkEQByDiff(Node aQNode, Node aTNode) {
		String aQNodeValue = aQNode.getValueParm();
		if (aQNodeValue == null) {
			aQNodeValue = aQNode.getValue();
		}
		aQNodeValue = aQNodeValue.replaceAll(UTCfg.PARM_VAR_NAME, UTCfg.PARM_VAR_NAME_TMP);
		String aTNodeValue = aTNode.getValue();
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		List<Diff> diffList = diffUtil.diff_main(aQNodeValue, aTNodeValue);
		List<String> lstDel = new ArrayList<String>();
		List<String> lstIns = new ArrayList<String>();
		for (Diff diff : diffList) {
			if (diff.operation == Operation.DELETE) {
				lstDel.add(diff.text);
			} else if (diff.operation == Operation.INSERT) {
				lstIns.add(diff.text);
			}
		}
		if (lstDel.size() != lstIns.size()) {
			return false;
		}
		for (String string : lstDel) {
			if (!string.startsWith("$")) {
				return false;
			}
		}
		return true;
	}

	public static boolean checkEQByDiff2(Node aQNode, Node aTNode) {
		System.out.println("[DBG] checkEQByDiff2:");
		System.out.println("aQNode: " + aQNode + " <=> " + "aTNode: " + aTNode);
		String aQNodeValue = aQNode.getValueParm();
		if (aQNodeValue == null) {
			aQNodeValue = aQNode.getValue();
		}
		aQNodeValue = aQNodeValue.replaceAll(UTCfg.PARM_VAR_NAME, UTCfg.PARM_VAR_NAME_TMP);
		String aTNodeValue = aTNode.getValue();
		UTCriticsDiffUtil diffUtil = new UTCriticsDiffUtil();
		System.out.println("aQNodeValue: " + aQNodeValue + " <=> " + "aTNodeValue: " + aTNodeValue);
		List<Diff> diffList = diffUtil.diff_main(aQNodeValue, aTNodeValue);
		List<String> lstDel = new ArrayList<String>();
		List<String> lstIns = new ArrayList<String>();
		for (Diff diff : diffList) {
			System.out.println("diff.operation: " + diff.operation + " <=> " + "diff.text: " + diff.text);
			if (diff.operation == Operation.DELETE) {
				lstDel.add(diff.text);
			} else if (diff.operation == Operation.INSERT) {
				lstIns.add(diff.text);
			}
		}
		if (lstDel.size() != lstIns.size()) {
			return false;
		}
		for (String string : lstDel) {
			if (!string.startsWith("$")) {
				return false;
			}
		}
		return true;
	}	
	/**
	 * Gets the node list method level.
	 * 
	 * @param aLstChange the a lst change
	 * @param aUnitMatchedNode the a unit matched node
	 * @param aSrcMatchedNode the a src matched node
	 * @param aFile the a file
	 * @return the node list method level
	 */
	public static List<Node> getNodeListMethodLevel(List<SourceCodeChange> aLstChange, //
			CompilationUnit aUnitMatchedNode, String aSrcMatchedNode, File aFile) {
		List<Node> lstNode = new ArrayList<Node>();
		Node methodNode = null;
		for (int i = 0; i < aLstChange.size(); i++) {
			SourceCodeChange iChange = aLstChange.get(i);
			if (methodNode == null) {
				methodNode = getMethodNode(iChange, aUnitMatchedNode, aSrcMatchedNode, aFile);
			}
			//System.out.println("[DBG UTChange.getNodeListMethodLevel()] methodNode: " + methodNode);
			Node nodes = getChildNode(iChange, methodNode);
			//System.out.println("[DBG UTChange.getNodeListMethodLevel()] nodes: " + nodes);
			lstNode.add(nodes);
		}
		return lstNode;
	}

	/**
	 * Gets the method node.
	 * 
	 * @param change the change
	 * @param aUnit the a unit
	 * @param aSource the a source
	 * @param aFile the a file
	 * @return the method node
	 */
	private static Node getMethodNode(SourceCodeChange change, CompilationUnit aUnit, String aSource, File aFile) {
		String message = "[WRN] null pointing";
		
		System.out.println("[DBG UTChange.getMethodNode()] change: " + change);
		System.out.println("[DBG UTChange.getMethodNode()] change.getChangeType(): " + change.getChangeType());
		//System.out.println("[DBG UTChange.getMethodNode()] change.getLabel(): " + change.getLabel());
		//System.out.println("[DBG UTChange.getMethodNode()] change.getParentEntity(): " + change.getParentEntity());
		SourceCodeEntity sce = change.getChangedEntity();
		System.out.println("[DBG UTChange.getMethodNode()] sce: " + sce);
		System.out.println("[DBG UTChange.getMethodNode()] sce.getUniqueName(): " + sce.getUniqueName());
		System.out.println("[DBG UTChange.getMethodNode()] sce.getSourceRange(): " + sce.getSourceRange());
		//System.out.println("[DBG UTChange.getMethodNode()] sce.getStartPosition(): " + sce.getStartPosition());
		//System.out.println("[DBG UTChange.getMethodNode()] sce.getEndPosition(): " + sce.getEndPosition());
		System.out.println("[DBG UTChange.getMethodNode()] sce.getType(): " + sce.getType());				
		int startPosition = change.getChangedEntity().getStartPosition();
		//System.out.println("[DBG UTChange.getMethodNode()] startPosition: " + startPosition);		
		System.out.println("[DBG UTChange.getMethodNode()] CompilationUnit Length: " + aUnit.getLength());
		//String aUnitStr = aUnit.toString();
		//System.out.println("[DBG UTChange.getMethodNode()] CompilationUnit String Length: " + aUnitStr.length());
		System.out.println("[DBG UTChange.getMethodNode()] aSource Length: " + aSource.length());
		System.out.println("[DBG UTChange.getMethodNode()] Selected SourceCodeEntity String: \n" + aSource.substring(sce.getStartPosition(), sce.getEndPosition() + 1));
		int defaultLength = 1;
		UTASTNodeFinder finder = new UTASTNodeFinder();
		UTASTNodeConverter converter = new UTASTNodeConverter();
		//MethodDeclaration methodDecl = finder.findCoveringMethodDeclaration(aUnit, new Point(startPosition, defaultLength));
		MethodDeclaration methodDecl = finder.findMethod(aSource, change.getChangedEntity().getSourceRange(), false);
		//String srcRev = UTFile.getContents(aFile.getAbsolutePath());
		System.out.println("[DBG UTChange.getMethodNode()] methodDecl: " + methodDecl.getName().getFullyQualifiedName());
		//System.out.println("[DBG UTChange.getMethodNode()] methodDecl startPosition: " + methodDecl.getStartPosition());
		//System.out.printf("[DBG UTChange.getMethodNode()] methodDecl endPosition: %d\n", methodDecl.getStartPosition() + methodDecl.getLength());
		//System.out.println("[DBG UTChange.getMethodNode()] methodDecl String: \n" + srcRev.substring(methodDecl.getStartPosition(), methodDecl.getStartPosition() + methodDecl.getLength()));
//		List<IfStatement> lstIfStatement = findIfStatement(methodDecl);
//		for (IfStatement ifStmt : lstIfStatement) {
//			System.out.println("[DBG UTChange.getMethodNode()] ifStmt startPosition: " + ifStmt.getStartPosition());
//			System.out.printf("[DBG UTChange.getMethodNode()] ifStmt endPosition: %d\n", ifStmt.getStartPosition() + ifStmt.getLength());
//			System.out.println("[DBG UTChange.getMethodNode()] ifStmt: \n" + ifStmt);
//			System.out.println("[DBG UTChange.getMethodNode()] ifStmt String: \n" + srcRev.substring(ifStmt.getStartPosition(), ifStmt.getStartPosition() + ifStmt.getLength()));
//		}				
		if (methodDecl == null)
			throw new RuntimeException(message);
		Node resultNodeConverted = converter.convertMethod(methodDecl, aSource, aFile);
		if (resultNodeConverted == null)
			throw new RuntimeException(message);
		return resultNodeConverted;
	}

	private static List<IfStatement> findIfStatement(MethodDeclaration methodDecl) {
		UTIGeneralVisitor<IfStatement> mVisitor = new UTGeneralVisitor<IfStatement>() {
			public boolean visit(IfStatement node) {
				results.add(node);
				return true;
			}
		};
		methodDecl.accept(mVisitor);
		return mVisitor.getResults();
	}
	
	/**
	 * Gets the child node.
	 * 
	 * @param aChange the a change
	 * @param aMethodNode the a method node
	 * @return the child node
	 */
	private static Node getChildNode(SourceCodeChange aChange, Node aMethodNode) {
		String label = aChange.getChangedEntity().getLabel();
		int startPosition = aChange.getChangedEntity().getStartPosition();
		Enumeration<?> e = aMethodNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			Node iNode = (Node) e.nextElement();
			String iLabel = iNode.getEntity().getLabel();
			int iBgnOffset = iNode.getEntity().getStartPosition();
			if (iBgnOffset >= startPosition && label.equals(iLabel))
				return iNode;
		}
		return null;
	}

	/**
	 * Prints the change.
	 * 
	 * @param aList the a list
	 * @param isPrnt the is prnt
	 */
	public static void printChange(List<SourceCodeChange> aList, boolean isPrnt) {
		for (int i = 0; i < aList.size(); i++) {
			SourceCodeChange change = aList.get(i);
			UTLog.println(isPrnt, "[RST] " + change.getChangedEntity().getLabel() + " " + change.getChangedEntity().getUniqueName());
		}
	}

	/**
	 * Prints the node.
	 * 
	 * @param aList the a list
	 * @param isPrnt the is prnt
	 */
	public static void printNode(List<Node> aList, boolean isPrnt) {
		for (int i = 0; i < aList.size(); i++) {
			Node change = aList.get(i);
			UTLog.println(isPrnt, "[RST] " + change.getEntity().getLabel() + " " + change.getEntity().getUniqueName());
//			if (change != null) {
//				System.out.println("[RST] " + change.getEntity().getLabel() + " " + change.getEntity().getUniqueName());
//			}
//			else {
//				System.out.println("[RST] change is null!");
//			}			
		}
	}
}
