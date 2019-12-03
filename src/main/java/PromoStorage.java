import java.util.ArrayList;
import java.util.List;

public class PromoStorage {
    public List getPromoItems() {
        List promoItems = new ArrayList();

        Item testItem = new Item(2, "shapka");

        promoItems.add(testItem);
        return promoItems;
    }
}
