package lectio.format;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author marvi
 */
public class IcalFormatTest {


  /**
   * Test of getIcalForYear method, of class IcalFormat.
   */
  @Test
  public void testGetIcalForYear() {
    // Test that getIcalForYear returns a non-null, non-empty string for a valid year
    int year = 2017;
    String result = IcalFormat.getIcalForYear(year);
    assertNotNull("iCal result should not be null", result);
    assertTrue("iCal result should not be empty", !result.isEmpty());
    // Optionally, print a snippet for manual inspection
    System.out.println("iCal output (first 200 chars):\n" + result.substring(0, Math.min(200, result.length())));
  }
}
