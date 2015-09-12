package webMail;

import java.io.Serializable;


public class LoginInfos implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username, pwd, server;
	private boolean newUser = false;

	public LoginInfos(String username, String pwd, String server) {
		this.username = username;
		this.pwd = pwd;
		this.server = server;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.pwd;
	}

	public String getServer() {
		return this.server;
	}

	public boolean isNewUser() {
		return this.newUser;
	}

	public void setNewUser(boolean b) {
		this.newUser = b;
	}

	// Rewriting method equals to use contains method for the Array (contains
	// method use equals method)
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof LoginInfos)
			if ((((LoginInfos) obj).getUsername().equals(this.username)) && (((LoginInfos) obj).getPassword().equals(this.pwd)))
				return true;
		return false;
	}
}