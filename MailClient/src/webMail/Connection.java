package webMail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Connection {
	private static int portNumber = 5050;
	private Socket socket = null;
	private ObjectOutputStream os = null;
	private ObjectInputStream is = null;
	private LoginInfos login;
	private String status;

	// Constructor
	public Connection(LoginInfos login) {
		this.login = login;
		String serverIP = null;
		if (login.getServer().equalsIgnoreCase("mail.com"))
			serverIP = "192.168.7.2";
		else if (login.getServer().equalsIgnoreCase("localhost"))
			serverIP = "127.0.0.1";
		else {
			new Error("The host " + login.getServer() + " does not exist");
			return;
		}

		if (this.connectToServer(serverIP))
			if (this.login.isNewUser())
				this.createUser(this.login);
			else
				this.getGrant(this.login);
		else {
			System.out.println("XX. Failed to open socket connection to: " + serverIP);
			new Error("Failed to contact server " + serverIP + ":" + portNumber);
		}
	}
	
	// Sending a mail to server
	public void sendMail(Mail mail) {
		System.out.println("01. -> Asking to send mail (" + mail.getSubject() + ") to the server...");
		this.send(mail);
		try {
			Object obj = receive();
			// Ok send
			if (obj.getClass() == Long.class) {
				mail.setID((Long) obj);
				this.status = "Sent";
			} else if (obj.getClass() == String.class) // User to send does not
														// exist
				this.status = (String) obj;
			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + obj);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}
	
	// Sending a new draft mail to server
	public void saveMail(Mail mail) {
		System.out.println("01. -> Asking to save mail with ID (" + mail.getSubject() + ") to status read to the server...");
		this.send("Save");
		this.sendMail(mail);
	}
	
	// Asking to server to get new mails (mails who are not on local)
	public void getMails(Table tables[]) {
		System.out.println("01. -> Asking to receive mails to the server...");

		// Get local IDs mails (Using 2 dimensions ArrayList to store IDs)
		ArrayList<ArrayList<Long>> IDs = new ArrayList<ArrayList<Long>>();
		for (int i = 0; i < tables.length; i++) {
			IDs.add(new ArrayList<Long>());
			ArrayList<Mail> mails = ((TableModel) tables[i].getTable().getModel()).getData();
			for (int j = 0; j < mails.size(); j++)
				IDs.get(i).add(mails.get(j).getID());
		}
		this.send(IDs);

		try {
			Object obj = receive();
			if (obj.getClass() == LoadMails.class) {
				LoadMails newMails = (LoadMails) obj;
				for (int i = 0; i < newMails.size(); i++) {
					TableModel model = ((TableModel) tables[i].getTable().getModel());
					for (int j = 0; j < newMails.get(i).size(); j++)
						model.addRow(newMails.get(i).get(j));
				}
				new SaveMail(newMails, this.login.getUsername());
				this.status = "Received";
			} else if (obj.getClass() == String.class)
				this.status = (String) obj;

			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + obj);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}
	
	// Ask to server to set read a mail
	public void sendRead(Long ID) {
		System.out.println("01. -> Asking to set mail with ID (" + ID + ") to status read to the server...");
		this.send(ID);
		try {
			Object obj = receive();
			// Ok send
			if (obj.getClass() == String.class)
				this.status = (String) obj;
			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + obj);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}
	
	// Asking to server to delete a mail
	public void delteteMail(Long ID, String folder) {
		System.out.println("01. -> Asking to delete mail with ID (" + ID + ") from " + folder + " folder to the server...");
		this.send(folder);
		this.send(ID);
		try {
			Object obj = receive();
			// Ok send
			if (obj.getClass() == String.class)
				this.status = (String) obj;
			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + obj);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}

	// Close connection with server
	public void close() {
		this.send("Finished"); // tell to server to close connection
		try {
			this.is.close();
			this.os.close();
			this.socket.close();
			System.out.println("06. -- Disconnected from Server.");
		} catch (IOException e) {
			System.out.println("Fail to close connection");
		}
	}
	
	// Start connection with server
	private boolean connectToServer(String serverIP) {
		try { // open a new socket to the server
			this.socket = new Socket(serverIP, portNumber);
			this.os = new ObjectOutputStream(this.socket.getOutputStream());
			this.is = new ObjectInputStream(this.socket.getInputStream());
			System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort());
			System.out.println(" -> from local address: " + this.socket.getLocalAddress().getHostAddress() + ":" + this.socket.getLocalPort());
		} catch (Exception e) {
			System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
			return false;
		}
		return true;
	}
	
	// Asking to grant the user
	private void getGrant(LoginInfos login) {
		System.out.println("01. -> Asking to grant (" + login.getUsername() + ") to the server...");
		this.send(login);
		try {
			this.status = (String) receive();
			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + status);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}

	// Create new user on server
	private void createUser(LoginInfos login) {
		System.out.println("01. -> Asking to create user (" + login.getUsername() + ") to the server...");
		this.send(login);
		try {
			this.status = (String) receive();
			System.out.println("05. <- The Server responded with: ");
			System.out.println(" <- " + status);
		} catch (Exception e) {
			System.out.println("XX. There was an invalid object sent back from the server");
		}
	}
	
	// Get the result of the server
	public String getStatus() {
		return this.status;
	}

	// Send a generic object.
	private void send(Object o) {
		try {
			System.out.println("02. -> Sending an object...");
			os.writeObject(o);
			os.flush();
		} catch (Exception e) {
			System.out.println("XX. Exception Occurred on Sending:" + e.toString());
		}
	}

	// Receive a generic object.
	private Object receive() {
		Object obj = null;
		try {
			System.out.println("03. -- About to receive an object...");
			obj = is.readObject();
			System.out.println("04. <- Object received...");
		} catch (Exception e) {
			System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		}
		return obj;
	}
}
