package webMail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler extends Thread {
	private Socket clientSocket;
	private ObjectInputStream is;
	private ObjectOutputStream os;
	private LoadUsers loginArray;
	private LoginInfos login;
	private boolean keepOpen = true;
	private String request;
	public static Long ID;

	// Constructor
	public ConnectionHandler(Socket clientSocket, LoadUsers loginArray) {
		this.clientSocket = clientSocket;
		this.loginArray = loginArray;
	}

	// Thread execution
	public void run() {
		try {
			System.out.println("Starting new thread");
			this.is = new ObjectInputStream(clientSocket.getInputStream());
			this.os = new ObjectOutputStream(clientSocket.getOutputStream());
			while (this.keepOpen) {
				this.readCommand();
			}
			this.closeSocket();
		} catch (Exception e) {
			System.out.println("XX. There was a problem with the Input/Output Communication:");
			e.printStackTrace();
		}
		System.out.println("Finished thread");
	}

	// Create new user directories
	private void addUser(LoginInfos login) {
		File userDir = new File("./storage/" + login.getUsername());
		if (!userDir.exists()) {
			System.out.println("Creating directory: " + userDir.getName());
			if (userDir.mkdir()) {
				System.out.println("New directory " + userDir.getName() + " has been created");
				// Create necessary folders
				(new File("./storage/" + login.getUsername() + "/inbox")).mkdir();
				(new File("./storage/" + login.getUsername() + "/drafts")).mkdir();
				(new File("./storage/" + login.getUsername() + "/sent")).mkdir();
				(new File("./storage/" + login.getUsername() + "/trash")).mkdir();

				if (saveUser(login)) {
					this.send("Created");
					this.loginArray.add(login);
					System.out.println("New user " + login.getUsername() + " has been created");
				} else {
					System.out.println("Cannot save " + login.getUsername() + ". Please check read/write rights");
					this.sendError("Internal server problem");
				}
			} else {
				System.out.println("Cannot create " + userDir.getName() + ". Please check read/write rights");
				this.sendError("Internal server problem");
			}
		}
	}

	// Add user in file Users.pwd => synchronized because we don't want 2 thread
	// write in same time
	private synchronized boolean saveUser(LoginInfos login) {
		try {
			File file = new File("./storage/Users.pwd");
			FileOutputStream fileStream = new FileOutputStream(file, true);
			ObjectOutput output;
			// Depending on file exist (write header)
			// (Compare the number of bytes of the file because the file has
			// just been created => exists method return always true)
			if (file.length() == 0)
				output = new ObjectOutputStream(fileStream);
			else
				output = new AppendingObjectOutputStream(fileStream);

			output.writeObject(login);
			output.flush();
			output.close();
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Write in file ID => synchronized because we don't want 2 thread write in
	// same time (one ID for each mail)
	private synchronized void writeID() {
		try {
			File file = new File("./storage/ID.id");
			FileOutputStream fileStream = new FileOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(fileStream);

			output.writeObject(ID);
			output.flush();
			output.close();
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Receive and process incoming requests from client
	private void readCommand() {
		Object obj = null;

		try {
			obj = is.readObject();
		} catch (Exception e) {
			System.out.println(e); // Client has disconnected
			this.keepOpen = false;
			return;
		}
		if (this.login != null)
			System.out.println("01. <- Received an object from the client " + this.login.getUsername() + "@"
					+ this.clientSocket.getInetAddress().getHostAddress());
		else
			System.out.println("01. <- Received an object from a new client.");

		// Client is asking for granted user or create user
		if (obj.getClass() == LoginInfos.class) {
			this.login = (LoginInfos) obj;
			// If client ask for new user/existing user
			if (this.login.isNewUser()) {
				System.out.println("Client " + login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
						+ " is asking for create a new account");
				// Seek if the username is not already used
				if (this.loginArray.containsUsername(this.login.getUsername()) != null)
					this.sendError("This email is already used");
				else {
					this.addUser(this.login); // Add the user
					if (this.loginArray.size() <= 4) { // Assign a LED if all the LED are not allocated
						this.login.setLED(new TurnLED());
						this.login.getLED().start();
					}
				}
			} else {
				// If user exist => check login => grant
				if ((this.login = this.loginArray.contains(this.login)) != null) {
					// + this.login temp change to loginArray instance
					this.send("Granted");
					System.out.println("Client " + this.login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
							+ " has been granted");
					// Set thread name for debugging
					Thread.currentThread().setName("Thread for " + this.login.getUsername());

					// Set LED for user on
					if (this.login.getLED() != null)
						this.login.getLED().turn("On", 1);

				} else
					this.sendError("Please check your login infos");
			}

		} else if (obj.getClass() == Mail.class) {
			// Mail object is send from the client
			Mail mail = (Mail) obj;
			System.out.println("Client " + this.login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
					+ " is sending a new mail");
			if ((this.request != null) && (this.request.equals("Save"))) {
				mail.setID(ID++);
				this.writeID();
				// Save mail in the mailbox
				new SaveMail(mail, this.login.getUsername(), "drafts");
				this.send(mail.getID());
			} else {
				String recipient = mail.getRecipient();
				String username = recipient.substring(0, recipient.indexOf("@"));
				String server = recipient.substring(recipient.indexOf("@") + 1, recipient.length());
				LoginInfos recipientLogin;
				if ((server.equals("mail.com")) && ((recipientLogin = this.loginArray.containsUsername(username)) != null)) {
					mail.setID(ID++);
					this.writeID();
					// Save mail in the mailbox
					new SaveMail(mail, username, "inbox");
					String sender = mail.getSender();
					mail.setRead(true);
					new SaveMail(mail, sender.substring(0, sender.indexOf("@")), "sent");

					if (recipientLogin.getLED() != null)
						recipientLogin.getLED().setBlink(mail, 0); // Change LED
																	// state if
																	// the
																	// user just
																	// read a
																	// mail
					this.send(mail.getID());
				} else
					this.sendError("The address " + mail.getRecipient() + " is unknown");
			}

		} else if (obj.getClass() == String.class) {
			// Client is asking for closed connection
			if (((String) obj).equals("Finished")) {
				System.out.println("Client " + this.login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
						+ " is asking to close connection");
				// Set LED for user off
				if (this.login.getLED() != null) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.login.getLED().turn("Off", 1);
				}
				this.keepOpen = false;
			} else
				this.request = (String) obj;

		} else if (obj.getClass() == ArrayList.class) {
			// Client is asking to get mails
			@SuppressWarnings("unchecked")
			ArrayList<ArrayList<Long>> IDs = (ArrayList<ArrayList<Long>>) obj;
			System.out.println("Client " + login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
					+ " is asking for new mails");
			LoadMails mails = new LoadMails(this.login, IDs);
			this.send(mails);

		} else if (obj.getClass() == Long.class) {
			Long ID = (Long) obj;

			if ((this.request != null) && (this.request.equals("inbox"))) {
				// Move mail from inbox to trash
				File file = new File("./storage/" + this.login.getUsername() + "/inbox/" + ID + ".mail");
				if (file.renameTo(new File("./storage/" + this.login.getUsername() + "/trash/" + ID + ".mail")))
					this.send("Done");
				else
					this.send("Failed");
			} else if ((this.request != null) && ((this.request.equals("drafts")) || (this.request.equals("sent")) || (this.request.equals("trash")))) {
				// Delete draft/sent/trash mail
				File file = new File("./storage/" + this.login.getUsername() + "/" + this.request + "/" + ID + ".mail");
				if (file.delete())
					this.send("Done");
				else
					this.send("Failed");
			} else {
				// Client is asking to set mail with ID obj read
				System.out.println("Client " + login.getUsername() + "@" + this.clientSocket.getInetAddress().getHostAddress()
						+ " is asking to set mail with ID " + ID + " read");
				LoadMails mails = new LoadMails(this.login, null, "inbox");
				mails.setRead(ID);
				if (this.login.getLED() != null)
					this.login.getLED().setBlink(mails); // Change LED state if
															// the user just
															// read a mail
				this.send("Done");
			}

		} else
			this.sendError("Invalid object.");
	}

	// Send a generic object back to the client
	private void send(Object o) {
		try {
			System.out.println("02. -> Sending (" + o + ") to the client " + this.login.getUsername() + "@"
					+ this.clientSocket.getInetAddress().getHostAddress());
			this.os.writeObject(o);
			this.os.flush();
		} catch (Exception e) {
			System.out.println("XX." + e.getStackTrace());
		}
	}

	// Send an error message to the client
	public void sendError(String message) {
		this.send("Error: " + message);
	}

	// Close the client socket
	public void closeSocket() {
		try {
			this.os.close();
			this.is.close();
			this.clientSocket.close();
		} catch (Exception e) {
			System.out.println("XX. " + e.getStackTrace());
		}
	}
}
