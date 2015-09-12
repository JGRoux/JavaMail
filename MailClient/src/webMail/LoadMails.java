package webMail;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class LoadMails extends ArrayList<ArrayList<Mail>> implements Serializable {

	private static final long serialVersionUID = 1L;
	private LoginInfos login;

	public LoadMails(LoginInfos login, ArrayList<ArrayList<Long>> IDs, String dir) {
		Load(login, IDs, dir);
	}

	public LoadMails(LoginInfos login, ArrayList<ArrayList<Long>> IDs) {
		// Load mail files
		File folder = new File("./storage/" + login.getUsername());
		// If file exist and is readable
		if (folder.exists())
			for (File dir : folder.listFiles())
				if (dir.isDirectory()) {
					Load(login, IDs, dir.getName());
				}
	}
	
	private void Load(LoginInfos login, ArrayList<ArrayList<Long>> IDs, String dir){
		this.login=login;
		File folder = new File("./storage/" + this.login.getUsername()+"/"+dir);
		this.add(new ArrayList<Mail>());
		for (File file : folder.listFiles())
			if (file.isFile()) {
				String filename = file.getName();
				// Compare IDs sent by client and name file (format
				// ID.mail)
				// => Not safe but faster than load every mails in
				// memory
				int i=0;
				if(dir.equalsIgnoreCase("drafts"))
					i=1;
				else if(dir.equalsIgnoreCase("sent"))
					i=2;
				else if(dir.equalsIgnoreCase("trash"))
					i=3;
				
				if ((IDs==null)||(!IDs.get(i).contains(Long.parseLong(filename.substring(0, filename.indexOf("."))))))
					try {
						FileInputStream fileStream = new FileInputStream(file);
						ObjectInputStream input = new ObjectInputStream(fileStream);

						Mail mail = (Mail) input.readObject();
						this.get(i).add(mail);
						input.close();
						fileStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
	}
}