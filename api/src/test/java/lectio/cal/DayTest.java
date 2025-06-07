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
    List<Memorial> mems = new ArrayList<>();
    List<Day> days = new ArrayList<>();
    days.add(new Day("Gazonk", LocalDate.of(2010, 1, 2), mems, LiturgicalColor.WHITE, null));
    days.add(new Day("Foo", LocalDate.of(1910, 1, 1), mems, LiturgicalColor.WHITE, null));
    days.add(new Day("Bar", LocalDate.of(2010, 1, 1), mems, LiturgicalColor.WHITE, null));
    assertFalse("Should not have matched", days.get(2).name().equals("Gazonk"));

    days.sort(Comparator.comparing(Day::date));
    assertEquals("Should have been sorted", days.get(0).name(), "Foo");
    assertEquals("Should have been sorted", days.get(1).name(), "Bar");
    assertEquals("Should have been sorted", days.get(2).name(), "Gazonk");

  }
}
