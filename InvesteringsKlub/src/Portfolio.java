import java.util.*;

public class Portfolio {
    protected double totalValueDKK;
    protected List<Holding> holdings = new ArrayList<>();

    public Portfolio(){
        updateTotalValue();
    }

    public double getTotalValueDKK() {
        return totalValueDKK;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void updateTotalValue(){
        double value = 0;
        for(Holding h: holdings){
            h.updateCurrentPriceDKK();
            value += h.getCurrentPriceDKk();
        }
        totalValueDKK = value;
    }
}
