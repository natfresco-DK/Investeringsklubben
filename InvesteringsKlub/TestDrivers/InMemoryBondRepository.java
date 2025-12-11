import CSVHandler.BondRepository;
import CSVHandler.StockRepository;
import Domain.Bond;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InMemoryBondRepository implements BondRepository {

        private final Map<String, Bond> stocks = new HashMap<>();

        private static String norm(String key) {
            return key == null ? null : key.toLowerCase(Locale.ROOT);
        }

        public void addBond(Bond bond) {
            stocks.put(norm(bond.getTicker()), bond);
        }

        public List<Bond> getAllBonds() {
            return new ArrayList<>(stocks.values());
        }

        public Bond getBondByTicker(String ticker) {
            return stocks.get(norm(ticker));
        }
}
