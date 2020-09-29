package demo;

import com.gargoylesoftware.htmlunit.Cache;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

public class ArrayListTests {
    private ArrayList<Object> sut;

    public ArrayListTests() {
        System.out.println("Tst instance cretaed!!!!");
    }

    @Before
    public void setUp() {
        sut = new ArrayList<>();
    }

    @Test
    public void shouldSizeIncrementedAndContainsElementWhenElementAdded() {
        //region Fixture | Arrange | Given
        final String dummy = "?????";
        //endregion

        //region Act | When
        sut.add(dummy);
        //endregion

        //region Assert | Then
        assertEquals("", 1, sut.size());
        assertTrue(sut.contains(dummy));

        assertThat(sut, allOf(
                hasItem("null"),
                nullValue()
        ));
        //endregion
    }

    @Test
    public void shouldSizeIncrementedAndContainsElementWhenNullElementAdded() {
        sut.add(null);
        assertEquals(1, sut.size());
        assertTrue(sut.contains(null));
    }
}
