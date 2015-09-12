package webMail;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class LoadUsers extends ArrayList<LoginInfos> {
	public LoadUsers() {
		// Load existing login
		File file = new File("./storage/Users.pwd");
		// If file exist and is readable
		if (file.isFile() && file.canRead())
			try {
				FileInputStream fileStream = new FileInputStream(file);
				ObjectInputStream input = new ObjectInputStream(fileStream);
				int i=0;
				// While remains bytes to read
				while (fileStream.available() > 0) {
					LoginInfos login = (LoginInfos) input.readObject();
					// Align the fourth first user to the LEDs
					if(i<4){
						LoadMails mails=new LoadMails(login,null,"inbox");
						login.setLED(new TurnLED());
						if(i==0)
							login.getLED().turnAllOff();
						login.getLED().turn("Off", 0); // In case of one LED has been set to bright before
						login.getLED().setBlink(mails);
						login.getLED().start();
					}
					this.add(login);
					i++;
				}
				input.close();
				fileStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public LoginInfos contains(LoginInfos login1){
		for (LoginInfos login2 : this)
			if (login2.equals(login1))
				return login2;
		return null;
	}
	
	// Check a username exist
		public LoginInfos containsUsername(String username) {
			if (this.isEmpty())
				return null;
			else {
				for (LoginInfos login2 : this)
					if (login2.getUsername().equals(username)) 						
						return login2;
				return null;
			}
		}
}
