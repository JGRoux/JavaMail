package webMail;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class Tree extends JScrollPane {
	private JTree myTree;
	private Table tables[];
	private JSplitPane splitPane;

	public Tree(String email, Table tables[], JSplitPane splitPane) {
		this.tables=tables;
		this.splitPane=splitPane;
		
		// Navigation folder tree
		DefaultMutableTreeNode user = new DefaultMutableTreeNode(email);
		DefaultMutableTreeNode incoming = new DefaultMutableTreeNode("Inbox");
		DefaultMutableTreeNode draft = new DefaultMutableTreeNode("Drafts");
		DefaultMutableTreeNode sent = new DefaultMutableTreeNode("Sent");
		DefaultMutableTreeNode deleted = new DefaultMutableTreeNode("Trash");
		this.myTree = new JTree(user);

		user.add(incoming);
		user.add(draft);
		user.add(sent);
		user.add(deleted);
		this.myTree.expandRow(0); // Expand the tree
		this.myTree.setSelectionRow(1);
		this.setViewportView(this.myTree);
		
		this.myTree.addMouseListener(new treeListener());
	};
	
	public class treeListener extends MouseAdapter{
		
		    public void mousePressed(MouseEvent e) {
		        int i = myTree.getRowForLocation(e.getX(), e.getY());
		        if(i>0)
		        splitPane.setTopComponent(tables[i-1]);
		        splitPane.setDividerLocation(200);
		        splitPane.revalidate();
		}
	}
}
