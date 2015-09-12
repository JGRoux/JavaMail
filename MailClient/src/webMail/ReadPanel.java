package webMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ReadPanel extends JPanel implements ActionListener {

	private JPanel paneInfos, paneInfosLeft, paneInfosRight;
	private JLabel subjectLbl, senderLbl, dateLbl, subject, sender, date, toLbl, priorityLbl;
	private JButton btnReply, btnForward, btnAttach, btnAttached, btnSend, btnDelete, btnSave;
	private ImageIcon attachIcon;
	private JScrollPane paneRead;
	private JTextArea textArea;
	private JTextField subjectFld, toFld;
	private File file;
	private byte[] bytes;
	private JComboBox<String> comboBox;
	private String priority = "N";
	private Table tables[];
	private LoginInfos login;
	private Mail mail;

	public ReadPanel() {
		this.subjectLbl = new JLabel("Subject:");

		this.paneInfos = new JPanel();
		this.paneInfos.setLayout(new BorderLayout());

		this.paneInfosLeft = new JPanel();
		this.paneInfosLeft.setLayout(new GridBagLayout());

		this.paneInfosRight = new JPanel();
		this.paneInfosRight.setLayout(new GridBagLayout());

		this.paneInfos.add(this.paneInfosLeft, BorderLayout.WEST);
		this.paneInfos.add(this.paneInfosRight, BorderLayout.EAST);
		this.paneInfos.setPreferredSize(new Dimension(200, 80));

		this.btnAttached = new JButton();
		this.btnAttached.setForeground(Color.blue);
		this.btnAttached.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.btnAttached.setFocusPainted(false);
		this.btnAttached.setMargin(new Insets(0, 0, 0, 0));
		this.btnAttached.setContentAreaFilled(false);
		this.btnAttached.setBorderPainted(false);
		this.btnAttached.setOpaque(false);
		this.btnAttached.addActionListener(this);

		this.attachIcon = new ImageIcon(this.getClass().getResource("/images/attach.png"));
		this.btnAttach = new JButton("Attach", this.attachIcon);
		this.btnAttach.addActionListener(this);

		this.textArea = new JTextArea(10, 10);
		this.textArea.setFont(new Font("SansSerif", Font.BOLD, 14));
		this.paneRead = new JScrollPane(this.textArea);

		this.setLayout(new BorderLayout());
		this.add(paneInfos, BorderLayout.NORTH);
		this.add(paneRead, BorderLayout.CENTER);
	};

	public ReadPanel(LoginInfos login, Table tables[]) {
		// Call the default constructor
		this();
		this.login = login;
		this.tables = tables;

		this.toLbl = new JLabel("Recipient:");
		this.priorityLbl = new JLabel("Priority:");

		ImageIcon sendIcon = new ImageIcon(this.getClass().getResource("/images/forward.png"));
		this.btnSend = new JButton("Send", sendIcon);
		this.btnSend.addActionListener(this);

		ImageIcon saveIcon = new ImageIcon(this.getClass().getResource("/images/save.png"));
		this.btnSave = new JButton("Save", saveIcon);
		this.btnSave.addActionListener(this);

		this.toFld = new JTextField(20);
		this.subjectFld = new JTextField(20);

		// Priority comboBox with custom renderer
		String[] items = { "None", "Low", "Medium", "High" };
		this.comboBox = new JComboBox<String>(items);
		this.comboBox.addActionListener(this);
		this.comboBox.setPreferredSize(new Dimension(223, 20));
		this.comboBox.setRenderer(new MyComboRenderer());

		if (this.file != null) {
			this.btnAttached.setIcon(this.attachIcon);
			this.btnAttached.setText("<HTML><U>" + this.file.getName() + "<U><HTML>");
		}

		// Place in the GridBagLayout
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);
		this.paneInfosRight.add(this.btnSend, c);
		c.gridx = 1;
		this.paneInfosRight.add(this.btnAttach, c);
		c.gridx = 2;
		this.paneInfosRight.add(this.btnSave, c);
		c.gridx = 0;
		c.gridy = 1;
		this.paneInfosRight.add(Box.createHorizontalGlue(), c);
		c.gridy = 2;
		this.paneInfosRight.add(Box.createHorizontalGlue(), c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosRight.add(this.btnAttached, c);
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosLeft.add(this.toLbl, c);
		c.gridx = 1;
		this.paneInfosLeft.add(this.toFld, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 10, 0, 0);
		this.paneInfosLeft.add(this.subjectLbl, c);
		c.gridx = 1;
		this.paneInfosLeft.add(this.subjectFld, c);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 0, 0, 0);
		this.paneInfosLeft.add(this.priorityLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 3;
		c.insets = new Insets(5, 0, 0, 0);
		this.paneInfosLeft.add(this.comboBox, c);
	};

	public ReadPanel(Mail mail, LoginInfos login, Table tables[], int cas) {

		// Call the default constructor
		this();

		this.mail = mail;
		this.login = login;
		this.tables = tables;
		this.file = this.mail.getFile();
		this.bytes = this.mail.getBytes();

		this.senderLbl = new JLabel("From:");
		this.dateLbl = new JLabel("Date:");

		ImageIcon replyIcon = new ImageIcon(this.getClass().getResource("/images/reply.png"));
		this.btnReply = new JButton("Reply", replyIcon);
		this.btnReply.addActionListener(this);

		ImageIcon forwardIcon = new ImageIcon(this.getClass().getResource("/images/forward.png"));
		this.btnForward = new JButton("Forward", forwardIcon);
		this.btnForward.addActionListener(this);

		if (cas == 0) {
			ImageIcon deleteIcon = new ImageIcon(this.getClass().getResource("/images/delete.png"));
			this.btnDelete = new JButton("Delete", deleteIcon);
			this.btnDelete.addActionListener(this);
		}

		if (this.file != null) {
			this.btnAttached.setIcon(this.attachIcon);
			this.btnAttached.setText("<HTML><U>" + this.file.getName() + "<U><HTML>");
		}

		this.textArea.setText(this.mail.getContent());
		this.textArea.setEditable(false);
		this.subject = new JLabel(this.mail.getSubject());
		this.sender = new JLabel(this.mail.getSender());
		this.date = new JLabel(this.mail.getStringDate());

		// Place in the GridBagLayout
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 5);
		this.paneInfosRight.add(this.btnReply, c);
		c.gridx = 1;
		this.paneInfosRight.add(this.btnForward, c);
		c.gridx = 2;
		if (cas == 0)
			this.paneInfosRight.add(this.btnDelete, c);
		c.gridx = 0;
		c.gridy = 1;
		this.paneInfosRight.add(Box.createHorizontalGlue(), c);
		c.gridy = 2;
		this.paneInfosRight.add(Box.createHorizontalGlue(), c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosRight.add(this.btnAttached, c);
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosLeft.add(this.subjectLbl, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		this.paneInfosLeft.add(this.subject, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosLeft.add(this.senderLbl, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		this.paneInfosLeft.add(this.sender, c);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 10, 0, 0);
		c.anchor = GridBagConstraints.LINE_END;
		this.paneInfosLeft.add(this.dateLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		this.paneInfosLeft.add(this.date, c);
	};

	// Constructor for replying and forwarding
	public ReadPanel(String priority, File file, String subject, String content, String sender, LoginInfos login, Table tables[]) {
		// Call the write constructor
		this(login, tables);

		this.priority = priority;
		this.file = file;
		this.subjectFld.setText(subject);
		this.textArea.setText(content);
		this.toFld.setText(sender);
		int i = 0;
		if (priority.equals("L"))
			i = 1;
		else if (priority.equals("M"))
			i = 2;
		else if (priority.equals("H"))
			i = 3;
		this.comboBox.setSelectedIndex(i);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btnReply)) {
			// New reply mail
			new WriteMail(this.mail.getPriority(), this.mail.getFile(), "Re: " + this.mail.getSubject(), this.mail.getContent(), this.mail.getSender(), this.login, this.tables);
		} else if (e.getSource().equals(this.btnForward)) {
			// New forward mail
			new WriteMail(this.mail.getPriority(), this.mail.getFile(), "For: " + this.mail.getSubject(), this.mail.getContent(), null, this.login, this.tables);
		} else if (e.getSource().equals(this.btnDelete)) {
			int result = JOptionPane.showConfirmDialog(null, "Do you want to delete \"" + this.mail.getSubject() + "\" ?", "Do you want to delete ?", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// Very very very very bad ! I know...but easier...
				Table table = (Table) ((JButton) e.getSource()).getParent().getParent().getParent().getParent().getComponent(1);
				Connection connection = new Connection(this.login);
				connection.delteteMail(this.mail.getID(), table.getTableFolder());
				connection.close();
				if (connection.getStatus().equals("Done")) {
					File file = new File("./storage/" + this.login.getUsername() + "/" + table.getTableFolder() + "/" + this.mail.getID() + ".mail");
					if (table.getTableFolder().equals("inbox"))
						file.renameTo(new File("./storage/" + this.login.getUsername() + "/trash/" + this.mail.getID() + ".mail"));
					else if ((table.getTableFolder().equals("drafts")) || (table.getTableFolder().equals("sent"))
							|| (table.getTableFolder().equals("trash")))
						file.delete();
					table.removeRow();// Delete the row
				} else
					new Error(connection.getStatus());
			}
		} else if (e.getSource().equals(this.btnAttach)) {
			// File chooser
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.file = chooser.getSelectedFile();
				try {
					this.bytes = new byte[(int) this.file.length()];
					FileInputStream input = new FileInputStream(this.file);
					input.read(this.bytes);
					input.close();
					// Easier to use html to underline
					this.btnAttached.setIcon(this.attachIcon);
					this.btnAttached.setText("<HTML><U>" + this.file.getName() + "<U><HTML>");
				} catch (Exception e1) {
					new Error("Failed to attach file " + this.file.getName());
				}
			}
		} else if (e.getSource().equals(this.btnSend)) {
			if (this.toFld.getText().indexOf("@") != -1) {
				// Send the mail
				Mail mail = new Mail(this.priority, this.file, this.subjectFld.getText(), this.textArea.getText(), this.login.getUsername() + "@"
						+ this.login.getServer(), this.toFld.getText());
				mail.setBytes(bytes);
				Connection connection = new Connection(this.login);
				connection.sendMail(mail);
				connection.close();
				if (connection.getStatus().equals("Sent")) { // Close the JFrame
					mail.setRead(true);
					new SaveMail(mail, this.login.getUsername(), "sent");
					((TableModel) this.tables[2].getTable().getModel()).addRow(mail);
					((JFrame) this.getTopLevelAncestor()).dispose();
				} else
					new Error(connection.getStatus());
			} else
				new Error("Verify the email address info");
		} else if (e.getSource().equals(comboBox)) {
			// Get the priority from the comboBox
			this.priority = ((String) comboBox.getSelectedItem()).substring(0, 1);
		} else if (e.getSource().equals(btnSave)) {
			// Save mail to drafts
			Mail mail = new Mail(this.priority, this.file, this.subjectFld.getText(), this.textArea.getText(), this.login.getUsername() + "@"
					+ this.login.getServer(), this.toFld.getText());
			mail.setRead(true);
			mail.setBytes(bytes);
			Connection connection = new Connection(this.login);
			connection.saveMail(mail);
			connection.close();
			if (connection.getStatus().equals("Sent")) { // Close the JFrame
				new SaveMail(mail, this.login.getUsername(), "drafts");
				((TableModel) this.tables[1].getTable().getModel()).addRow(mail);
				((JFrame) this.getTopLevelAncestor()).dispose();
			} else
				new Error(connection.getStatus());
		} else if (e.getSource().equals(btnAttached)) {
			// File chooser to save attached file
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					FileOutputStream output = new FileOutputStream(new File(chooser.getSelectedFile().getPath() + "\\" + this.file.getName()));
					// Double anti-slash because of the effect of the anti-slash
					output.write(this.bytes);
					output.flush();
					output.close();
				} catch (IOException e1) {
					new Error("Failed to save file " + this.file.getName());
				}
			}
		}
	}

	// If the user has started writing
	public boolean isWriting() {
		if ((this.toFld.getText().equals("")) && (this.subjectFld.getText().equals("")) && (this.textArea.getText().equals(""))
				&& (this.file == null))
			return false;
		else
			return true;
	}

	// Rewrite a renderer for the comboBox to display JLabel (icon+text)
	public class MyComboRenderer extends DefaultListCellRenderer {

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setOpaque(true);
			label.setText(value.toString());
			if (!value.equals("None"))
				label.setIcon(new ImageIcon(this.getClass().getResource("/images/" + value.toString().toLowerCase() + ".png")));
			return label;
		}

	}
}
