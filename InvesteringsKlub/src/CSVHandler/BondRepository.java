package CSVHandler;

import Domain.Bond;
import java.util.List;

public interface BondRepository {
    List<Bond> getAllBonds();
    Bond getBondByTicker(String ticker);
}

