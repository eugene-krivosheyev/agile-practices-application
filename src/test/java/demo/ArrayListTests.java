package demo;

import com.gargoylesoftware.htmlunit.Cache;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayListTests {
    @Test
    public void shouldSizeIncrementedAndContainsElementWhenElementAdded() {
        //region Fixture | Arrange | Given
        final ArrayList<Object> sut = new ArrayList<>();
        final Object dummy = new Object();
        //endregion

        //region Act | When
        sut.add(dummy);
        //endregion

        //region Assert | Then
        assertEquals("", 1, sut.size());
        assertTrue(sut.contains(dummy));
        //endregion
    }
}
