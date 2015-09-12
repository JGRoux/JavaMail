package webMail;

public class AutomaticGetMails extends Thread {
	private LoginInfos login;
	private Table tables[];
	private Connection connection;
	
	public AutomaticGetMails(LoginInfos login, Table tables[], Connection connection) {
		this.login=login;
		this.tables=tables;
		this.connection=connection;
	}
	
	// Thread to get new mails every minute
	public void run() {
		// Only when launch => a connection is already open
		this.connection.getMails(this.tables);
		this.connection.close();
		while (true) {
			try {
				sleep(60000); // Sleep for one minute before rechecks mails
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.connection=new Connection(this.login);
			this.connection.getMails(this.tables);
			this.connection.close();
		}
	}
}
