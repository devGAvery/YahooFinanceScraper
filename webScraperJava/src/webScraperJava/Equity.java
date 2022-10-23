package webScraperJava;


public class Equity {
	
	public String ticker;
	public String companyName;
	public double currentPrice;
	public double targetLowPrice;
	public double targetMeanPrice;
	public double targetMedianPrice;
	public double targetHighPrice;
	public boolean targetPriceAvailable = false;
	public int numberOfAnalystOpinions;
	public boolean recommendationMeanAvailable = false;
	public double recommendationMean;
	public double beta;
	public boolean priceHistoryUpdated = false;
	public boolean metricsUpdated = false;
	public double trailingPE;
	public double dividendRate;
	public String exDividendDate;
	public double quickRatio;
	public double currentRatio;
	
	
	public String getTicker() { return this.ticker; }
	public void setTicker(String newTicker) { this.ticker = newTicker; }
	
	public String getCompanyName() { return this.companyName; }
	public void setCompanyName(String newCoName) { this.companyName = newCoName; }
	
	public double getCurrentPrice() { return this.currentPrice; }
	public void setCurrentPrice(double newCurrentPrice) { this.currentPrice = newCurrentPrice; }
	
	public double getTargetLowPrice() { return this.targetLowPrice; }
	public void setTargetLowPrice(double newTargetLowPrice) { this.targetLowPrice = newTargetLowPrice; }
	
	public double getTargetMeanPrice() { return this.targetMeanPrice; }
	public void setTargetMeanPrice(double newTargetMeanPrice) { this.targetMeanPrice = newTargetMeanPrice; }
	
	public double getTargetMedianPrice() { return this.targetMedianPrice; }
	public void setTargetMedianPrice(double newTargetMedianPrice) { this.targetMedianPrice = newTargetMedianPrice; }
	
	public double getTargetHighPrice() { return this.targetHighPrice; }
	public void setTargetHighPrice(double newTargetHighPrice) { this.targetHighPrice = newTargetHighPrice; }
	
	public boolean getTargetPriceAvailable() { return this.targetPriceAvailable; }
	public void setTargetPriceAvailable(boolean newTPA) { this.targetPriceAvailable = newTPA; }
	
	public int getNumberOfAnalystOpinions() { return this.numberOfAnalystOpinions; }
	public void setNumberOfAnalystOpinions(int newNumOpinions) { this.numberOfAnalystOpinions = newNumOpinions; }
	
	public boolean getRecommendationMeanAvailable() { return this.recommendationMeanAvailable; }
	public void setRecommendationMeanAvailable(boolean newRecommendation) { this.recommendationMeanAvailable = newRecommendation; }
	
	public double getRecommendationMean() { return this.recommendationMean; }
	public void setRecommendationMean(double newRecommendationMean) { this.recommendationMean = newRecommendationMean; }
	
	public double getBeta() { return this.beta; }
	public void setBeta(double newBeta) { this.beta = newBeta; }
	
	public boolean getPriceHistoryUpdated() { return this.priceHistoryUpdated; }
	public void setPriceHistoryUpdated(boolean newHistory) { this.priceHistoryUpdated = newHistory; }
	
	public boolean getMetricsUpdated() { return this.metricsUpdated; }
	public void setMetricsUpdated(boolean newMetrics) { this.metricsUpdated = newMetrics; }
	
	public double getTrailingPE() { return this.trailingPE; }
	public void setTrailingPE(double newTrailingPE) { this.trailingPE = newTrailingPE; }
	
	public double getDividendRate() { return this.dividendRate; }
	public void setDividendRate(double newDividendRate) { this.dividendRate = newDividendRate; }
	
	public String getExDividendDate() { return this.exDividendDate; }
	public void setExDividendDate(String newExDividendDate) { this.exDividendDate = newExDividendDate; }
	
	public double getQuickRatio() { return this.quickRatio; }
	public void setQuickRatio(double newQuickRatio) { this.quickRatio = newQuickRatio; }
	
	public double getCurrentRatio() { return this.currentRatio; }
	public void setCurrentRatio(double newCurrentRatio) { this.currentRatio = newCurrentRatio; }
}
