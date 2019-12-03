import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShopTest {
    @Test
    public void shouldGetAllItemsWhenNoFilterError() {
        final List<Item> items = Shop.getAllItems("dummy filter");

        assertThat(items).containsExactly(
                        new Item(1, "sweeter"),
                        new Item(2, "shapka")
                );
    }
}
