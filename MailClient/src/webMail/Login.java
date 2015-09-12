package webMail;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class Login extends JPanel {

	private JLabel emailLbl, pwdLbl, newLbl;
	private JTextField emailFld;
	private JFrame frame;
	private JPasswordField pwdFld;
	private JDialog dialog;
	private JOptionPane optionPane;
	private ImageIcon icon;

	public Login() {
		this.setLayout(new GridLayout(3, 2));

		// Labels for the textfield components
		this.emailLbl = new JLabel("Email:");
		this.pwdLbl = new JLabel("Password:");

		this.emailFld = new JTextField();
		this.pwdFld = new JPasswordField();

		// Easier to use html to underline
		this.newLbl = new JLabel("<HTML><U>Create a new account<U><HTML>");
		this.newLbl.setForeground(Color.blue);
		this.newLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// New listener to click on the newLbl
		this.newLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				dialog.dispose();
				new NewAccount();
			}
		});

		// Add the components to the JPanel
		this.add(this.emailLbl);
		this.add(this.emailFld);
		this.add(this.pwdLbl);
		this.add(this.pwdFld);
		this.add(this.newLbl);

		// As the JOptionPane accepts an object as the message
		// it allows us to use any component we like - in this case
		// a JPanel containing the dialog components we want
		Object[] options = { "Sign in" };
		this.optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null, options, options[0]);
		this.dialog = new JDialog(this.frame, "Login", true);
		this.dialog.setResizable(false);
		// Change the "sign in" button size
		JButton buttonSign = (JButton) ((JPanel) optionPane.getComponent(1)).getComponent(0);
		buttonSign.setPreferredSize(new Dimension(245, 30));

		// Set window icon
		this.icon = new ImageIcon(this.getClass().getResource("/images/webmail.png"));
		this.dialog.setIconImage(this.icon.getImage());

		this.dialog.setContentPane(optionPane);
		// Close properly (EXIT_ON_CLOSE is not possible with JDialog and
		// DISPOSE_ON_CLOSE does not close the JVM)
		this.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				// Check if it's the correct dialog box
				if (dialog.isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY))) {

					// Get the clicked button. If it's "Sign In" check email and
					// pwd
					String select = ((String) optionPane.getValue());
					if (select == "Sign in") {
						String email = new String(emailFld.getText());
						if (email.indexOf("@") != -1) {
							String username = email.substring(0, email.indexOf("@"));
							String server = email.substring(email.indexOf("@") + 1, email.length());
							String pwd = new String(pwdFld.getPassword());
							if (!pwd.equals("")) {
								LoginInfos login = new LoginInfos(username, pwd, server);
								Connection connection = new Connection(login);
								if (connection.getStatus() != null)
									if (connection.getStatus().equalsIgnoreCase("Granted")) {
										new UserFolders(login);
										dialog.dispose();
										new Interface(login, connection);
									} else
										new Error(connection.getStatus());
							}
							else
								new Error("Please enter a password");
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

	public static void main(String args[]) {
		new Login();
	}
}