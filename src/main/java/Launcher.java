import java.util.List;

public class Launcher {
    public static void main(String... args) {
        final List allItems = Shop.getAllItems("test filter");
        System.out.println(allItems);
    }
}
