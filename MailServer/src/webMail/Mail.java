package webMail;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mail implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long ID;
	private String priority, subject, content, sender, recipient;
	private File file;
	private byte[] bytes;
	private Date date;
	private boolean read;
	
	public Mail(String priority, File file, String subject, String content, String sender, String recipient){
		this.priority=priority;
		this.file=file;
		this.subject=subject;
		this.content=content;
		this.sender=sender;
		this.recipient=recipient;
		this.date=new Date();
		this.read=false;
	};
	
	public String getSender(){
	return this.sender;
	}
	
	public String getRecipient(){
		return this.recipient;
	}
	
	public String getPriority(){
		return this.priority;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public String getFileEta(){
		if(this.file!=null)
			return "Y";
		else
			return "N";
	}
	
	public Long getID(){
		return this.ID;
	}
	
	public void setID(Long ID){
		this.ID=ID;
	}
	
	public void setRead(boolean b){
		this.read=b;
	}
	
	public boolean isRead(){
		return this.read;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	public String getStringDate(){
		return DateToString(this.date);
	}
	
	private static String DateToString(Date date){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		return df.format(date);
	}
	
	public void setBytes(byte[] bytes){
		this.bytes=bytes;
		
	}
	
	public byte[] getBytes(){
		return this.bytes;
	}

}
