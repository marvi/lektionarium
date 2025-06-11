package lectio.cal;

import junit.framework.TestCase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author marvi
 */
public class ReadingsTest extends TestCase {

  public ReadingsTest(String testName) {
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

  public void testGetDaysOfYear() {


    // OT
    Map<LocalDate, String> otData = new HashMap<>();
    otData.put(LocalDate.of(2020, 4, 10), "Jes 53:1-12");
    otData.put(LocalDate.of(2020, 10, 31), "5 Mos 34:1-5");

    // Test Old Testament readings for specific dates
    for (Map.Entry<LocalDate, String> entry : otData.entrySet()) {
      LocalDate date = entry.getKey();
      String expectedRef = entry.getValue();
      LiturgicalYear ly = new LiturgicalYear(date.getYear());
      LiturgicalDay litDay = ly.getDaysOfYear().get(date);
      assertNotNull("No liturgical day found for date " + date, litDay);
      assertTrue("Expected HolyDay for date " + date, litDay instanceof HolyDay);
      HolyDay holyDay = (HolyDay) litDay;
      Readings readings = holyDay.readings();
      assertEquals("OT reading for " + date + " should be " + expectedRef,
                   expectedRef, readings.getOt().getSweRef());
    }

    // EP
    Map<LocalDate, String> epData = new HashMap<>();
    epData.put(LocalDate.of(2023, 2, 22), "2 Kor 7:8-13");
    epData.put(LocalDate.of(2023, 4, 8), "Ef 2:1−6");
    epData.put(LocalDate.of(2020, 11, 29), "Rom 13:11-14");

    LiturgicalYearFactory factory = new LiturgicalYearFactory();
    // Test Epistle readings for specific dates
    for (Map.Entry<LocalDate, String> entry : epData.entrySet()) {
      LocalDate date = entry.getKey();
      String expectedRef = entry.getValue();
      LiturgicalYear year = factory.getLiturgicalYear(date);
      LiturgicalDay litDay = year.getDaysOfYear().get(date);
      assertNotNull("No liturgical day found for date " + date, litDay);
      assertTrue("Expected HolyDay for date " + date, litDay instanceof HolyDay);
      HolyDay holyDay = (HolyDay) litDay;
      Readings readings = holyDay.readings();
      assertEquals("Epistle reading for " + date + " should be " + expectedRef,
                   expectedRef, readings.getEp().getSweRef());
    }

    // GO
    Map<LocalDate, String> goData = new HashMap<>();
    goData.put(LocalDate.of(2024, 3, 30), "Matt 28:1-8");
    goData.put(LocalDate.of(2024, 5, 12), "Joh 15:26-16:4");

    // Test Gospel readings for specific dates
    for (Map.Entry<LocalDate, String> entry : goData.entrySet()) {
      LocalDate date = entry.getKey();
      String expectedRef = entry.getValue();
      LiturgicalYear ly = new LiturgicalYear(date.getYear());
      LiturgicalDay litDay = ly.getDaysOfYear().get(date);
      assertNotNull("No liturgical day found for date " + date, litDay);
      assertTrue("Expected HolyDay for date " + date, litDay instanceof HolyDay);
      HolyDay holyDay = (HolyDay) litDay;
      Readings readings = holyDay.readings();
      assertEquals("Gospel reading for " + date + " should be " + expectedRef,
                   expectedRef, readings.getGo().getSweRef());
    }
  }
}
