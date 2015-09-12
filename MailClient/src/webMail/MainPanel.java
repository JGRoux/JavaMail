package webMail;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	private JTabbedPane myTabbedPane;
	private Table tables[];
	private Tree paneLeft;
	private JPanel paneRight;
	private ReadPanel paneBtm;
	private JSplitPane splitPane1, splitPane2;

	public MainPanel(JTabbedPane myTabbedPane, Table tables[], LoginInfos login, Connection connection) {
		this.myTabbedPane = myTabbedPane;
		this.tables = tables;
		this.paneBtm = new ReadPanel();

		// SplitPane between the table and the read area
		this.splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.splitPane1.setDividerLocation(200);

		// Load local mails
		ArrayList<ArrayList<Mail>> mails = new LoadMails(login, null);
		
		String folders[]={"inbox","drafts","sent","trash"};
		// Create tables for inbox, drafts, sent and trash
		for (int i = 0; i < mails.size(); i++) {
			if ((i == 0) || (i == mails.size() - 1))
				this.tables[i] = new Table(this.myTabbedPane, this.splitPane1, login, "Sender", folders[i], this.tables);
			else
				this.tables[i] = new Table(this.myTabbedPane, this.splitPane1, login, "Recipient", folders[i], this.tables);
			((TableModel) this.tables[i].getTable().getModel()).setData(mails.get(i));
		}

		// Asking for new mails
		AutomaticGetMails autoGet=new AutomaticGetMails(login, this.tables, connection);	
		autoGet.start();
		
		// Trick (set the components after) to send the tabbedPane into the
		// table call
		this.splitPane1.setTopComponent(this.tables[0]); // Set default inbox table
		this.splitPane1.setBottomComponent(paneBtm);

		this.paneLeft = new Tree(login.getUsername() + "@" + login.getServer(), this.tables, this.splitPane1);
		this.paneLeft.setMinimumSize(new Dimension(150,150));
		this.paneRight = new JPanel();
		this.paneRight.setLayout(new BorderLayout());
		this.paneRight.add(splitPane1, BorderLayout.CENTER);

		// SplitPane between the tree and the mails area
		this.splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paneLeft, paneRight);
		this.splitPane2.setDividerLocation(150);

		this.setLayout(new BorderLayout());
		this.add(splitPane2);
	};
}
