package webMail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SaveMail {

	public SaveMail(Mail mail, String username, String folder) {
		Save(mail, username, folder);
	}

	public SaveMail(ArrayList<Mail> mails, String username, String folder) {
		Save(mails, username, folder);
	}

	public SaveMail(ArrayList<ArrayList<Mail>> mails, String username) {
		// Save a multidimensional array of mails
		String folder = "inbox";
		for (int i = 0; i < mails.size(); i++) {
			if (i == 1)
				folder = "drafts";
			else if (i == 2)
				folder = "sent";
			else if (i == 3)
				folder = "trash";
			Save(mails.get(i), username, folder);
		}
	}

	private void Save(ArrayList<Mail> mails, String username, String folder) {
		// Save an array of mails
		for (int i = 0; i < mails.size(); i++)
			Save(mails.get(i), username, folder);
	}

	private void Save(Mail mail, String username, String folder) {
		// Save one mail
		File file = new File("./storage/" + username + "/" + folder + "/" + mail.getID() + ".mail");
		try {
			FileOutputStream fileStream = new FileOutputStream(file, false); // false
																				// =>
																				// overwrite
			ObjectOutput output = new ObjectOutputStream(fileStream);

			output.writeObject(mail);
			output.flush();
			output.close();
			fileStream.close();
		} catch (Exception e) {
			System.out.println("Can't save mail @ " + file.getAbsolutePath());
		}
	}
}
