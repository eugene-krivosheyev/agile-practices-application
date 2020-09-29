package demo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

//        assertMyCustom();
        assertThat(sut)
            .containsExactly(dummy)
            .hasSize(1)
            .isNotNull();
        //endregion
    }

    @Test
    public void shouldSizeIncrementedAndContainsElementWhenNullElementAdded() {
        sut.add(null);
        assertEquals(1, sut.size());
        assertTrue(sut.contains(null));
    }
}
