package webScraperJava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ThreadableWork implements Runnable{

	private Thread t;
	private String t_name = "";
	private String[] t_data;
	private URL url;
	
	
	// construct thread object data
	ThreadableWork(String threadName, int startIndex, int endIndex){
		t_name = threadName;
		t_data = new String[((endIndex - startIndex) + 1)];
		for(int x = 0; x < t_data.length; x++) {
			t_data[x] = YahooFinanceScraper.tickers.get(startIndex + x);
		}
		
		System.out.println("Created thread: " + t_name);
	}
	
	
	// instantiate new thread
	public void start() {
		System.out.println("Starting thread: " + t_name);
		
		if(t == null) {
			t = new Thread (this, t_name);
			t.start();
		}	
	}
	
	
	// thread status check
	public boolean getStatus() {
		return this.t.isAlive();
	}
	
	
	// save data from Finance.Yahoo! Analysis [View Source] pages for each ticker
	public void run() {
		System.out.println("Running thread: " + this.t_name);
		
		String tempText = "";
		int slice = (this.t_data.length / YahooFinanceScraper.THREAD_COUNT) / 5;
		
		for(int i = 0; i < this.t_data.length; i++) {
			if(i % slice == 0) { System.out.println(this.t_name + " websites visited: " + i + " of " + this.t_data.length + ", retreiving ticker " + this.t_data[i]); }
			
			try {
				url = new URL("https://finance.yahoo.com/quote/" + this.t_data[i] + "/analysis?p=" + t_data[i]);
			}
			catch (MalformedURLException ex) { 
				System.out.println("Thread exception: " + this.t_name);
				ex.printStackTrace(); 
				System.out.println("Exception, getEquitySourceData(" + this.t_data[i] + ")\n" + ex.toString());
			}
			
			File tempWriteFile = new File(YahooFinanceScraper.equityDataFolder + this.t_data[i] + ".txt");
			
			try {
				Scanner input = new Scanner(url.openStream());
				while(input.hasNext()) {
					tempText += input.nextLine();
				}
				input.close();
			}
			catch (IOException io) { 
				System.out.println("Thread exception: " + this.t_name);
				io.printStackTrace(); 
			}
			
			// write page source data to file
			try {
				PrintWriter output = new PrintWriter(tempWriteFile);
				output.print(tempText);
				tempText = "";
				output.close();
			}
			catch (FileNotFoundException e) { 
				System.out.println("Thread exception: " + this.t_name);
				e.printStackTrace(); 
				System.out.println("FileNotFoundException"); 
			}
		}
		
		System.out.println("Exiting thread: " + this.t_name);
	}
}
