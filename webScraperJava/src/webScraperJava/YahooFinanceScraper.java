package webScraperJava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class YahooFinanceScraper {
	
	public static List<String> tickers = new ArrayList<String>();
	public static List<Equity> stocks = new ArrayList<Equity>();
	public static List<ThreadableWork> threads = new ArrayList<ThreadableWork>();
	public static URL url;
	public static int THREAD_COUNT = 16;		// MAX = 16
	public static int MAX_DAYS = 7;
	public static String tempTkr = "";
	public static String tempText = "";
	public static int tempInt = 0;
	public static File tempFile;
	public static String tempLocator;

	public static String tradingNotebookFile = new String("equity_master_data.xlsx");
	public static File pricingDataFile = new File("yahoo_pricing_data.xlsx");
	public static String equityDataFolder = "Equity_Data\\";
	public static String equityObjectsFolder = "Daily_Objects\\";
	
	public static void main(String[] args) throws IOException {
		System.out.println("STARTED: webScraperJava");
		
		Scanner input = new Scanner(System.in);
		boolean undecided = true;
		while(undecided) {
			System.out.println("Would you like to...");
			System.out.println("(1) get NEW equity data");
			System.out.println("(2) get EXISTING equity data");
			System.out.print("Type '1' to get NEW data, OR type '2' for EXISTING data: ");
			tempInt = input.nextInt();
			if(tempInt == 1 || tempInt == 2) {
				undecided = false;
			}
		}
		input.close();
		
		getTickersList();
		
		// NEW gets most recent data
		if(tempInt == 1) {
			distributeSourceWork(THREAD_COUNT);
			createNewEquityObjects();
			parseEquitySourceData(equityDataFolder);
		}
		// EXISTING data helpful in expediting data inputs when new equity object fields are added to the program
		else if(tempInt == 2) {
			getRecentObjects(equityObjectsFolder);
		}
		
		updatePricingData();
		saveObjectData(equityObjectsFolder);	
		dataScrub();
		
		System.out.println("COMPLETED: webScraperJava");
	}
	
	
	// Retrieve all stock tickers from trading_notebook_tickers_practice.txt [text] file as List<String> tickers
	public static void getTickersList() {
		System.out.println("STARTED: getTickersList()");
		
		try {
			FileInputStream fis = new FileInputStream(tradingNotebookFile);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sht = wb.getSheet("DATA");
			Iterator<Row> itr = sht.iterator();
			while(itr.hasNext()) {
				Row row = itr.next();
				Cell cell = row.getCell(0);
					switch(cell.getCellType()) {
					case NUMERIC:
						break;
					case STRING:
						tickers.add(cell.getStringCellValue());	
						break;
					case BOOLEAN:
						break;
					case ERROR:
						break;
					case _NONE:
						break;
					case FORMULA:
						break;
					case BLANK:
						break;
					}
			}
			wb.close();
			fis.close();
			System.out.println("tickers.size(): " + tickers.size());
		}
		catch(IOException e) { System.out.println("ERROR_IO exception"); System.out.println(e); }
		
		System.out.println("COMPLETED: getTickersList()");
	}
	
	
	// Create Equity objects in stocks<Equity> list for each ticker from tickers<String>
	public static void createNewEquityObjects() {
		System.out.println("START: createNewEquityObjects()");
		
		for(int i = 0; i < tickers.size(); i++) {
			Equity eq = new Equity();
			eq.setTicker(tickers.get(i));
			stocks.add(eq);
			//System.out.println("stocks(" + stocks.size() + "): " + stocks.get(i).getTicker());
		}
		System.out.println("stocks.size(): " + stocks.size());
		
		System.out.println("COMPLETED: createNewEquityObjects()");
	}

	
	// distribute equity source data collection work via threads 
	public static void distributeSourceWork(int threadCount) {
		System.out.println("START: distributeSourceWork()");
		
		int addRemainder = 0;
		int tempRemainder = 0;
		int threadFraction = 0;
		
		if(tickers.size() <= THREAD_COUNT) {
			threadFraction = THREAD_COUNT;
		}
		else {
			tempRemainder = tickers.size() % THREAD_COUNT;
			threadFraction = (tickers.size() - tempRemainder) / THREAD_COUNT;
		}
		
		for(int i = THREAD_COUNT; i > 0; i--) {
			if(i == THREAD_COUNT) { addRemainder = tempRemainder; }
			else { addRemainder = 0; }
			
			switch(i) {
			case 0 : 
				System.out.println("ERROR - no thread 0");
				break;
			case 1 : 
				ThreadableWork t1 = new ThreadableWork("thread_1", 0, ((threadFraction * i) - 1 + addRemainder));
				t1.start();
				threads.add(t1);
				break;
			case 2 : 
				ThreadableWork t2 = new ThreadableWork("thread_2", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t2.start();
				threads.add(t2);
				break;
			case 3 : 
				ThreadableWork t3 = new ThreadableWork("thread_3", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t3.start();
				threads.add(t3);
				break;
			case 4 : 
				ThreadableWork t4 = new ThreadableWork("thread_4", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t4.start();
				threads.add(t4);
				break;
			case 5 : 
				ThreadableWork t5 = new ThreadableWork("thread_5", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t5.start();
				threads.add(t5);
				break;
			case 6 : 
				ThreadableWork t6 = new ThreadableWork("thread_6", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t6.start();
				threads.add(t6);
				break;
			case 7 : 
				ThreadableWork t7 = new ThreadableWork("thread_7", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t7.start();
				threads.add(t7);
				break;
			case 8 : 
				ThreadableWork t8 = new ThreadableWork("thread_8", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t8.start();
				threads.add(t8);
				break;
			case 9 : 
				ThreadableWork t9 = new ThreadableWork("thread_9", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t9.start();
				threads.add(t9);
				break;
			case 10 : 
				ThreadableWork t10 = new ThreadableWork("thread_10", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t10.start();
				threads.add(t10);
				break;
			case 11 : 
				ThreadableWork t11 = new ThreadableWork("thread_11", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t11.start();
				threads.add(t11);
				break;
			case 12 : 
				ThreadableWork t12 = new ThreadableWork("thread_12", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t12.start();
				threads.add(t12);
				break;
			case 13 : 
				ThreadableWork t13 = new ThreadableWork("thread_13", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t13.start();
				threads.add(t13);
				break;
			case 14 : 
				ThreadableWork t14 = new ThreadableWork("thread_14", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t14.start();
				threads.add(t14);
				break;
			case 15 : 
				ThreadableWork t15 = new ThreadableWork("thread_15", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t15.start();
				threads.add(t15);
				break;
			case 16 : 
				ThreadableWork t16 = new ThreadableWork("thread_16", (threadFraction * (i - 1)), ((threadFraction * i) - 1 + addRemainder));
				t16.start();
				threads.add(t16);
				break;
			}
		}
		// PROGRAM WAIT UNTIL ALL THREADS COMPLETED
		for(int u = 0; u < threads.size(); u++) {
			while(threads.get(u).getStatus()) { 
				// DO NOTHING, WAIT
			}
		}
		
		System.out.println("COMPLETED: distributeSourceWork()");
	}
		
	
	// Update Equity objects using Equity_Data .txt file(s)
	public static void parseEquitySourceData(String tempEquityDataFolder) {
		System.out.println("START: parseEquitySourceData()"); 
		
		for(int i = 0; i < stocks.size(); i++) {			
			if(i % 300 == 0) {  System.out.println("parsed files: " + i + " of " + stocks.size()); }

			tempTkr = stocks.get(i).getTicker();
			String tempFileName = tempEquityDataFolder + tempTkr + ".txt";
			tempText = "";
			try {
				tempText = new String(Files.readAllBytes(Paths.get(tempFileName)));
			}
			catch(IOException e) { e.printStackTrace(); System.out.println("readAllBytes FAILURE to read: " + tempFileName);}
			
			String tempS = "";
			Double tempPrice;
				
			//read source code text file(s) to locate and parse data
			for(int x = 0; x < tempText.length() - 50; x++) {
				// B
				if(tempText.charAt(x) == 'b') {	  
					// beta":{"raw":1.298759,"fmt":"1.30"}
					if(tempText.charAt(x+1) == 'e' && tempText.charAt(x+2) == 't' && tempText.charAt(x+3) == 'a' 
							&& tempText.charAt(x+4) == '"' && tempText.charAt(x+5) == ':' && tempText.charAt(x+6) == '{') {
						// beta":{}
						if(tempText.charAt(x+7) == '}') { stocks.get(i).setBeta(0.0); }
						else {
							int countNB = 0;
							for(int y = 0; y < 35; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countNB++;
								}
								if(countNB == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								double tempBeta = Double.parseDouble(tempS);
								stocks.get(i).setBeta(tempBeta);
							}
							countNB = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
				}
				// C
				if(tempText.charAt(x) == 'c') {	 
					// find "currentRatio", exclude: ""
					if(tempText.charAt(x+1) == 'u' && tempText.charAt(x+2) == 'r' && tempText.charAt(x+3) == 'r' && tempText.charAt(x+4) == 'e'
							&& tempText.charAt(x+5) == 'n' && tempText.charAt(x+6) == 't' && tempText.charAt(x+7) == 'R' 
							&& tempText.charAt(x+8) == 'a' && tempText.charAt(x+9) == 't' && tempText.charAt(x+10) == 'i'
							&& tempText.charAt(x+11) == 'o' && tempText.charAt(x+14) == '{' && tempText.charAt(x-1) != '_') {
						//"currentRatio":{}
						if(tempText.charAt(x+15) == '}') {
							stocks.get(i).setQuickRatio(0.0);
						}
						else {
							int countCR = 0;
							//"currentRatio":{"raw":0.865,"fmt":"0.87"}
							for(int y = 11; y < 60; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countCR++;
								}
								if(countCR == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								double quickR = Double.parseDouble(tempS);
								stocks.get(i).setCurrentRatio(quickR);
								stocks.get(i).setMetricsUpdated(true);
							}
							countCR = 0;
							tempS = "";
						}
					}
					// find 'currentPrice'
					if(tempText.charAt(x+1) == 'u' && tempText.charAt(x+2) == 'r' && tempText.charAt(x+3) == 'r' && tempText.charAt(x+4) == 'e'
							&& tempText.charAt(x+5) == 'n' && tempText.charAt(x+6) == 't' && tempText.charAt(x+7) == 'P'
							&& tempText.charAt(x+8) == 'r' && tempText.charAt(x+9) == 'i' && tempText.charAt(x+10) == 'c'
							&& tempText.charAt(x+11) == 'e' && tempText.charAt(x+14) == '{') {
						// "currentPrice":{}
						if(tempText.charAt(x+15) == '}') { stocks.get(i).setCurrentPrice(0.0); }
						// "currentPrice":{"raw":155.81,"fmt":"155.81"}
						else {
							int countCP = 0;
							for(int y = 0; y < 50; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countCP++;
								}
								if(countCP == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setCurrentPrice(tempPrice);
							}
							countCP = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
				}
				// D
				if(tempText.charAt(x) == 'd') {	 
					// find "dividendRate", exclude: "trailingAnnualDividendRate", ",dividendRate,"
					if(tempText.charAt(x+1) == 'i' && tempText.charAt(x+2) == 'v' && tempText.charAt(x+3) == 'i' && tempText.charAt(x+4) == 'd'
							&& tempText.charAt(x+5) == 'e' && tempText.charAt(x+6) == 'n' && tempText.charAt(x+7) == 'd' && tempText.charAt(x+8) == 'R'
							&& tempText.charAt(x+9) == 'a' && tempText.charAt(x+10) == 't' && tempText.charAt(x+11) == 'e' && tempText.charAt(x+13) == ':' 
							&& tempText.charAt(x+14) == '{' && tempText.charAt(x-1) != 'l' && tempText.charAt(x+12) != ',') {
						// "dividendRate":{}
						if(tempText.charAt(x+15) == '}') {
							stocks.get(i).setDividendRate(0.0);
						}
						else {
							int countDivR = 0;
							// "dividendRate":{"raw":0.92,"fmt":"0.92"}
							for(int y = 11; y < 60; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countDivR++;
								}
								if(countDivR == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								double divRate = Double.parseDouble(tempS);
								stocks.get(i).setDividendRate(divRate);
								stocks.get(i).setMetricsUpdated(true);
							}
							countDivR = 0;
							tempS = "";
						}
					}
				}
				// E
				if(tempText.charAt(x) == 'e') {
					// "exDividendDate":{"raw":1605571200,"fmt":"2020-11-17"}
					if(tempText.charAt(x+1) == 'x' && tempText.charAt(x+2) == 'D' && tempText.charAt(x+3) == 'i' && tempText.charAt(x+4) == 'v'
							&& tempText.charAt(x+5) == 'i' && tempText.charAt(x+6) == 'd' && tempText.charAt(x+7) == 'e' && tempText.charAt(x+8) == 'n'
							&& tempText.charAt(x+9) == 'd' && tempText.charAt(x+10) == 'D' && tempText.charAt(x+11) == 'a' && tempText.charAt(x+12) == 't' 
							&& tempText.charAt(x+13) == 'e' && tempText.charAt(x+15) == ':' && tempText.charAt(x+16) == '{') {
						// exDividendDate":{}
						if(tempText.charAt(x+17) == '}') {
							stocks.get(i).setExDividendDate("");
						}
						// exDividendDate":{"raw":1605571200,"fmt":"2020-11-17"}
						else {
							int countDivD = 0;
							for(int y = 13; y < 60; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countDivD++;
								}
								if(countDivD == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '-') {
										tempS += "" + tempText.charAt(digitIndex);
									}
								}
								
							}
							if(tempS != "") {
								stocks.get(i).setExDividendDate(tempS);
								stocks.get(i).setMetricsUpdated(true);
							}
							countDivD = 0;
							tempS = "";
						}
					}
				}
				// N
				if(tempText.charAt(x) == 'n') {	  
					//find 'numberOfAnalystOpinions'
					if(tempText.charAt(x+6) == 'O' && tempText.charAt(x+8) == 'A' && tempText.charAt(x+12) == 'y' && tempText.charAt(x+17) == 'i') {
						// numberOfAnalystOpinions":{}
						if(tempText.charAt(x+26) == '}') {
							stocks.get(i).setTargetPriceAvailable(false);
							stocks.get(i).setNumberOfAnalystOpinions(0);
						}
						// numberOfAnalystOpinions":{"raw":10,"fmt":"10","longFmt":"10"}
						else {
							int countNAP = 0;
							// numberOfAnalystOpinions":{"raw":10
							for(int y = 0; y < 34; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countNAP++;
								}
								if(countNAP > 1) {
									if(checkNum("" + tempText.charAt(digitIndex))) {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								int aCount = Integer.parseInt(tempS);
								stocks.get(i).setNumberOfAnalystOpinions(aCount);
								stocks.get(i).setTargetPriceAvailable(true);
							}
							countNAP = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
				}
				// P
				if(tempText.charAt(x) == 'p') {	 
					// find "pageViews" which follows upgrades/downgrades signaling completion of file data
					if(tempText.charAt(x+1) == 'a' && tempText.charAt(x+2) == 'g' && tempText.charAt(x+3) == 'e' && tempText.charAt(x+4) == 'V'
							&& tempText.charAt(x+5) == 'i' && tempText.charAt(x+6) == 'e' && tempText.charAt(x+7) == 'w' && tempText.charAt(x+8) == 's') {
						 x = tempText.length()- 75;
					}
				}
				// Q
				if(tempText.charAt(x) == 'q') {	 
					// find "quickRatio", exclude: "_quickratio.lasttwelve"
					if(tempText.charAt(x+1) == 'u' && tempText.charAt(x+2) == 'i' && tempText.charAt(x+3) == 'c' && tempText.charAt(x+4) == 'k'
							&& tempText.charAt(x+5) == 'R' && tempText.charAt(x+6) == 'a' && tempText.charAt(x+7) == 't' && tempText.charAt(x+8) == 'i'
							&& tempText.charAt(x+9) == 'o' && tempText.charAt(x+12) == '{' && tempText.charAt(x-1) != '_') {
						//"quickRatio":{}
						if(tempText.charAt(x+13) == '}') {
							stocks.get(i).setQuickRatio(0.0);
						}
						else {
							int countQR = 0;
							//"quickRatio":{"raw":0.873,"fmt":"0.87"}
							for(int y = 9; y < 40; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countQR++;
								}
								if(countQR == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
								
							}
							if(tempS != "") {
								double quickR = Double.parseDouble(tempS);
								stocks.get(i).setQuickRatio(quickR);
								stocks.get(i).setMetricsUpdated(true);
							}
							countQR = 0;
							tempS = "";
						}
					}
				}
				// R
				if(tempText.charAt(x) == 'r') {
					//find 'recommendationMean'
					if(tempText.charAt(x+4) == 'm' && tempText.charAt(x+8) == 'd' && tempText.charAt(x+13) == 'n' && tempText.charAt(x+14) == 'M') {
						// recommendationMean":{}
						if(tempText.charAt(x+21) == '}') {
							stocks.get(i).setRecommendationMeanAvailable(false);
							stocks.get(i).setRecommendationMean(0.0);
						}
						// recommendationMean":{"raw":1.7,"fmt":"1.70"}
						else {
							int countREC = 0;
							// recommendationMean":{"raw":1.7,"fmt":"1.70"}
							for(int y = 17; y < 67; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countREC++;
								}
								if(countREC == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								double rCount = Double.parseDouble(tempS);
								stocks.get(i).setRecommendationMean(rCount);
								stocks.get(i).setRecommendationMeanAvailable(true);
							}
							countREC = 0;
							tempS = "";
						}
					}
				}
				// T
				if(tempText.charAt(x) == 't') {
					//find 'targetLowPrice'
					if(tempText.charAt(x+6) == 'L' && tempText.charAt(x+8) == 'w' && tempText.charAt(x+9) == 'P') {
						if(tempText.charAt(x+17) == '}') {
							stocks.get(i).setTargetPriceAvailable(false);
							stocks.get(i).setTargetLowPrice(0.0);
						}
						else {
							int countTLP = 0;
							for(int y = 0; y < 50; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countTLP++;
								}
								if(countTLP == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setTargetLowPrice(tempPrice);
								stocks.get(i).setTargetPriceAvailable(true);
							}
							countTLP = 0;
							tempPrice = 0.0;
							tempS = "";	
						}
					}
					//find 'targetMeanPrice'
					else if(tempText.charAt(x+6) == 'M' && tempText.charAt(x+9) == 'n' && tempText.charAt(x+10) == 'P') {
						if(tempText.charAt(x+18) == '}') {
							stocks.get(i).setTargetPriceAvailable(false);
							stocks.get(i).setTargetMeanPrice(0.0);
						}
						else {
							int countTMnP = 0;
							for(int y = 0; y < 50; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countTMnP++;
								}
								if(countTMnP == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setTargetMeanPrice(tempPrice);
								stocks.get(i).setTargetPriceAvailable(true);
							}
							countTMnP = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
					//find 'targetMedianPrice'
					else if(tempText.charAt(x+6) == 'M' && tempText.charAt(x+11) == 'n' && tempText.charAt(x+12) == 'P') {
						if(tempText.charAt(x+20) == '}') {
							stocks.get(i).setTargetPriceAvailable(false);
							stocks.get(i).setTargetMedianPrice(0.0);
						}
						else {
							int countTMdP = 0;
							for(int y = 0; y < 50; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countTMdP++;
								}
								if(countTMdP == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setTargetMedianPrice(tempPrice);
								stocks.get(i).setTargetPriceAvailable(true);
							}
							countTMdP = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
					//find 'targetHighPrice'
					else if(tempText.charAt(x+3) == 'g' && tempText.charAt(x+6) == 'H' && tempText.charAt(x+10) == 'P') {
						if(tempText.charAt(x+18) == '}') {
							stocks.get(i).setTargetPriceAvailable(false);
							stocks.get(i).setTargetHighPrice(0.0);
						}
						else {
							int countTHP = 0;
							for(int y = 0; y < 50; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countTHP++;
								}
								if(countTHP == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setTargetHighPrice(tempPrice);
								stocks.get(i).setTargetPriceAvailable(true);
							}
							countTHP = 0;
							tempPrice = 0.0;
							tempS = "";
						}
					}
					// find "trailingPE":{"raw":34.998486,"fmt":"35.00"}
					else if(tempText.charAt(x+1) == 'r' && tempText.charAt(x+2) == 'a' && tempText.charAt(x+3) == 'i' && tempText.charAt(x+4) == 'l'
							&& tempText.charAt(x+5) == 'i' && tempText.charAt(x+6) == 'n' && tempText.charAt(x+7) == 'g' && tempText.charAt(x+8) == 'P'
							&& tempText.charAt(x+9) == 'E' && tempText.charAt(x+11) == ':' && tempText.charAt(x+12) == '{') {
						if(tempText.charAt(x+13) == '}') {
							stocks.get(i).setTrailingPE(0.0);
						}
						else {
							int countTPE = 0;
							for(int y = 9; y < 59; y++) {
								int digitIndex = x + y;
								if(tempText.charAt(digitIndex) == ':') {
									countTPE++;
								}
								if(countTPE == 3) {
									if(checkNum("" + tempText.charAt(digitIndex)) || tempText.charAt(digitIndex) == '.') {
										tempS += "" + tempText.charAt(digitIndex);
									}	
								}
							}
							if(tempS != "") {
								tempPrice = Double.parseDouble(tempS);
								stocks.get(i).setMetricsUpdated(true);
								stocks.get(i).setTrailingPE(tempPrice);
							}
							countTPE = 0;
							tempPrice = 0.0;
							tempS = "";
						}	
					}
				}
			} 
		}
		
		System.out.println("COMPLETED: parseEquitySourceData()");
	}	
	
	
	// update trading_notebook.xlxs file with Equity pricing data
	public static void updatePricingData() {
		System.out.println("START: updatePricingData()");

		try {
			FileOutputStream os = new FileOutputStream(pricingDataFile);   //
			XSSFWorkbook wb = new XSSFWorkbook();   //creating Workbook instance;   Illegal reflective access by org.apache.poi.util.DocumentHelper (file:/C:/Users/voted/Documents/Financial/Trading/Java_jar_files/Excel/poi-ooxml-3.17.jar) to constructor com.sun.org.apache.xerces.internal.util.SecurityManager()
			XSSFSheet sheet = wb.createSheet("TargetPriceData");     //creating a Sheet object to retrieve object  

			String[] columnHeaders = new String[] 
				{
				"TICKER",							//0
				"Current_Price",					//1
				"Target_Price_Available",			//2
				"Number_Of_Analyst_Opinions",		//3
				"Target_LOW_Price",					//4
				"Target_MEAN_Price",				//5
				"Target_MEDIAN_Price",				//6
				"Target_HIGH_Price",				//7
				"Recommendation_Mean_Available",	//8
				"Recommendation_Mean",				//9
				"Beta",								//10
				"Metrics_Updated",					//11
				"Trailing_PE",						//12
				"Dividend_Rate",					//13
				"ExDividend_Date",					//14
				"Quick_Ratio",						//15
				"Current_Ratio"						//16
				};							
				
				//"marketCap":{"raw":2503991361536,"fmt":"2.5T","longFmt":"2,503,991,361,536"}
				//"revenueGrowth":{"raw":0.374,"fmt":"37.40%"}
				//"totalCashPerShare":{"raw":136.327,"fmt":"136.33"}
				//"revenuePerShare":{"raw":696.935,"fmt":"696.93"}
				//"upgradeDowngradeHistory":{"history":[{"epochGradeDate":1618185600,"firm":"JP Morgan","toGrade":"Overweight","fromGrade":"Neutral","action":"up"}
				//"sharesOutstanding":{"raw":21676000,"fmt":"21.676M","longFmt":"21,676,000"}
				//"priceToSalesTrailing12Months":{"raw":6.461213,"fmt":"6.46"}
			
			Row header = sheet.createRow(0);			
			for(int n = 0; n < columnHeaders.length; n++) {
				header.createCell(n).setCellValue(columnHeaders[n]);
			}
			
			System.out.println("yahoo_pricing_data write-to-file...");
			for(int x = 0; x < stocks.size(); x++) {
				Row row = sheet.createRow(x+1);
				for(int y = 0; y < columnHeaders.length; y++) {
					switch(y) {
						case 0 : 
							// TICKER
							row.createCell(y).setCellValue(stocks.get(x).getTicker());
							break;
						case 1 : 
							// CURRENT_PRICE
							row.createCell(y).setCellValue(stocks.get(x).getCurrentPrice());
							break;
						case 2 : 
							// TARGET_PRICE_AVAILABLE
							row.createCell(y).setCellValue(stocks.get(x).getTargetPriceAvailable());
							break;
						case 3 : 
							// NUMBER_OF_ANALYST_OPINIONS
							row.createCell(y).setCellValue(stocks.get(x).getNumberOfAnalystOpinions());
							break;
						case 4 :
							// TARGET_LOW_PRICE
							row.createCell(y).setCellValue(stocks.get(x).getTargetLowPrice());
							break;
						case 5 : 
							// TARGET_MEAN_PRICE
							row.createCell(y).setCellValue(stocks.get(x).getTargetMeanPrice());
							break;
						case 6 : 
							// TARGET_MEDIAN_PRICE
							row.createCell(y).setCellValue(stocks.get(x).getTargetMedianPrice());
							break;
						case 7 : 
							// TARGET_HIGH_PRICE
							row.createCell(y).setCellValue(stocks.get(x).getTargetHighPrice());
							break;
						case 8 : 
							// RECOMMENDATION_MEAN_AVAILABLE
							row.createCell(y).setCellValue(stocks.get(x).getRecommendationMeanAvailable());
							break;
						case 9 : 
							// RECOMMENDATION_MEAN
							row.createCell(y).setCellValue(stocks.get(x).getRecommendationMean());
							break;
						case 10 : 
							// BETA
							row.createCell(y).setCellValue(stocks.get(x).getBeta());
							break;
						case 11 : 
							// METRICS_UPDATED
							row.createCell(y).setCellValue(stocks.get(x).getMetricsUpdated());
							break;
						case 12 : 
							// TRAILING_PE
							row.createCell(y).setCellValue(stocks.get(x).getTrailingPE());
							break;
						case 13 : 
							// DIVIDEND_RATE
							row.createCell(y).setCellValue(stocks.get(x).getDividendRate());
							break;
						case 14 : 
							// EX_DIVIDEND_RATE
							row.createCell(y).setCellValue(stocks.get(x).getExDividendDate());
							break;
						case 15 : 
							// 	QUICK_RATIO
							row.createCell(y).setCellValue(stocks.get(x).getQuickRatio());
							break;
						case 16 : 
							// 	CURRENT_RATIO
							row.createCell(y).setCellValue(stocks.get(x).getCurrentRatio());
							break;
					}
				}
			}
			wb.write(os);
			
			wb.close();
			os.close();
		}
		catch(FileNotFoundException e) { e.printStackTrace(); }
		catch(IOException i) { i.printStackTrace(); }
		
		System.out.println("COMPLETED: updatePricingData()");
	}
	
	
	// save object data by write to file
	public static void saveObjectData(String tempEquityObjFolder) throws IOException {
		System.out.println("START: saveObjectData()");
				
		String tempStr = "" + java.time.LocalDate.now();
		tempFile = new File(tempEquityObjFolder + tempStr + ".txt");
		tempFile.createNewFile();
		FileOutputStream fos2 = new FileOutputStream(tempFile);
				
		try {
			if (!tempFile.exists()) { tempFile.createNewFile(); }
					
			for(int z = 0; z < stocks.size(); z++) {
				tempStr = stocks.get(z).getTicker() + ";" + 
						stocks.get(z).getCurrentPrice() + ";" + 
						stocks.get(z).getTargetPriceAvailable() + ";" + 
						stocks.get(z).getNumberOfAnalystOpinions() + ";" +
						stocks.get(z).getTargetLowPrice() + ";" + 
						stocks.get(z).getTargetMeanPrice() + ";" + 
						stocks.get(z).getTargetMedianPrice() + ";" +
						stocks.get(z).getTargetHighPrice() + ";" +
						stocks.get(z).getRecommendationMeanAvailable() + ";" + 
						stocks.get(z).getRecommendationMean() + ";" +
						stocks.get(z).getBeta() + ";" +
						stocks.get(z).getMetricsUpdated() + ";" +
						stocks.get(z).getTrailingPE() + ";" +
						stocks.get(z).getDividendRate() + ";" +
						stocks.get(z).getExDividendDate() + ";" +
						stocks.get(z).getQuickRatio() + ";" +
						stocks.get(z).getCurrentRatio() + "\n";
						
				byte[] bytesArray = tempStr.getBytes();
						
				fos2.write(bytesArray);
				fos2.flush();
			}
		}
		catch(IOException ioe) { ioe.printStackTrace(); }
				
		fos2.close();
		System.out.println("COMPLETED: saveObjectData()");
	}
	
	
	// checking valid integer using parseInt() method 
	public static boolean checkNum(String s) {
		try { 
            Integer.parseInt(s); 
            return true;
        }  
        catch (NumberFormatException e) { return false; } 
	}
	
	
	//retrieve most recent object data and fill stocks<Equity>
	public static void getRecentObjects(String tempEquityObjFolder) {
		System.out.println("START: getRecentObjects()");
		
		try {
			
			Equity eq3;
			String tempLine = "";
			
			for(int d = 0; d < MAX_DAYS; d++) {
				String tempStr = "" + java.time.LocalDate.now().minusDays(d);
				tempFile = new File(tempEquityObjFolder + tempStr + ".txt");
				System.out.println("tempFile: " + tempEquityObjFolder + tempStr + ".txt");
				
				if(tempFile.exists()) {
					Scanner scnr = new Scanner(tempFile);
					
					while(scnr.hasNextLine()) {
						tempLine = scnr.nextLine();
						
						String[] tempData = tempLine.split(";");
						
						eq3 = new Equity();
						
						eq3.setTicker(tempData[0]);
						eq3.setCurrentPrice(Double.parseDouble(tempData[1]));
						eq3.setTargetPriceAvailable(Boolean.parseBoolean(tempData[2]));
						eq3.setNumberOfAnalystOpinions(Integer.parseInt(tempData[3]));
						eq3.setTargetLowPrice(Double.parseDouble(tempData[4]));
						eq3.setTargetMeanPrice(Double.parseDouble(tempData[5]));
						eq3.setTargetMedianPrice(Double.parseDouble(tempData[6]));
						eq3.setTargetHighPrice(Double.parseDouble(tempData[7]));
						eq3.setRecommendationMeanAvailable(Boolean.parseBoolean(tempData[8]));
						eq3.setRecommendationMean(Double.parseDouble(tempData[9]));
						eq3.setBeta(Double.parseDouble(tempData[10]));
						eq3.setMetricsUpdated(Boolean.parseBoolean(tempData[11]));
						eq3.setTrailingPE(Double.parseDouble(tempData[12]));
						eq3.setDividendRate(Double.parseDouble(tempData[13]));
						eq3.setExDividendDate(tempData[14]);
						eq3.setQuickRatio(Double.parseDouble(tempData[15]));
						eq3.setCurrentRatio(Double.parseDouble(tempData[16]));

						stocks.add(eq3);
					}
					scnr.close();
					d = MAX_DAYS;
				}
			}
		}
		catch(IOException e) { e.printStackTrace(); }
		System.out.println("stocks[" + stocks.size() + "]");
		
		System.out.println("COMPLETED: getRecentObjects()");
	}
	
	
	// garbage collect used data from ListArrays
	public static void dataScrub() {
		System.out.println("STARTED: dataScrub()");
		
		// remove String objects from tickers<String> if required
		if(tickers.size() > 0) {
			System.out.println("tickers.size() before scrub: " + tickers.size());
			tickers.clear();
			System.out.println("tickers.size() after scrub: " + tickers.size());
		}
		else {
			System.out.println("tickers<String>: no need to scrub");
		}
		
		// remove Equity objects from stocks<Equity> if required
		if(stocks.size() > 0) {
			System.out.println("stocks.size() before scrub: " + stocks.size());
			stocks.clear();
			System.out.println("stocks.size() after scrub: " + stocks.size());
		}
		else {
			System.out.println("stocks<Equity>: no need to scrub");
		}
		
		// remove ThreadableWork objects from threads<ThreadableWork> if required
		if(threads.size() > 0) {
			System.out.println("threads.size() before scrub: " + threads.size());
			threads.clear();
			System.out.println("threads.size() after scrub: " + threads.size());
		}
		else {
			System.out.println("threads<ThreadableWork>: no need to scrub");
		}
				
		System.out.println("COMPLETED: dataScrub()");
	}
}
