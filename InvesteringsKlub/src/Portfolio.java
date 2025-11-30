import java.util.*;

public class Portfolio {
    protected double totalValueDKK;
    protected HashMap<String, Holding> holdings = new HashMap<>();

    public Portfolio(){
        updateTotalValue();
    }

    public double getTotalValueDKK() {
        return totalValueDKK;
    }

    public HashMap<String,Holding> getHoldings() {
        return holdings;
    }

    public void addHolding(Holding holding) {
        holdings.put(holding.getTicker(),holding);
    }

    public void updateTotalValue(){
        double value = 0;
        for(String h: holdings.keySet()){
            holdings.get(h).updateCurrentPriceDKK();
            value += holdings.get(h).getCurrentPriceDKk();
        }
        totalValueDKK = value;
    }


}
