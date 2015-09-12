package webMail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class Toolbar extends JToolBar implements ActionListener {
	private JButton btnGet, btnWrite, btnAddress;
	private LoginInfos login;
	private Table tables[];

	public Toolbar(LoginInfos login, Table tables[]) {
		this.login=login;
		this.tables=tables;
		this.btnGet = new JButton("Get Mails");
		this.btnGet.addActionListener(this);
		this.btnWrite = new JButton("Write Mail");
		this.btnWrite.addActionListener(this);
		this.btnAddress = new JButton("Address Book");
		this.btnAddress.addActionListener(this);
		this.add(btnGet);
		this.add(btnWrite);
		this.add(btnAddress);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btnWrite)) {
			new WriteMail(this.login, this.tables);
		}else if(e.getSource().equals(this.btnGet)){
			Connection connection = new Connection(this.login);
			connection.getMails(this.tables);
			connection.close();
			if (!connection.getStatus().equals("Received"))
				new Error("Connexion problem");
		}else if(e.getSource().equals(this.btnAddress)){
				new Error("Sorry this fonction is not available yet...");
		}
	}
}
