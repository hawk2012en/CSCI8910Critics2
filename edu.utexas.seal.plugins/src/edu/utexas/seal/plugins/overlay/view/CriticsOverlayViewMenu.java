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
 * @(#) CriticsOverlayViewHelper.java
 *
 * Copyright 2013 2014 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
 */
package edu.utexas.seal.plugins.overlay.view;

import java.io.File;
import java.util.List;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ut.seal.plugins.utils.UTFile;
import ut.seal.plugins.utils.UTStr;
import ut.seal.plugins.utils.ast.UTASTParser;
import ut.seal.plugins.utils.change.UTChangeDistiller;
import ut.seal.plugins.utils.visitor.DeclarationVisitor;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodeForStmt;
import edu.utexas.seal.plugins.analyzer.ReplaceMethodBodyAnalyzer;
import edu.utexas.seal.plugins.crystal.internal.UTASTNodeSearcher;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeHelper;
import edu.utexas.seal.plugins.overlay.model.CriticsCBTreeNode;
import edu.utexas.seal.plugins.util.UTCriticsPairFileInfo;
import edu.utexas.seal.plugins.util.UTCriticsTextSelection;
import edu.utexas.seal.plugins.util.root.UTCriticsEditor;

/**
 * @author Myoungkyu Song
 * @date Feb 7, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class CriticsOverlayViewMenu {
	protected CriticsOverlayView		mCriticsOverlayView	= null;
	private CheckboxTreeViewer			mTVSimilarContext	= null;
	private Text						mTxtSearch			= null;
	private Browser						mLeftBrowser		= null;
	private Browser						mRightBrowser		= null;
	private CriticsOverlayBrowser		mHTMLLeftBrowser	= null;
	private CriticsOverlayBrowser		mHTMLRightBrowser	= null;
	private CriticsOverlayNewBrowser	mNewBrowser			= null;
	private CriticsCBTreeHelper			mCBTreeHelper		= null;
	private TableViewer					mSummaryTableViewer		= null;
	private TableViewer 				mAnomalyTableViewer     = null;

	public CriticsOverlayViewMenu(CriticsOverlayView criticsOverlayView) {
		mCriticsOverlayView = criticsOverlayView;
		mTVSimilarContext = criticsOverlayView.getTVSimilarContext();
		mTxtSearch = criticsOverlayView.getTxtSearch();
		mLeftBrowser = criticsOverlayView.getLeftBrowser();
		mRightBrowser = criticsOverlayView.getRightBrowser();
		mHTMLLeftBrowser = criticsOverlayView.getHTMLLeftBrowser();
		mHTMLRightBrowser = criticsOverlayView.getHTMLRightBrowser();
		mCBTreeHelper = criticsOverlayView.getCBTreeHelper();
		mSummaryTableViewer = criticsOverlayView.getSummaryTableViewer();
		mAnomalyTableViewer = criticsOverlayView.getAnomalyTableViewer();
	}

	public CriticsOverlayViewMenu(CriticsOverlayNewView overlayView) {
		mCriticsOverlayView = overlayView;
		mSummaryTableViewer = overlayView.getSummaryTableViewer();
		mAnomalyTableViewer = overlayView.getAnomalyTableViewer();
		mNewBrowser = overlayView.getNewDiffBrowser();
	}

	void addMenuToSummaryTableViewer() {
		Table table = mSummaryTableViewer.getTable();
		Menu itemActions = new Menu(table);
		table.setMenu(itemActions);

		MenuItem itemActionShowTemplate = new MenuItem(itemActions, SWT.NONE);
		itemActionShowTemplate.setText("Show Template");
		itemActionShowTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UTChangeDistiller changeDistiller = mCriticsOverlayView.diffQTree();
				Node qTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
				Node qTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
				mNewBrowser.setSourceBaseNode(changeDistiller, qTreeRightRev, qTreeLeftRev);
				// System.out.println("[DBG] Show Template " + e);
				// if (mHTMLLeftBrowser == null || mHTMLRightBrowser == null) {
				// return;
				// }
				// mHTMLLeftBrowser.setSourceBaseNode(qTreeLeftRev, UTCriticsPairFileInfo.getLeftFile());
				// mHTMLRightBrowser.setSourceBaseNode(qTreeRightRev, UTCriticsPairFileInfo.getLeftFile());
			}
		});
	}
	
	void addMenuToAnomalyTableViewer() {
		Table table = mAnomalyTableViewer.getTable();
		Menu itemActions = new Menu(table);
		table.setMenu(itemActions);

		MenuItem itemActionShowTemplate = new MenuItem(itemActions, SWT.NONE);
		itemActionShowTemplate.setText("Show Template");
		itemActionShowTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UTChangeDistiller changeDistiller = mCriticsOverlayView.diffQTree();
				Node qTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
				Node qTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
				mNewBrowser.setSourceBaseNode(changeDistiller, qTreeRightRev, qTreeLeftRev);
			}
		});
		
		MenuItem itemActionFixIncorrectUpdate = new MenuItem(itemActions, SWT.NONE);
		itemActionFixIncorrectUpdate.setText("Fix Incorrect Update");
		itemActionFixIncorrectUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer mAnomalyTableViewer = mCriticsOverlayView.getAnomalyTableViewer();
				TableItem item = mAnomalyTableViewer.getTable().getSelection()[0];
				Object data = item.getData();
				if (data instanceof CriticsCBTreeNode) {
					CriticsCBTreeNode tSelected = (CriticsCBTreeNode) data;
					Node selectedRightRev = tSelected.getNode();
					String value = selectedRightRev.getValue();
					String className = selectedRightRev.getClassName();
					String packageName = selectedRightRev.getPackageName();
					System.out.println("[DBG6] Selected Incorrect Updated Method: " + "[" + packageName + "]" + "[" + className + "]" + "[" + value + "]");
					Node selectedLeftRev = mCriticsOverlayView.mEventHandler.getOppositeNode(tSelected);
					System.out.println("[DBG6] old Node selectedRightRev: " + selectedRightRev + " source range: " + selectedRightRev.getEntity().getSourceRange());
					System.out.println("[DBG6] new Node selectedLeftRev: " + selectedLeftRev + " source range: " + selectedLeftRev.getEntity().getSourceRange());
					
					File fOldRev = UTCriticsPairFileInfo.getRightFile();
					String srcOldFile = UTFile.getContents(fOldRev.getAbsolutePath());
					System.out.println("[DBG6] srcOldFile.length(): " + srcOldFile.length());
					File fNewRev = UTCriticsPairFileInfo.getLeftFile();
					String srcNewFile = UTFile.getContents(fNewRev.getAbsolutePath());
					System.out.println("[DBG6] srcNewFile.length(): " + srcNewFile.length());

					MergeSourceViewer rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
					ISourceViewer rightSRViewer = rightMSViewer.getSourceViewer();
					String srcOldViewer = rightSRViewer.getDocument().get();
					System.out.println("[DBG6] srcOldViewer.length(): " + srcOldViewer.length());
					MergeSourceViewer leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
					ISourceViewer leftSRViewer = leftMSViewer.getSourceViewer();
					String srcNewViewer = leftSRViewer.getDocument().get();
					System.out.println("[DBG6] srcNewViewer.length(): " + srcNewViewer.length());

					
					MethodDeclaration oldMethodDecl = selectedRightRev.getMethodDeclaration();
					MethodDeclaration newMethodDecl = selectedLeftRev.getMethodDeclaration();
					System.out.println("[DBG6] oldMethodDecl: \n" + srcOldFile.substring(oldMethodDecl.getStartPosition(), oldMethodDecl.getStartPosition() + oldMethodDecl.getLength()));
					System.out.println("[DBG6] newMethodDecl: \n" + srcNewFile.substring(newMethodDecl.getStartPosition(), newMethodDecl.getStartPosition() + newMethodDecl.getLength()));
					System.out.println("[DBG6] selectedRightRev: \n" + srcOldFile.substring(selectedRightRev.getEntity().getStartPosition(), selectedRightRev.getEntity().getEndPosition() + 1));
					System.out.println("[DBG6] selectedLeftRev: \n" + srcNewFile.substring(selectedLeftRev.getEntity().getStartPosition(), selectedLeftRev.getEntity().getEndPosition() + 1));
					
					String oldMethodBody = oldMethodDecl.getBody().toString();
					String oldMethodBodyStms = oldMethodBody.substring(1, oldMethodBody.length() - 2);
					String newMethodBody = newMethodDecl.getBody().toString();
					String newMethodBodyStms = newMethodBody.substring(1, newMethodBody.length() - 2);					
					System.out.println("[DBG6] oldMethodBodyStms: \n" + oldMethodBodyStms);
					System.out.println("[DBG6] newMethodBodyStms: \n" + newMethodBodyStms);
					List<Statement> lstOldStms = oldMethodDecl.getBody().statements();					
					String oldMethodBodyStms2 = srcOldFile.substring(lstOldStms.get(0).getStartPosition(), lstOldStms.get(lstOldStms.size()-1).getStartPosition() + lstOldStms.get(lstOldStms.size()-1).getLength());
					System.out.println("[DBG6] oldMethodBodyStms2: \n" + oldMethodBodyStms2);
					List<Statement> lstNewStms = newMethodDecl.getBody().statements();
					String newMethodBodyStms2 = srcNewFile.substring(lstNewStms.get(0).getStartPosition(), lstNewStms.get(lstNewStms.size()-1).getStartPosition() + lstNewStms.get(lstNewStms.size()-1).getLength());
					System.out.println("[DBG6] newMethodBodyStms2: \n" + newMethodBodyStms2);
					
					Node mQTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
					Node mQTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
					//System.out.println("[DBG6] old query Node mQTreeRightRev: " + mQTreeRightRev + " source range: " + mQTreeRightRev.getEntity().getSourceRange());
					//System.out.println("[DBG6] new query Node mQTreeLeftRev: " + mQTreeLeftRev + " source range: " + mQTreeLeftRev.getEntity().getSourceRange());
					MethodDeclaration oldQueryMethodDecl = mQTreeRightRev.getMethodDeclaration();
					MethodDeclaration newQueryMethodDecl = mQTreeLeftRev.getMethodDeclaration();
					
					//UTChangeDistiller diffTTree = new UTChangeDistiller();
					//diffTTree.diffBlock(selectedRightRev.copy(), selectedLeftRev.copy()); // right -> left
					//System.out.println("[DBG6] source code changes between selectedRightRev and selectedLeftRev:");
					//diffTTree.printChanges();
					
					String oldFilePath = tSelected.getFile().getAbsolutePath();
					String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
					String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
					String newFilePath = oldFilePath.replace(prjNameRight, prjNameLeft);
					System.out.println("[DBG6] old file path: " + oldFilePath);		
					System.out.println("[DBG6] new file path: " + newFilePath);
					
//					UTASTParser astParser = new UTASTParser();
//					String newSource = UTFile.getContents(newFilePath);
//					CompilationUnit newCU = astParser.parse(newSource);
//					DeclarationVisitor mVisitor = new DeclarationVisitor(selectedLeftRev.getValue());
//					newCU.accept(mVisitor);
//					System.out.println("[DBG6] new package name: " + newCU.getPackage().getName());					
										
					List<Node> incorrectInsertionNodes = mCriticsOverlayView.mEventHandler.getIncorrectInsertionNodes();
					List<Node> incorrectDeletionNodes = mCriticsOverlayView.mEventHandler.getIncorrectDeletionNodes();
					List<Node> missingInsertionNodes = mCriticsOverlayView.mEventHandler.getMissingInsertionNodes();		
					List<Node> missingDeletionNodes = mCriticsOverlayView.mEventHandler.getMissingDeletionNodes();
					for (Node iNode : incorrectInsertionNodes) {
						System.out.println("[DBG6] incorrectInsertionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] incorrectInsertionNode String: \n" + srcNewFile.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					for (Node iNode : incorrectDeletionNodes) {
						System.out.println("[DBG6] incorrectDeletionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] incorrectDeletionNode String: \n" + srcOldFile.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					for (Node iNode : missingInsertionNodes) {
						System.out.println("[DBG6] missingInsertionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] missingInsertionNode String: \n" + srcNewViewer.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					for (Node iNode : missingDeletionNodes) {
						System.out.println("[DBG6] missingDeletionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] missingDeletionNode String: \n" + srcNewFile.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					
					String updatedMethodBodyStms = newMethodBodyStms;
					String updatedMethodBodyStms2 = newMethodBodyStms2;
					for (int i = 0; i < incorrectInsertionNodes.size(); i++) {
						Node incorrectInsertionNode = incorrectInsertionNodes.get(i);
						Node missingInsertionNode = missingInsertionNodes.get(i);	
						
						String incorrectInsertionNodeStr = "";
						String incorrectInsertionNodeStr2 = "";
						String incorrectInsertionNodeLabel = incorrectInsertionNode.getLabel().name();
						String incorrectInsertionNodeValue = incorrectInsertionNode.getValue();
						if (incorrectInsertionNodeLabel.equals("FOR_STATEMENT")) {							
							//incorrectInsertionNodeStr = removeParen(incorrectInsertionNodeValue);
							ForStatement forStmt = (ForStatement) getASTNode(incorrectInsertionNode, newMethodDecl);
							//System.out.println("[DBG6] forStmt: \n" + forStmt);
							incorrectInsertionNodeStr = forStmt.getExpression().toString();
							//System.out.println("[DBG6] forStmt expression: " + incorrectInsertionNodeStr);
							incorrectInsertionNodeStr2 = srcNewFile.substring(forStmt.getExpression().getStartPosition(), forStmt.getExpression().getStartPosition() + forStmt.getExpression().getLength());
						}
						else {
							incorrectInsertionNodeStr = getLabel(incorrectInsertionNodeLabel) + incorrectInsertionNodeValue;
							incorrectInsertionNodeStr2 = getLabel(incorrectInsertionNodeLabel) + incorrectInsertionNodeValue;
						}						
						System.out.println("[DBG6] incorrectInsertionNodeStr: " + incorrectInsertionNodeStr);
						System.out.println("[DBG6] incorrectInsertionNodeStr2: " + incorrectInsertionNodeStr2);
						String missingInsertionNodeStr = "";
						String missingInsertionNodeStr2 = "";
						String missingInsertionNodeLabel = missingInsertionNode.getLabel().name();
						String missingInsertionNodeValue = missingInsertionNode.getValue();
						if (missingInsertionNodeLabel.equals("FOR_STATEMENT")) {							
							//missingInsertionNodeStr = removeParen(missingInsertionNodeValue);
							ForStatement forStmt = (ForStatement) getASTNode(missingInsertionNode, newQueryMethodDecl);
							missingInsertionNodeStr = forStmt.getExpression().toString();
							missingInsertionNodeStr2 = srcNewViewer.substring(forStmt.getExpression().getStartPosition(), forStmt.getExpression().getStartPosition() + forStmt.getExpression().getLength());
						}
						else {
							missingInsertionNodeStr = getLabel(missingInsertionNodeLabel) + missingInsertionNodeValue;
							missingInsertionNodeStr2 = getLabel(missingInsertionNodeLabel) + missingInsertionNodeValue;
						}						
						System.out.println("[DBG6] missingInsertionNodeStr: " + missingInsertionNodeStr);
						System.out.println("[DBG6] missingInsertionNodeStr2: " + missingInsertionNodeStr2);
						int startIndex = updatedMethodBodyStms.indexOf(incorrectInsertionNodeStr);												
						int endIndex = startIndex + incorrectInsertionNodeStr.length();
						System.out.println("[DBG6] startIndex: " + startIndex + " endIndex: " + endIndex);
						
//						String incorrectInsertionNodeStr2 = srcNewFile.substring(incorrectInsertionNode.getEntity().getStartPosition(), incorrectInsertionNode.getEntity().getEndPosition() + 1);
//						String missingInsertionNodeStr2 = srcNewViewer.substring(missingInsertionNode.getEntity().getStartPosition(), missingInsertionNode.getEntity().getEndPosition() + 1);
//						int startIndex2 =  incorrectInsertionNode.getEntity().getStartPosition() - lstNewStms.get(0).getStartPosition();
//						int endIndex2 = startIndex2 + incorrectInsertionNodeStr2.length();
						int startIndex2 = updatedMethodBodyStms2.indexOf(incorrectInsertionNodeStr2);												
						int endIndex2 = startIndex2 + incorrectInsertionNodeStr2.length();
						System.out.println("[DBG6] startIndex2: " + startIndex2 + " endIndex2: " + endIndex2);						
						//System.out.println("[DBG6] updatedMethodBodyStms2.substring(startIndex2, endIndex2): \n" + updatedMethodBodyStms2.substring(startIndex2, endIndex2));
						updatedMethodBodyStms = UTStr.replace(updatedMethodBodyStms, missingInsertionNodeStr, startIndex, endIndex);						
						updatedMethodBodyStms2 = UTStr.replace(updatedMethodBodyStms2, missingInsertionNodeStr2, startIndex2, endIndex2);
					}
					System.out.println("[DBG6] updatedMethodBodyStms: \n" + updatedMethodBodyStms);
					System.out.println("[DBG6] updatedMethodBodyStms2: \n" + updatedMethodBodyStms2);
					//new ReplaceMethodBodyAnalyzer(prjNameLeft, packageName, UTStr.getClassNameFromJavaFile(className), selectedLeftRev.getValue(), updatedMethodBodyStms);
					new ReplaceMethodBodyAnalyzer(prjNameLeft, packageName, UTStr.getClassNameFromJavaFile(className), selectedLeftRev.getValue(), updatedMethodBodyStms2);
				}
			}
		});
		
		MenuItem itemActionApplyMissingUpdate = new MenuItem(itemActions, SWT.NONE);
		itemActionApplyMissingUpdate.setText("Apply Missing Update");
		itemActionApplyMissingUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer mAnomalyTableViewer = mCriticsOverlayView.getAnomalyTableViewer();
				TableItem item = mAnomalyTableViewer.getTable().getSelection()[0];
				Object data = item.getData();
				if (data instanceof CriticsCBTreeNode) {
					CriticsCBTreeNode tSelected = (CriticsCBTreeNode) data;
					Node selectedRightRev = tSelected.getNode();
					String value = selectedRightRev.getValue();
					String className = selectedRightRev.getClassName();
					String packageName = selectedRightRev.getPackageName();
					System.out.println("[DBG6] Selected Incorrect Updated Method: " + "[" + packageName + "]" + "[" + className + "]" + "[" + value + "]");
					Node selectedLeftRev = mCriticsOverlayView.mEventHandler.getOppositeNode(tSelected);
					System.out.println("[DBG6] old Node selectedRightRev: " + selectedRightRev + " source range: " + selectedRightRev.getEntity().getSourceRange());
					System.out.println("[DBG6] new Node selectedLeftRev: " + selectedLeftRev + " source range: " + selectedLeftRev.getEntity().getSourceRange());
					
					File fOldRev = UTCriticsPairFileInfo.getRightFile();
					String srcOldFile = UTFile.getContents(fOldRev.getAbsolutePath());
					System.out.println("[DBG6] srcOldFile.length(): " + srcOldFile.length());
					File fNewRev = UTCriticsPairFileInfo.getLeftFile();
					String srcNewFile = UTFile.getContents(fNewRev.getAbsolutePath());
					System.out.println("[DBG6] srcNewFile.length(): " + srcNewFile.length());

					MergeSourceViewer rightMSViewer = UTCriticsTextSelection.rightMergeSourceViewer;
					ISourceViewer rightSRViewer = rightMSViewer.getSourceViewer();
					String srcOldViewer = rightSRViewer.getDocument().get();
					System.out.println("[DBG6] srcOldViewer.length(): " + srcOldViewer.length());
					MergeSourceViewer leftMSViewer = UTCriticsTextSelection.leftMergeSourceViewer;
					ISourceViewer leftSRViewer = leftMSViewer.getSourceViewer();
					String srcNewViewer = leftSRViewer.getDocument().get();
					System.out.println("[DBG6] srcNewViewer.length(): " + srcNewViewer.length());

					MethodDeclaration oldMethodDecl = selectedRightRev.getMethodDeclaration();
					MethodDeclaration newMethodDecl = selectedLeftRev.getMethodDeclaration();					
					String oldMethodBody = oldMethodDecl.getBody().toString();
					String oldMethodBodyStms = oldMethodBody.substring(1, oldMethodBody.length() - 2);
					String newMethodBody = newMethodDecl.getBody().toString();
					String newMethodBodyStms = newMethodBody.substring(1, newMethodBody.length() - 2);					
					System.out.println("[DBG6] oldMethodBodyStms: \n" + oldMethodBodyStms);
					System.out.println("[DBG6] newMethodBodyStms: \n" + newMethodBodyStms);
					List<Statement> lstOldStms = oldMethodDecl.getBody().statements();					
					String oldMethodBodyStms2 = srcOldFile.substring(lstOldStms.get(0).getStartPosition(), lstOldStms.get(lstOldStms.size()-1).getStartPosition() + lstOldStms.get(lstOldStms.size()-1).getLength());
					System.out.println("[DBG6] oldMethodBodyStms2: \n" + oldMethodBodyStms2);
					List<Statement> lstNewStms = newMethodDecl.getBody().statements();
					String newMethodBodyStms2 = srcNewFile.substring(lstNewStms.get(0).getStartPosition(), lstNewStms.get(lstNewStms.size()-1).getStartPosition() + lstNewStms.get(lstNewStms.size()-1).getLength());
					System.out.println("[DBG6] newMethodBodyStms2: \n" + newMethodBodyStms2);
					
					Node mQTreeLeftRev = mCriticsOverlayView.getQTreeNewRev();
					Node mQTreeRightRev = mCriticsOverlayView.getQTreeOldRev();
					System.out.println("[DBG6] old query Node mQTreeRightRev: " + mQTreeRightRev + " source range: " + mQTreeRightRev.getEntity().getSourceRange());
					System.out.println("[DBG6] new query Node mQTreeLeftRev: " + mQTreeLeftRev + " source range: " + mQTreeLeftRev.getEntity().getSourceRange());
					
					MethodDeclaration oldQueryMethodDecl = mQTreeRightRev.getMethodDeclaration();
					MethodDeclaration newQueryMethodDecl = mQTreeLeftRev.getMethodDeclaration();
					String oldQueryMethodBody = oldQueryMethodDecl.getBody().toString();
					String oldQueryMethodBodyStms = oldQueryMethodBody.substring(1, oldQueryMethodBody.length() - 2);
					String newQueryMethodBody = newQueryMethodDecl.getBody().toString();
					String newQueryMethodBodyStms = newQueryMethodBody.substring(1, newQueryMethodBody.length() - 2);					
					System.out.println("[DBG6] oldQueryMethodBodyStms: \n" + oldQueryMethodBodyStms);
					System.out.println("[DBG6] newQueryMethodBodyStms: \n" + newQueryMethodBodyStms);
					List<Statement> lstOldQueryStms = oldQueryMethodDecl.getBody().statements();					
					String oldQueryMethodBodyStms2 = srcOldViewer.substring(lstOldQueryStms.get(0).getStartPosition(), lstOldQueryStms.get(lstOldQueryStms.size()-1).getStartPosition() + lstOldQueryStms.get(lstOldQueryStms.size()-1).getLength());
					System.out.println("[DBG6] oldQueryMethodBodyStms2: \n" + oldQueryMethodBodyStms2);
					List<Statement> lstNewQueryStms = newQueryMethodDecl.getBody().statements();
					String newQueryMethodBodyStms2 = srcNewViewer.substring(lstNewQueryStms.get(0).getStartPosition(), lstNewQueryStms.get(lstNewQueryStms.size()-1).getStartPosition() + lstNewQueryStms.get(lstNewQueryStms.size()-1).getLength());
					System.out.println("[DBG6] newQueryMethodBodyStms2: \n" + newQueryMethodBodyStms2);					
					
					String oldFilePath = tSelected.getFile().getAbsolutePath();
					String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
					String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
					String newFilePath = oldFilePath.replace(prjNameRight, prjNameLeft);
					System.out.println("[DBG6] old file path: " + oldFilePath);		
					System.out.println("[DBG6] new file path: " + newFilePath);										
										
					List<Node> missingInsertionNodes = mCriticsOverlayView.mEventHandler.getMissingInsertionNodes();		
					List<Node> missingDeletionNodes = mCriticsOverlayView.mEventHandler.getMissingDeletionNodes();

					for (Node iNode : missingInsertionNodes) {
						System.out.println("[DBG6] missingInsertionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] missingInsertionNode String: \n" + srcNewViewer.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					for (Node iNode : missingDeletionNodes) {
						System.out.println("[DBG6] missingDeletionNode: " + iNode + " source range: " + iNode.getEntity().getSourceRange());
						System.out.println("[DBG6] missingDeletionNode String: \n" + srcNewFile.substring(iNode.getEntity().getStartPosition(), iNode.getEntity().getEndPosition() + 1));
					}
					
					String updatedMethodBodyStms = newMethodBodyStms;
					String updatedMethodBodyStms2 = newMethodBodyStms2;
					//String missingDeletionNodesFirst = getLabel(missingDeletionNodes.get(0).getLabel().name()) + missingDeletionNodes.get(0).getValue();
					//System.out.println("[DBG6] missingDeletionNodesFirst: " + missingDeletionNodesFirst);
					int missingDeletionNodesSize = missingDeletionNodes.size();
					//String missingDeletionNodesLast = getLabel(missingDeletionNodes.get(missingDeletionNodesSize-1).getLabel().name()) + missingDeletionNodes.get(missingDeletionNodesSize-1).getValue();
					//System.out.println("[DBG6] missingDeletionNodesLast: " + missingDeletionNodesLast);
					ASTNode missingDeletionNodesFirst = getASTNode(missingDeletionNodes.get(0), newMethodDecl);
					ASTNode missingDeletionNodesLast = getASTNode(missingDeletionNodes.get(missingDeletionNodesSize-1), newMethodDecl);
					//int startIndexMissingDeletion = newMethodBodyStms.indexOf(missingDeletionNodesFirst);					
					int startIndexMissingDeletion = missingDeletionNodesFirst.getStartPosition() - lstNewStms.get(0).getStartPosition();
					//int endIndexMissingDeletion = newMethodBodyStms.indexOf(missingDeletionNodesLast) + missingDeletionNodesLast.length();					
					int endIndexMissingDeletion = missingDeletionNodesLast.getStartPosition() + missingDeletionNodesLast.getLength() + 1 - lstNewStms.get(0).getStartPosition();
					String missingDeletionNodesRange = newMethodBodyStms2.substring(startIndexMissingDeletion, endIndexMissingDeletion);										
					System.out.println("[DBG6] missingDeletionNodesRange: \n" + missingDeletionNodesRange);
					int startIndexMissingDeletion2 = missingDeletionNodes.get(0).getEntity().getStartPosition() - lstNewStms.get(0).getStartPosition();
					int endIndexMissingDeletion2 = missingDeletionNodes.get(missingDeletionNodesSize-1).getEntity().getEndPosition() - lstNewStms.get(0).getStartPosition();
					String missingDeletionNodesRange2 = newMethodBodyStms2.substring(startIndexMissingDeletion2, endIndexMissingDeletion2);
					System.out.println("[DBG6] missingDeletionNodesRange2: \n" + missingDeletionNodesRange2);
					
					String missingInsertionNodesFirst = getLabel(missingInsertionNodes.get(0).getLabel().name()) + missingInsertionNodes.get(0).getValue();
					//System.out.println("[DBG6] missingInsertionNodesFirst: " + missingInsertionNodesFirst);
					int missingInsertionNodesSize = missingInsertionNodes.size();
					String missingInsertionNodesLast = getLabel(missingInsertionNodes.get(missingInsertionNodesSize-1).getLabel().name()) + missingInsertionNodes.get(missingInsertionNodesSize-1).getValue();
					//System.out.println("[DBG6] missingInsertionNodesLast: " + missingInsertionNodesLast);
					int startIndexMissingInsertion = newQueryMethodBodyStms.indexOf(missingInsertionNodesFirst);
					int endIndexMissingInsertion = newQueryMethodBodyStms.indexOf(missingInsertionNodesLast) + missingInsertionNodesLast.length();
					String missingInsertionNodesRange = newQueryMethodBodyStms.substring(startIndexMissingInsertion, endIndexMissingInsertion);
					System.out.println("[DBG6] missingInsertionNodesRange: \n" + missingInsertionNodesRange);
					int startIndexMissingInsertion2 = missingInsertionNodes.get(0).getEntity().getStartPosition() - lstNewQueryStms.get(0).getStartPosition();
					int endIndexMissingInsertion2 = missingInsertionNodes.get(missingInsertionNodesSize-1).getEntity().getEndPosition() - lstNewQueryStms.get(0).getStartPosition();
					String missingInsertionNodesRange2 = newQueryMethodBodyStms2.substring(startIndexMissingInsertion2, endIndexMissingInsertion2);
					System.out.println("[DBG6] missingInsertionNodesRange2: \n" + missingInsertionNodesRange2);
					
					updatedMethodBodyStms = UTStr.replace(updatedMethodBodyStms2, missingInsertionNodesRange, startIndexMissingDeletion, endIndexMissingDeletion);
					updatedMethodBodyStms2 = UTStr.replace(updatedMethodBodyStms2, missingInsertionNodesRange2, startIndexMissingDeletion2, endIndexMissingDeletion2);
					System.out.println("[DBG6] updatedMethodBodyStms: \n" + updatedMethodBodyStms);
					System.out.println("[DBG6] updatedMethodBodyStms2: \n" + updatedMethodBodyStms2);
					//new ReplaceMethodBodyAnalyzer(prjNameLeft, packageName, UTStr.getClassNameFromJavaFile(className), selectedLeftRev.getValue(), updatedMethodBodyStms);
					new ReplaceMethodBodyAnalyzer(prjNameLeft, packageName, UTStr.getClassNameFromJavaFile(className), selectedLeftRev.getValue(), updatedMethodBodyStms2);
				}
			}
		});
	}

	private String getLabel(String aLabel) {
		if (aLabel.equals("VARIABLE_DECLARATION_STATEMENT") || aLabel.equals("ASSIGNMENT") || //
				aLabel.equals("METHOD_INVOCATION") || aLabel.equals("POSTFIX_EXPRESSION")) {
			return "";
		}
		if (aLabel.endsWith("_STATEMENT")) {
			aLabel = aLabel.replace("_STATEMENT", " ");
		} else if (aLabel.endsWith("_CLAUSE")) {
			aLabel = aLabel.replace("_CLAUSE", " ");
		}
		return aLabel.toLowerCase();
	}
	
	private String removeParen(String str) {
		if (str.startsWith("(")) {
			str = str.substring(1, str.length() - 1);
		}
		return str;

	}
	
	private ASTNode getASTNode(Node node, ASTNode root) {
		UTASTNodeSearcher searcher = new UTASTNodeSearcher(node);
		return searcher.search(root);
	}
	
	/**
	 * Adds the menu to checkbox tree viewer.
	 */
	void addMenuToCheckboxTreeViewer() {
		Menu mnSimilarContext = new Menu(mTVSimilarContext.getTree());
		mTVSimilarContext.getTree().setMenu(mnSimilarContext);

		MenuItem mntmExpand = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmExpand.setText("Expand All");
		mntmExpand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] expand " + e);
				mTVSimilarContext.expandAll();
			}
		});

		MenuItem mntmCollapse = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmCollapse.setText("Collapse All");
		mntmCollapse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] collapse " + e);
				mTVSimilarContext.collapseAll();
			}
		});

		MenuItem mntmOpenCompare = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenCompare.setText("Open Compare");
		mntmOpenCompare.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						String f1117Name = f1117.getAbsolutePath();
						String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
						String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
						String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
						File f1114 = new File(f1114Name);
						UTCriticsEditor.openComparisonEditor(f1114, f1117);
					}
				}
			}
		});

		MenuItem mntmOpenEditorOldRev = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenEditorOldRev.setText("Open Editor (Old Rev.)");
		mntmOpenEditorOldRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						UTCriticsEditor.openEditor(f1117);
					}
				}
			}
		});

		MenuItem mntmOpenEditorNewRev = new MenuItem(mnSimilarContext, SWT.NONE);
		mntmOpenEditorNewRev.setText("Open Editor (New Rev.)");
		mntmOpenEditorNewRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selElem = mTVSimilarContext.getSelection();
				if (selElem instanceof TreeSelection) {
					TreeSelection selTreeNode = (TreeSelection) selElem;
					Object selObj = selTreeNode.getFirstElement();
					if (selObj instanceof CriticsCBTreeNode) {
						CriticsCBTreeNode cbtNode = (CriticsCBTreeNode) selObj;
						File f1117 = cbtNode.getFile();
						String f1117Name = f1117.getAbsolutePath();
						String prjNameRight = UTCriticsPairFileInfo.getRightProjectName();
						String prjNameLeft = UTCriticsPairFileInfo.getLeftProjectName();
						String f1114Name = f1117Name.replace(prjNameRight, prjNameLeft);
						File f1114 = new File(f1114Name);
						UTCriticsEditor.openEditor(f1114);
					}
				}
			}
		});
	}

	/**
	 * Adds the menu to browser.
	 */
	void addMenuToBrowser() {
		// Menu menu = new Menu(mRightBrowser);
		// mRightBrowser.setMenu(menu);
		// MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		// menuItem.setText("Refresh");
		// MenuItem menuItem_1 = new MenuItem(menu, SWT.NONE);
		// menuItem_1.setText("Open");
		// MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		// menuItem_2.setText("Save");
		Menu mnBrowser = new Menu(mLeftBrowser);
		mLeftBrowser.setMenu(mnBrowser);
		mRightBrowser.setMenu(mnBrowser);

		MenuItem mntmRefresh = new MenuItem(mnBrowser, SWT.NONE);
		mntmRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String txtSearch = mTxtSearch.getText();
				if (txtSearch.equals("t1") || txtSearch.equals("test1")) {
					mHTMLRightBrowser.sampleOverlay();
					mHTMLLeftBrowser.sampleOverlay();
				} else {
					List<Object> arSelected = mCBTreeHelper.getCheckedElements();
					mHTMLRightBrowser.drawBrowser(arSelected);
					mHTMLLeftBrowser.drawBrowser(arSelected);
				}
			}
		});
		mntmRefresh.setText("Refresh");

		MenuItem mntmOpen = new MenuItem(mnBrowser, SWT.NONE);
		mntmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] open " + e);
			}
		});
		mntmOpen.setText("Open");

		MenuItem mntmSave = new MenuItem(mnBrowser, SWT.NONE);
		mntmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("[DBG] save " + e);
			}
		});
		mntmSave.setText("Save");
	}

}
