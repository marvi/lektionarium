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
    // Added null for the new 'readings' parameter in Day constructor
    days.add(new Day("Gazonk", LocalDate.of(2010, 1, 2), mems, LiturgicalColor.WHITE, null));
    days.add(new Day("Foo", LocalDate.of(1910, 1, 1), mems, LiturgicalColor.WHITE, null));
    days.add(new Day("Bar", LocalDate.of(2010, 1, 1), mems, LiturgicalColor.WHITE, null));
    assertFalse("Should not have matched", days.get(2).name().equals("Gazonk")); // Replaced getName() with name()

    days.sort(Comparator.comparing(Day::date)); // Replaced Day::getDate with Day::date
    assertEquals("Should have been sorted", days.get(0).name(), "Foo"); // Replaced getName() with name()
    assertEquals("Should have been sorted", days.get(1).name(), "Bar"); // Replaced getName() with name()
    assertEquals("Should have been sorted", days.get(2).name(), "Gazonk"); // Replaced getName() with name()
  }

  public void testDayRecordBehavior() {
    // Create mock Readings
    Readings mockReadings = new Readings("Test Theme", null, null, null, null, null);

    // Day instance with Readings
    Day dayWithReadings = new Day("Holy Day", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.WHITE, mockReadings);
    // Day instance with null Readings
    Day dayWithoutReadings = new Day("Ordinary Day", LocalDate.of(2023, 1, 2), Collections.emptyList(), LiturgicalColor.GREEN, null);

    // Test isHolyDay()
    assertTrue("isHolyDay() should be true when Readings is not null", dayWithReadings.isHolyDay());
    assertFalse("isHolyDay() should be false when Readings is null", dayWithoutReadings.isHolyDay());

    // Test theme()
    assertEquals("theme() should return the theme from Readings", "Test Theme", dayWithReadings.theme());
    assertNull("theme() should be null when Readings is null", dayWithoutReadings.theme());

    // Test compareTo()
    Day day1 = new Day("Day1", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day day2 = new Day("Day2", LocalDate.of(2023, 1, 2), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day day3 = new Day("Day3", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day dayNullDate1 = new Day("NullDate1", null, Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day dayNullDate2 = new Day("NullDate2", null, Collections.emptyList(), LiturgicalColor.GREEN, null);


    assertTrue("day1.compareTo(day2) should be < 0", day1.compareTo(day2) < 0);
    assertTrue("day2.compareTo(day1) should be > 0", day2.compareTo(day1) > 0);
    assertEquals("day1.compareTo(day3) should be 0", 0, day1.compareTo(day3));

    // Test compareTo with null dates (as per updated Day.compareTo)
    assertTrue("day1.compareTo(dayNullDate1) should be > 0", day1.compareTo(dayNullDate1) > 0);
    assertTrue("dayNullDate1.compareTo(day1) should be < 0", dayNullDate1.compareTo(day1) < 0);
    assertEquals("dayNullDate1.compareTo(dayNullDate2) should be 0", 0, dayNullDate1.compareTo(dayNullDate2));


    // Test equals() and hashCode()
    Day day1_copy = new Day("Day1", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day day_different_name = new Day("Different Name", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day day_different_date = new Day("Day1", LocalDate.of(2023, 1, 2), Collections.emptyList(), LiturgicalColor.GREEN, null);
    Day day_different_readings = new Day("Day1", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.GREEN, mockReadings);


    assertEquals("Day instances with same data should be equal", day1, day1_copy);
    assertEquals("Day instances with same data should have same hashCode", day1.hashCode(), day1_copy.hashCode());

    assertFalse("Day instances with different names should not be equal", day1.equals(day_different_name));
    // Note: hashCode might be the same if only name differs and other fields dominate, which is fine.
    // For records, all fields participate in equals and hashCode.

    assertFalse("Day instances with different dates should not be equal", day1.equals(day_different_date));
    assertFalse("Day instances with different readings should not be equal", day1.equals(day_different_readings));

    // Test equality with the dayWithReadings and dayWithoutReadings
    Day dayWithReadings_copy = new Day("Holy Day", LocalDate.of(2023, 1, 1), Collections.emptyList(), LiturgicalColor.WHITE, mockReadings);
    assertEquals("Day with readings should be equal to its copy", dayWithReadings, dayWithReadings_copy);
    assertEquals("Day with readings should have same hashCode as its copy", dayWithReadings.hashCode(), dayWithReadings_copy.hashCode());
    assertFalse("Day with readings should not be equal to day without readings", dayWithReadings.equals(dayWithoutReadings));

  }
}
