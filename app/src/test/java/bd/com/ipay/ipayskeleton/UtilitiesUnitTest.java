package bd.com.ipay.ipayskeleton;

import org.junit.Test;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UtilitiesUnitTest {
    @Test
    public void isValueAvailable_CorrectFormat_ReturnsTrue() {
        assertThat(Utilities.isValueAvailable(new BigDecimal(0.0)), is(true));
        assertThat(Utilities.isValueAvailable(new BigDecimal(90.0)), is(true));
        assertThat(Utilities.isValueAvailable(new BigDecimal(100)), is(true));
    }

    @Test
    public void isValueAvailable_IncorrectFormat_ReturnsFalse() {
        assertThat(Utilities.isValueAvailable(new BigDecimal(-1)), is(false));
    }

    @Test
    public void formatTakaWithComma_CorrectFormat_ReturnsTrue() {
        assertThat(Utilities.formatTakaWithComma(10000000), is("৳10,000,000.00"));
        assertThat(Utilities.formatTakaWithComma(0.3569), is("৳0.36"));
    }

}