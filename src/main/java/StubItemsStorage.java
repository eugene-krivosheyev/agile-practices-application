import java.util.ArrayList;
import java.util.List;

public class StubItemsStorage implements ItemsStorage {
    public List getStoreItems() {
        List storeItems = new ArrayList();
        Item testItem = new Item(1, "sweeter");
        storeItems.add(testItem);
        return storeItems;
    }
}
