package webMail;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class NewAccount extends JPanel {

	private JFrame frame;
	private JLabel emailLbl, pwdLbl1, pwdLbl2;
	private JTextField emailFld;
	private JPasswordField pwdFld1, pwdFld2;
	private JOptionPane optionPane;
	private JDialog dialog;
	private ImageIcon icon;

	public NewAccount() {

		this.setLayout(new GridLayout(3, 2));

		// Labels for the textfields components
		this.emailLbl = new JLabel("Email:");
		this.pwdLbl1 = new JLabel("Password:");
		this.pwdLbl2 = new JLabel("Verify password:");

		// textfields components
		this.emailFld = new JTextField();
		this.pwdFld1 = new JPasswordField();
		this.pwdFld2 = new JPasswordField();

		// Add the components to the Jthis
		this.add(this.emailLbl);
		this.add(this.emailFld);
		this.add(this.pwdLbl1);
		this.add(this.pwdFld1);
		this.add(this.pwdLbl2);
		this.add(this.pwdFld2);

		// As the JOptionPane accepts an object as the message
		// it allows us to use any component we like - in this case
		// a JPanel containing the dialog components we want
		Object[] options = { "Sign up" };
		this.optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null, options, options[0]);
		this.dialog = new JDialog(this.frame, "New account", true);
		this.dialog.setResizable(false);
		// Change the sign in button size
		JButton buttonSign = (JButton) ((JPanel) optionPane.getComponent(1)).getComponent(0);
		buttonSign.setPreferredSize(new Dimension(245, 30));

		// Set window icon
		this.icon = new ImageIcon(this.getClass().getResource("/images/webmail.png"));
		this.dialog.setIconImage(icon.getImage());

		this.dialog.setContentPane(optionPane);
		// Close properly (EXIT_ON_CLOSE is not possible with JDialog and
		// DISPOSE_ON_CLOSE does not close the JVM)
		this.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dialog.dispose();
				new Login();
			}
		});

		this.optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				// Check if it's the correct dialog box (patch: if integer
				// because the setValue recall an event)
				if (dialog.isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {

					// Get the clicked button. If it's "Sign up" check email and
					// passwords
					String select = ((String) optionPane.getValue());
					if (select == "Sign up") {
						String email = new String(emailFld.getText());
						if (email.indexOf("@") != -1) {
							String username = email.substring(0, email.indexOf("@"));
							String server = email.substring(email.indexOf("@") + 1, email.length());

							String pwd1 = new String(pwdFld1.getPassword());
							String pwd2 = new String(pwdFld2.getPassword());
							if (pwd1.equals(pwd2)) {
								if (pwd1.compareTo("") != 0) {
									if (pwd1.length() > 6) {
										LoginInfos login = new LoginInfos(username, pwd1, server);
										login.setNewUser(true);
										Connection connection = new Connection(login);
										if (connection.getStatus() != null)
											if (connection.getStatus().equalsIgnoreCase("Created")) {
												new UserFolders(login);
												login.setNewUser(false);
												dialog.dispose();
												new Interface(login, connection);
											} else {
												new Error(connection.getStatus());
											}
									} else
										new Error("The password must be longer than 6 characters");
								} else
									new Error("Please enter a password");
							} else
								new Error("Please check your password");
						} else
							new Error("Please check your email format");
						// Reinitialize buttons clicks
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					}
				}
			}
		});

		this.dialog.pack();
		this.dialog.setLocationRelativeTo(null); // Center on the screen
		this.dialog.setVisible(true);
	};
}
