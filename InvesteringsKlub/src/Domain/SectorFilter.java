package Domain;
import CSVHandler.*;
import java.util.*;

public class SectorFilter {
    
    // Gruppér aktier efter sektor
    public static Map<Sektor, List<Stock>> groupBySektor(List<Stock> stocks) {
        Map<Sektor, List<Stock>> grouped = new HashMap<>();
        
        // Initialiser alle sektorer med tomme lister
        for (Sektor sektor : Sektor.values()) {
            grouped.put(sektor, new ArrayList<>());
        }
        
        // Gruppér aktier
        for (Stock stock : stocks) {
            try {
                Sektor sektor = Sektor.fromString(stock.getSector());
                grouped.get(sektor).add(stock);
            } catch (IllegalArgumentException e) {
                // Ignorer aktier med ukendt sektor
                System.err.println("Warning: Unknown sector for " + stock.getTicker() + ": " + stock.getSector());
            }
        }
        
        return grouped;
    }
    
    // Filtrer aktier baseret på én sektor
    public static List<Stock> filterBySektor(List<Stock> stocks, Sektor sektor) {
        List<Stock> filtered = new ArrayList<>();
        
        for (Stock stock : stocks) {
            try {
                Sektor stockSektor = Sektor.fromString(stock.getSector());
                if (stockSektor == sektor) {
                    filtered.add(stock);
                }
            } catch (IllegalArgumentException e) {
                // Ignorer aktier med ukendt sektor
            }
        }
        
        return filtered;
    }
    
    // Print oversigt over alle sektorer og deres aktier
    public static void printSectorOverview(List<Stock> stocks) {
        Map<Sektor, List<Stock>> grouped = groupBySektor(stocks);
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("           OVERSIGT OVER AKTIER PER SEKTOR");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        for (Sektor sektor : Sektor.values()) {
            List<Stock> sektorStocks = grouped.get(sektor);
            System.out.println( sektor.getDisplayName() + " (" + sektorStocks.size() + " aktier):");
            System.out.println("───────────────────────────────────────────────────────────");
            
            if (sektorStocks.isEmpty()) {
                System.out.println("  Ingen aktier i denne sektor");
            } else {
                for (Stock stock : sektorStocks) {
                    System.out.printf("  • %-10s - %-30s %8.2f %s%n", 
                        stock.getTicker(), 
                        stock.getName(), 
                        stock.getPrice(), 
                        stock.getCurrency());
                }
            }
            System.out.println();
        }
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }
}
