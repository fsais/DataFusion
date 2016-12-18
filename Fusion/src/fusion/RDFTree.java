package fusion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Display a file system in a JTree view
 * 
 * @version $Id: RDFTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
 * @author Ian Darwin
 */
public class RDFTree extends JPanel {
	/** Construct a RDFTree */

	private static JTree tree;
	private static String prefix = "http://localhost/rdf/";


	public RDFTree(Model fusionGraph, Model graph) {
		setLayout(new BorderLayout());

		// Make a tree list with all the nodes, and make it a JTree
		tree = new JTree(createTreeBis(fusionGraph, graph)); //ou createTree
		
		// Property in bold
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {

				Pattern reg = Pattern.compile("^[^\\:]+\\:");
				String text = value.toString();

				StringBuffer html = new StringBuffer("<html>");
				Matcher m = reg.matcher(text);
				while (m.find())
					m.appendReplacement(html, "<b>" + m.group() + "</b>");
				m.appendTail(html).append("</html>");

				return super.getTreeCellRendererComponent(
						tree, html.toString(), sel, expanded, leaf, row, hasFocus);
			}
		});
		// Add a listener
		/*
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				//DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
					//	.getPath().getLastPathComponent();
				//System.out.println("You selected " + node);
			}
		});
		 */
	}

	// from Model to RDFTree
	private DefaultMutableTreeNode createTree(Model model){  // A commenter ... //forte complexite
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("saSets");
		StmtIterator iterSaset = model.listStatements(null, model.getProperty(prefix + "hasSameAsSet"), (RDFNode) null);
		while(iterSaset.hasNext()){
			Statement stmtSaset = iterSaset.nextStatement();
			Resource saSet = ((Resource) stmtSaset.getObject());
			DefaultMutableTreeNode saSetNode = new DefaultMutableTreeNode(saSet.getURI().replaceAll(prefix, ""));
			root.add(saSetNode);

			StmtIterator iterProp = model.listStatements(saSet, null, (RDFNode) null);
			while(iterProp.hasNext()){
				Statement stmtProp = iterProp.nextStatement();
				com.hp.hpl.jena.rdf.model.Property prop = (com.hp.hpl.jena.rdf.model.Property) stmtProp.getPredicate();
				DefaultMutableTreeNode propNode = new DefaultMutableTreeNode(prop.getURI().replaceAll(prefix, ""));
				saSetNode.add(propNode);

				StmtIterator iterValue = model.listStatements(saSet, prop, (RDFNode) null);
				while(iterValue.hasNext()){
					Statement stmtValue = iterValue.nextStatement();
					Resource value = (Resource) stmtValue.getObject();
					DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(value.getURI().replaceAll(prefix, ""));
					propNode.add(valueNode);

					StmtIterator iterAnnot = model.listStatements(value, null, (RDFNode) null);
					while(iterAnnot.hasNext()){
						Statement stmtAnnot = iterAnnot.nextStatement();
						DefaultMutableTreeNode annotNode = new DefaultMutableTreeNode(stmtAnnot.getPredicate().getURI().replaceAll(prefix, ""));
						valueNode.add(annotNode);
						annotNode.add(new DefaultMutableTreeNode(stmtAnnot.getObject()));
					}
				}			

			}

		}
		return root;
	}

	private DefaultMutableTreeNode createTreeBis(Model fusionGraph, Model graph){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("saSets");
		StmtIterator iterSaset = fusionGraph.listStatements(null, fusionGraph.getProperty(prefix + "hasSameAsSet"), (RDFNode) null);
		while(iterSaset.hasNext()){
			Statement stmtSaset = iterSaset.nextStatement();
			Resource saSet = ((Resource) stmtSaset.getObject());
			DefaultMutableTreeNode saSetNode = new DefaultMutableTreeNode(saSet.getURI().replaceAll(prefix, ""));
			root.add(saSetNode);

			StmtIterator iterValue = fusionGraph.listStatements(saSet, null, (RDFNode) null);
			while(iterValue.hasNext()){
				Statement stmtValue = iterValue.nextStatement();

				com.hp.hpl.jena.rdf.model.Property prop = (com.hp.hpl.jena.rdf.model.Property) stmtValue.getPredicate();
				Resource value = (Resource) stmtValue.getObject();

				StmtIterator i = fusionGraph.listStatements(value, fusionGraph.getProperty(prefix + "hasValue"), (RDFNode) null);
				Statement v = i.nextStatement();
				String valName = v.getObject().toString();

				String st = propertyString(prop.getURI().replaceAll(prefix, "")) + ": " + valName;
				DefaultMutableTreeNode propNode = new DefaultMutableTreeNode(st);
				saSetNode.add(propNode);

				// Not selected values
				DefaultMutableTreeNode otherValsNode = new DefaultMutableTreeNode("Not selected values");
				propNode.add(otherValsNode);
				Extraction extract = new Extraction(graph);
				Model otherVals = extract.getProperty(prop.getURI().replaceAll(prefix, ""), saSet.getURI().replaceAll(prefix, ""));

				StmtIterator iterValueO = otherVals.listStatements(saSet, null, (RDFNode) null);
				while(iterValueO.hasNext()){
					Statement stmtValueO = iterValueO.nextStatement();

					com.hp.hpl.jena.rdf.model.Property propO = (com.hp.hpl.jena.rdf.model.Property) stmtValueO.getPredicate();
					Resource valueO = (Resource) stmtValueO.getObject();

					StmtIterator iO = otherVals.listStatements(valueO, otherVals.getProperty(prefix + "hasValue"), (RDFNode) null);
					Statement vO = iO.nextStatement();
					String valNameO = vO.getObject().toString(); //
					if(! valNameO.equals(valName)){

						String stO = propertyString(propO.getURI().replaceAll(prefix, "")) + ": " + valNameO;
						DefaultMutableTreeNode propNodeO = new DefaultMutableTreeNode(stO);
						otherValsNode.add(propNodeO);

						StmtIterator iterAnnotO = otherVals.listStatements(valueO, null, (RDFNode) null);
						while(iterAnnotO.hasNext()){
							Statement stmtAnnotO = iterAnnotO.nextStatement();
							String pNameO = propertyString(stmtAnnotO.getPredicate().getURI().replaceAll(prefix, ""));
							String vNameO = stmtAnnotO.getObject().toString();
							DefaultMutableTreeNode annotNodeO = new DefaultMutableTreeNode(pNameO + ": " + vNameO);
							propNodeO.add(annotNodeO);
						}	
					}
				}
				///////////////

				/*// More preciseThan values
				DefaultMutableTreeNode lessPreciseValsNode = new DefaultMutableTreeNode("Less precise values ");
				propNode.add(otherValsNode);
				Extraction extract2 = new Extraction(graph);
				Model otherValsNode = extract.getProperty(prop.getURI().replaceAll(prefix, ""), saSet.getURI().replaceAll(prefix, ""));

				StmtIterator iterValue1 = otherValsNode.listStatements(saSet, null, (RDFNode) null);
				while(iterValue1.hasNext()){
					Statement stmtValueO = iterValueO.nextStatement();

					com.hp.hpl.jena.rdf.model.Property propO = (com.hp.hpl.jena.rdf.model.Property) stmtValueO.getPredicate();
					Resource valueO = (Resource) stmtValueO.getObject();

					StmtIterator iO = otherVals.listStatements(valueO, otherVals.getProperty(prefix + "hasValue"), (RDFNode) null);
					Statement vO = iO.nextStatement();
					String valNameO = vO.getObject().toString(); //
					
					
					if(! valNameO.equals(valName)){

						String stO = propertyString(propO.getURI().replaceAll(prefix, "")) + ": " + valNameO;
						DefaultMutableTreeNode propNodeO = new DefaultMutableTreeNode(stO);
						otherValsNode.add(propNodeO);

						StmtIterator iterAnnotO = otherVals.listStatements(valueO, null, (RDFNode) null);
						while(iterAnnotO.hasNext()){
							Statement stmtAnnotO = iterAnnotO.nextStatement();
							String pNameO = propertyString(stmtAnnotO.getPredicate().getURI().replaceAll(prefix, ""));
							String vNameO = stmtAnnotO.getObject().toString();
							DefaultMutableTreeNode annotNodeO = new DefaultMutableTreeNode(pNameO + ": " + vNameO);
							propNodeO.add(annotNodeO);
						}	
					}
				}
				///////////////
*/
				
				
				StmtIterator iterAnnot = fusionGraph.listStatements(value, null, (RDFNode) null);
				while(iterAnnot.hasNext()){
					Statement stmtAnnot = iterAnnot.nextStatement();
					String pName = propertyString(stmtAnnot.getPredicate().getURI().replaceAll(prefix, ""));
					String vName = stmtAnnot.getObject().toString();
					DefaultMutableTreeNode annotNode = new DefaultMutableTreeNode(pName + ": " + vName);
					propNode.add(annotNode);
				}
			}			

		}
		return root;
	}

	// example: from hasQualityValue to Has quality value
	private String propertyString(String s){
		/*String s2 = "";
		for(int i=0; i < s.length(); i++){
			Character c = s.charAt(i);
			if(Character.isUpperCase(c)){
				s2 = s2 + " " + Character.toLowerCase(c);
			}
			else{
				s2 = s2 + c;
			}
		}
		return s2.substring(0, 1).toUpperCase() + s2.substring(1);*/
		return s;
	}


	// return a JScrollPane with the tree
	public JScrollPane display(){

		//JFrame frame = new JFrame("RDFTree");
		//frame.setSize(600, 1200); 
		/*frame.setForeground(Color.black);
		frame.setBackground(Color.lightGray);*/
		//Container cp = frame.getContentPane();
		//cp.setSize(600, 1200);

		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setSize(600, 1200);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().add(tree);
		add(BorderLayout.CENTER, scrollPane);
		//cp.add(scrollPane, BorderLayout.CENTER);

		/*frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);*/
		return scrollPane;
	}
}