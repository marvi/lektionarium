package lectio.cal;

import junit.framework.TestCase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author marvi
 */
public class DayTest extends TestCase {

  public DayTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCompareTo() {
    // Test compareTo and sorting of Day records
    List<Memorial> mems = new ArrayList<>();
    List<Day> days = new ArrayList<>();
    days.add(new Day("Gazonk", LocalDate.of(2010, 1, 2), mems, LiturgicalColor.WHITE));
    days.add(new Day("Foo", LocalDate.of(1910, 1, 1), mems, LiturgicalColor.WHITE));
    days.add(new Day("Bar", LocalDate.of(2010, 1, 1), mems, LiturgicalColor.WHITE));

    // Check that the names are as expected before sorting
    assertEquals("Expected 'Bar' as third before sort", "Bar", days.get(2).name());

    // Sort by date
    days.sort(Comparator.comparing(Day::date));

    // After sorting, check the order explicitly
    assertEquals("First after sort should be 'Foo'", "Foo", days.get(0).name());
    assertEquals("Second after sort should be 'Bar'", "Bar", days.get(1).name());
    assertEquals("Third after sort should be 'Gazonk'", "Gazonk", days.get(2).name());

  }
}
