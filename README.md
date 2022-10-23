# YahooFinanceScraper
YahooFinanceScraper in JAVA collects equity data for the S&P 500 stocks available at https://finance.yahoo.com and drops it into an Excel file (.xslx).
NOTE: You should open the two Excel files in the webScraperJava folder so the reference file paths will adjust and make the final data viewable. 

The program retrieves the list of equity tickers from equity_master_data.xlsx, builds Equity objects to house data, 
splits up the web scraping workload over 16 threads, dowloads each tickers website data into individual files and writes those files into the Equity_Data directory.
After the threads are done scraping the website data, the program reads the files and extracts specific data. Then the program writes each Equity object to a .txt
file and saves that file to Daily_Objects, and writes the data to yahoo_pricing_data.xlsx file. The equity_master_data.xlsx file contains ticker object data available
in Excel and pairs it with the scraped data referenced to the yahoo_pricing_data.xlsx file, making for a basic cental data store for each equity.

By saving the object data and the individual web data in .txt files you are able to reference them after adding additional data points to the program to look for and 
extract. Users may want to reprogram to delete the files after reading to conserve memory/space, or write directly to equity_master_data bypassing the referencing.
