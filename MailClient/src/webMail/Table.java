package webMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

@SuppressWarnings("serial")
public class Table extends JScrollPane {

	private JTable myTable;
	private TableModel myModel;
	private JTabbedPane myTabbedPane;
	private ImageIcon mailIcon, closeIcon;
	private JSplitPane splitPane;
	private String tableFolder;
	private Table tables[];
	private LoginInfos login;
	private int row;

	public Table(JTabbedPane myTabbedPane, JSplitPane splitPane, LoginInfos login, String type, String tableFolder, Table tables[]) {
		this.myTabbedPane = myTabbedPane;
		this.splitPane = splitPane;
		this.login = login;
		this.tableFolder = tableFolder;
		this.tables=tables;

		this.mailIcon = new ImageIcon(this.getClass().getResource("/images/mail.png"));
		this.closeIcon = new ImageIcon(this.getClass().getResource("/images/close.png"));

		String[] columnNames = { "H", "Y", "Subject", type, "Date" };
		// Use rewriting model
		this.myModel = new TableModel(columnNames);
		this.myTable = new JTable(this.myModel);

		// Create custom row sorter for Priority column (H,M,L)
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.myModel);
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String s1, String s2) {
				int n[] = new int[2];
				String s[] = { s1, s2 };
				for (int i = 0; i < 2; i++) {
					if (s[i].equals("H"))
						n[i] = 3;
					else if (s[i].equals("M"))
						n[i] = 2;
					else if (s[i].equals("L"))
						n[i] = 1;
					else
						n[i] = 0;
				}
				return n[0] - n[1];
			}
		};
		sorter.setComparator(0, comparator);
		this.myTable.setRowSorter(sorter); // Enable sortering with custom
											// sorter
		if (!this.myModel.getData().isEmpty()) { // Set default sorter
			this.myTable.getRowSorter().toggleSortOrder(4); // by increasing dates
		}

		this.myTable.setDefaultRenderer(Object.class, new MyRenderer());
		// Custom rendering for columns 0 and 1 (icons) && edit col/row sizes
		for (int i = 0; i < 2; i++) {
			this.myTable.getColumnModel().getColumn(i).setHeaderRenderer(new MyRenderer());
			this.myTable.getColumnModel().getColumn(i).setMaxWidth(25);
		}
		this.myTable.getTableHeader().setReorderingAllowed(false);
		this.myTable.getColumnModel().getColumn(4).setMaxWidth(100);
		this.myTable.getColumnModel().getColumn(4).setMinWidth(100);
		this.myTable.getColumnModel().getColumn(2).setMinWidth(200);
		this.myTable.setRowHeight(20);
		this.myTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

		// No borders
		this.myTable.setIntercellSpacing(new Dimension(0, 0));
		this.myTable.setOpaque(false);
		// this.myTable.setRowSelectionAllowed(true);

		// Get motions
		this.myTable.addMouseMotionListener(new TableListener());
		// Get double click on table
		this.myTable.addMouseListener(new TableListener());
		this.setViewportView(this.myTable);
	}

	public String getTableFolder() {
		return this.tableFolder;
	}

	public JTable getTable() {
		return this.myTable;
	}

	public void removeRow() {
		int row = this.myTable.getSelectedRow();
		if (row >= 0) {
			this.myModel.removeRow(row);
			// this.repaint();

			if (row > 0) {
				// Show the previous mail
				Mail mail = this.myModel.getMailAtRow(row - 1);
				this.splitPane.setBottomComponent(new ReadPanel(mail, this.login, this.tables, 0));
				// Select the previous raw
				this.myTable.setRowSelectionInterval(row - 1, row - 1);
			} else if (row == 0 && this.myModel.getRowCount() > 0) {
				// Show the next mail
				Mail mail = this.myModel.getMailAtRow(row);
				this.splitPane.setBottomComponent(new ReadPanel(mail, this.login, this.tables, 0));
			} else
				this.splitPane.setBottomComponent(new ReadPanel()); // No mails
																	// anymore
			// Keep the divider at the position
			this.splitPane.setDividerLocation(this.splitPane.getDividerLocation());
		}
	}

	private class TableListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			// Get the mail at row
			row = myTable.rowAtPoint(e.getPoint());
			row = myTable.convertRowIndexToModel(row);
			// If the table is not empty
			if (row != -1) {
				Mail mail = myModel.getMailAtRow(row);

				// Simple click on table => open mail on the paneBottom
				splitPane.setBottomComponent(new ReadPanel(mail, login, tables, 0));
				// Keep the divider at the position
				splitPane.setDividerLocation(splitPane.getDividerLocation());

				// Double click on table => open mail in a new tab
				if (e.getClickCount() == 2) {

					// Add a tab
					myTabbedPane.addTab(mail.getSubject(), new ReadPanel(mail, login, tables, 1));
					// Focus on the new tab
					myTabbedPane.setSelectedIndex(myTabbedPane.getTabCount() - 1); //

					JPanel panel = new JPanel(new BorderLayout());
					panel.setOpaque(false);
					JLabel title = new JLabel(mail.getSubject());
					title.setIcon(mailIcon);
					JButton btnClose = new JButton(closeIcon);
					btnClose.setPreferredSize(new Dimension(20, 20));

					panel.add(title, BorderLayout.WEST);
					panel.add(btnClose, BorderLayout.EAST);

					myTabbedPane.setTabComponentAt(myTabbedPane.getTabCount() - 1, panel);

					btnClose.addActionListener(new MyCloseActionHandler(mail.getSubject()));
				}
			}
		}

		// Highlighting table when mouse move
		public void mouseMoved(MouseEvent e) {
			row = myTable.rowAtPoint(e.getPoint());
			myTable.repaint();
		}
	}

	private class MyRenderer extends DefaultTableCellRenderer {
		// Table modified renderer with icons

		private ImageIcon iconAttach = new ImageIcon(this.getClass().getResource("/images/attach.png"));
		private ImageIcon iconLow = new ImageIcon(this.getClass().getResource("/images/low.png"));
		private ImageIcon iconMed = new ImageIcon(this.getClass().getResource("/images/medium.png"));
		private ImageIcon iconHigh = new ImageIcon(this.getClass().getResource("/images/high.png"));

		public MyRenderer() {
			setOpaque(true);
		};

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowc, int column) {

			// Replace string by icons for priority and attached
			if (column == 0 || column == 1) {
				if (value.equals("Y"))
					this.setIcon(iconAttach);
				else if (value.equals("L"))
					this.setIcon(iconLow);
				else if (value.equals("M"))
					this.setIcon(iconMed);
				else if (value.equals("H"))
					this.setIcon(iconHigh);
				else {
					this.setIcon(null);
					this.setText("");
				}
			} else {
				this.setIcon(null);
				this.setText(value.toString());
			}

			// On select highlight the row
			if (isSelected) {
				if (!myModel.getMailAtRow(rowc).isRead()) {
					Connection connection = new Connection(login);
					connection.sendRead(myModel.getMailAtRow(rowc).getID());
					connection.close();
					if (connection.getStatus().equals("Done")) {
						myModel.getMailAtRow(rowc).setRead(true);
						new SaveMail(myModel.getMailAtRow(rowc), login.getUsername(), getTableFolder());
					} else
						new Error(connection.getStatus());

				}
				this.setBackground(table.getSelectionBackground());
				this.setForeground(table.getSelectionForeground());
			} else {
				// On mouse over (only if no selected row)
				if (row == rowc) {
					this.setBackground(new Color(230, 240, 250));
					this.setForeground(Color.black);
				} else {
					this.setBackground(table.getBackground());
					this.setForeground(table.getForeground());
				}
			}

			// Read/No read properties
			if (rowc != -1)
				if (!myModel.getMailAtRow(myTable.convertRowIndexToModel(rowc)).isRead())
					this.setFont(new Font("SansSerif", Font.BOLD, 12));
				else
					this.setFont(new Font("SansSerif", Font.PLAIN, 12));

			// TableHeader properties
			if (rowc == -1) {
				this.setBackground(table.getTableHeader().getBackground());
				this.setForeground(table.getTableHeader().getForeground());
				this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				if (column == 0)
					this.setToolTipText("Priority");
				else if (column == 1)
					this.setToolTipText("Attached");
			}
			return this;
		}
	}

	public class MyCloseActionHandler implements ActionListener {

		private String tabName;

		public MyCloseActionHandler(String tabName) {
			this.tabName = tabName;
		}

		public String getTabName() {
			return tabName;
		}

		public void actionPerformed(ActionEvent evt) {
			int index = myTabbedPane.indexOfTab(getTabName());
			myTabbedPane.removeTabAt(index);
		}
	}
}
