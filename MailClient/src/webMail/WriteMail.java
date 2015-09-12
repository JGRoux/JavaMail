package webMail;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class WriteMail extends JFrame {

	private ImageIcon icon;
	private ReadPanel myPanel;

	public void panel() {
		this.icon = new ImageIcon(this.getClass().getResource("/images/webmail.png"));
		this.setIconImage(this.icon.getImage());

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (myPanel.isWriting()) {
					int result = JOptionPane.showConfirmDialog(null, "Are you sure you don't want to finish writing this email ?", "Do you want to quit ?", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION)
						dispose();
				} else
					dispose();

			}
		});
		this.setMinimumSize(new Dimension(600,400));
		this.setSize(700, 500);
		this.setLocationRelativeTo(null); // Center on the screen
		this.setVisible(true);
	}

	// JFrame for a new mail
	public WriteMail(LoginInfos login, Table tables[]) {
		super("New mail");
		panel();
		this.myPanel = new ReadPanel(login, tables);
		this.getContentPane().add(this.myPanel, BorderLayout.CENTER);
	};

	// Replying and forwarding
	public WriteMail(String priority, File file, String subject, String content, String sender, LoginInfos login, Table tables[]) {
		super("New mail");
		panel();
		this.myPanel = new ReadPanel(priority, file, subject, content, sender, login, tables);
		this.getContentPane().add(this.myPanel, BorderLayout.CENTER);
	};
}
