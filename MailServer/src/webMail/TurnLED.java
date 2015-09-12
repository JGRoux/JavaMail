package webMail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TurnLED extends Thread {
	private static String LEDS_PATH = "/sys/class/leds/beaglebone:green:usr";
	private String LED_PATH, priority;
	private static int i = 0;
	private int time;
	private boolean connect, loop;

	// Assign LED
	public TurnLED() {
		this.LED_PATH = LEDS_PATH + i;
		i++;
	}
	
	// Turn a LED on or off
	public void turn(String eta, int cas) {
		if ((eta.equalsIgnoreCase("On")) || (eta.equalsIgnoreCase("Off"))) {
			if (cas == 1)
				if (eta.equals("On"))
					this.connect = true;
				else
					this.connect = false;
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(LED_PATH + "/brightness"));
				bw.write(eta.equalsIgnoreCase("On") ? "1" : "0");
				bw.close();
			} catch (IOException e) {
				System.out.println("Failed to access the Beaglebone LEDs");
			}
		}
	}

	// Stop triggered and turn all the LEDs off
	public void turnAllOff() {
		for (int i = 0; i < 4; i++) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(LEDS_PATH + i + "/trigger"));
				bw.write("none");
				bw.close();
			} catch (IOException e) {
				System.out.println("Fail to put all LEDs off");
			}
		}
	}
	
	// Get the highest priority mail which is non-read
	public void setBlink(LoadMails mails) {
		this.priority = "A"; // reset highest priority (if no mail left not
								// read)
		if (mails.get(0).size() == 0)
			this.loop = false;
		else
			for (int i = 0; i < mails.get(0).size(); i++)
				if ((!mails.get(0).get(i).isRead()))
					setBlink(mails.get(0).get(i), 1);
		assign();
	}
	
	// Get the priority of the mail
	public void setBlink(Mail mail, int cas) {
		if (mail.getPriority().equals("H"))
			this.priority = "H";
		else if (mail.getPriority().equals("M") && (!this.priority.equals("H")))
			this.priority = "M";
		else if (mail.getPriority().equals("L") && (!this.priority.equals("M")) && (!this.priority.equals("H")))
			this.priority = "L";
		else if (mail.getPriority().equals("N") && (!this.priority.equals("L")) && (!this.priority.equals("M")) && (!this.priority.equals("H")))
			this.priority = "N";

		if (cas == 0)
			assign();
	}

	// Set the half-period time for blinking
	private void assign() {
		if (!this.priority.equals("A")) {
			if (this.priority.equals("H"))
				this.time = 300;
			else if (this.priority.equals("M"))
				this.time = 500;
			else if (this.priority.equals("L"))
				this.time = 700;
			else if (this.priority.equals("N"))
				this.time = 900;
			this.loop = true;
		} else
			this.loop = false;
	}
	
	// Infinite loop => blinking LED
	public void run() {
		String eta = "On";
		while (true) {
			if ((!this.connect) && (this.loop)) {
				eta = eta.equals("On") ? "Off" : "On";
				this.turn(eta, 0);
				try {
					sleep(this.time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
