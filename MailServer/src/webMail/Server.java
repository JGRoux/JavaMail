package webMail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static int portNumber = 5050;
	private static ServerSocket serverSocket;
	private static LoadUsers loginArray;

	public Server() {

		initialize();
		// Load existing users
		loginArray = new LoadUsers();
	}

	private void initialize() {

		// Set correct time if it's not
		try {
			Runtime.getRuntime().exec("ntpdate -b -s -u pool.ntp.org");
		} catch (IOException e) {
			System.out.println(e);
		}

		// Create storage directory
		File storageDir = new File("./storage");
		if (!storageDir.exists()) {
			System.out.println("Creating directory: " + storageDir.getName());
			if (storageDir.mkdir()) {
				System.out.println("New directory " + storageDir.getName() + " has been created");
			} else {
				System.out.println("Cannot create " + storageDir.getName() + ". Please check read/write rights");
				System.exit(1);
			}
		}
		// Get the last ID
		File file = new File("./storage/ID.id");
		// If file exist and is readable
		if (file.exists()) {
			try {
				FileInputStream fileStream = new FileInputStream(file);
				ObjectInputStream input = new ObjectInputStream(fileStream);
				ConnectionHandler.ID = (long) input.readObject();
				input.close();
				fileStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			ConnectionHandler.ID = (long) 0;

		System.out.println("Get ID => " + ConnectionHandler.ID);
	}

	public static void main(String args[]) {

		new Server();

		// Set up the Server Socket
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("New Server has started listening on port: " + portNumber);
		} catch (IOException e) {
			System.out.println("Cannot listen on port: " + portNumber + ", Exception: " + e);
			System.exit(1);
		}

		// Server is now listening for connections
		while (true) // loop once for each client request
		{
			Socket clientSocket = null;
			try {
				System.out.println("**. Listening for a connection...");
				clientSocket = serverSocket.accept();
				System.out.println("00. <- Accepted socket connection from a client @" + clientSocket.getInetAddress().getHostAddress() + ":"
						+ clientSocket.getPort());
			} catch (IOException e) {
				System.out.println("XX. Accept failed: " + portNumber + e);
				break; // Better than set the loop variable to false because
						// don't cause troubles with trying ConnectionHander =>
						// break the loop
			}

			ConnectionHandler con = new ConnectionHandler(clientSocket, loginArray);
			con.start();
			System.out.println("02. -- Finished communicating with client:" + clientSocket.getInetAddress().getHostAddress());
		}
		// Server is no longer listening for client connections - time to shut
		// down.
		try {
			System.out.println("04. -- Closing down the server socket gracefully.");
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("XX. Could not close server socket. " + e.getMessage());
		}
	}
}
