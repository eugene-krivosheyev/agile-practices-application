import java.util.ArrayList;
import java.util.List;

public class StubPromoStorage implements PromoStorage {
    @Override
    public List getPromoItems() {
        List promoItems = new ArrayList();

        Item testItem = new Item(2, "shapka");

        promoItems.add(testItem);
        return promoItems;
    }
}
