package Domain;
import CSVHandler.*;
import java.util.*;

public class SectorFilter {
    
    // Gruppér aktier efter sector
    public static Map<Sector, List<Stock>> groupBySector(List<Stock> stocks) {
        Map<Sector, List<Stock>> grouped = new HashMap<>();
        
        // Initialiser alle sector med tomme lister
        for (Sector sector : Sector.values()) {
            grouped.put(sector, new ArrayList<>());
        }
        
        // Gruppér aktier
        for (Stock stock : stocks) {
            try {
                Sector sector = Sector.fromString(stock.getSector());
                grouped.get(sector).add(stock);
            } catch (IllegalArgumentException e) {
                // Ignorer aktier med ukendt sector
                System.err.println("Warning: Unknown sector for " + stock.getTicker() + ": " + stock.getSector());
            }
        }
        
        return grouped;
    }
    
    // Filtrer aktier baseret på én sector
    public static List<Stock> filterBySector(List<Stock> stocks, Sector sector) {
        List<Stock> filtered = new ArrayList<>();
        
        for (Stock stock : stocks) {
            try {
                Sector stockSector = Sector.fromString(stock.getSector());
                if (stockSector == sector) {
                    filtered.add(stock);
                }
            } catch (IllegalArgumentException e) {
                // Ignorer aktier med ukendt sector
            }
        }
        
        return filtered;
    }

}
