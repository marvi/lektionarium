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
    Map<LocalDate, String> otData = new HashMap<LocalDate, String>();
    otData.put(LocalDate.of(2020, 4, 10), "Jes 53:1-12");
    otData.put(LocalDate.of(2020, 10, 31), "5 Mos 34:1-5");

    Iterator it = otData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry) it.next();
      LocalDate date = (LocalDate) pairs.getKey();
      Day day = new LiturgicalYear(date.getYear()).getDaysOfYear().get(date);
      assertTrue("Day " + date + " (" + day.name() + ") should be a HolyDay for this test.", day.isHolyDay());
      Readings r = day.readings();
      assertEquals(r.getOt().getSweRef(), pairs.getValue());
    }

    // EP
    Map<LocalDate, String> epData = new HashMap<LocalDate, String>();
    epData.put(LocalDate.of(2023, 2, 22), "2 Kor 7:8-13");
    epData.put(LocalDate.of(2023, 4, 8), "Ef 2:1−6");
    epData.put(LocalDate.of(2020, 11, 29), "Rom 13:11-14");

    LiturgicalYearFactory factory = new LiturgicalYearFactory();
    Iterator it2 = epData.entrySet().iterator();
    while (it2.hasNext()) {
      Map.Entry pairs = (Map.Entry) it2.next();
      LocalDate date = (LocalDate) pairs.getKey();
      LiturgicalYear year = factory.getLiturgicalYear(date);
      Day day = year.getDaysOfYear().get(date);
      assertTrue("Day " + date + " (" + day.name() + ") should be a HolyDay for this test.", day.isHolyDay());
      Readings r = day.readings();
      assertEquals(pairs.getValue(), r.getEp().getSweRef());
    }

    // GO
    Map<LocalDate, String> goData = new HashMap<LocalDate, String>();
    goData.put(LocalDate.of(2024, 3, 30), "Matt 28:1-8");
    goData.put(LocalDate.of(2024, 5, 12), "Joh 15:26-16:4");

    Iterator it3 = goData.entrySet().iterator();
    while (it3.hasNext()) {
      Map.Entry pairs = (Map.Entry) it3.next();
      LocalDate date = (LocalDate) pairs.getKey();
      Day day = new LiturgicalYear(date.getYear()).getDaysOfYear().get(date);
      assertTrue("Day " + date + " (" + day.name() + ") should be a HolyDay for this test.", day.isHolyDay());
      Readings r = day.readings();
      assertEquals(r.getGo().getSweRef(), pairs.getValue());
    }
  }
}
