package webMail;

import java.io.File;

public class UserFolders {

	public UserFolders(LoginInfos login) {
		File userDir = new File("./storage/" + login.getUsername());
		if (!userDir.exists()) {
			System.out.println("Creating directory: " + userDir.getName());
			if (userDir.mkdirs()) {
				System.out.println("New directory " + userDir.getName() + " has been created");
				// Create necessary folders
				String[] folders = { "inbox", "drafts", "sent", "trash" };
				for (String folder : folders) {
					File dir = new File("./storage/" + login.getUsername() + "/" + folder);
					dir.mkdir();
				}

			} else
				System.out.println("Cannot create " + userDir.getName() + ". Please check read/write rights");
		}
	}
}
