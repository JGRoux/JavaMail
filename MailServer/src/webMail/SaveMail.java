package webMail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class SaveMail {
	
	public SaveMail(Mail mail, String username, String folder) {
		// Save one mail

		File file = new File("./storage/" + username + "/" + folder + "/" + mail.getID() + ".mail");
		try {
			FileOutputStream fileStream = new FileOutputStream(file, false); // false => overwrite
			ObjectOutput output = new ObjectOutputStream(fileStream);

			output.writeObject(mail);
			output.flush();
			output.close();
			fileStream.close();
		} catch (Exception e) {
			System.out.println("Can't save mail @ "+file.getAbsolutePath());
		}
	}
}
