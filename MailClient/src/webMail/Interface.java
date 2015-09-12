package webMail;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class Interface extends JFrame {

	private ImageIcon icon, inboxIcon;
	private JTabbedPane myTabbedPane;
	private JPanel mainPanel;

	public Interface(LoginInfos login, Connection connection) {
		super("JavaMail");

		// Set icons
		this.icon = new ImageIcon(this.getClass().getResource("/images/webmail.png"));
		this.setIconImage(this.icon.getImage());
		this.inboxIcon = new ImageIcon(this.getClass().getResource("/images/inbox.png"));

		// Verify if no mail is being writing before closing
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Window[] windows = Window.getWindows();
				for (int i = 0; i < windows.length; i++) {
					if ((windows[i].getClass() == WriteMail.class) && (windows[i].isVisible())) {
						int result = JOptionPane.showConfirmDialog(null, "A new mail is being created. Are you sure you want to quit ?", "Do you want to quit ?", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							break;
						} else
							return;
					}
				}
				dispose();
				System.exit(0);
			}
		});
		Table tables[] = new Table[4];

		this.myTabbedPane = new JTabbedPane();
		this.mainPanel = new MainPanel(this.myTabbedPane, tables, login, connection);

		this.myTabbedPane.addTab("Incoming", this.inboxIcon, mainPanel, "Incoming");

		this.getContentPane().add(new Toolbar(login, tables), BorderLayout.NORTH);
		this.getContentPane().add(this.myTabbedPane, BorderLayout.CENTER);
		

		this.setMinimumSize(new Dimension(650,300));
		this.setSize(800, 600);
		this.setLocationRelativeTo(null); // Center on the screen
		this.setVisible(true);
	}
}