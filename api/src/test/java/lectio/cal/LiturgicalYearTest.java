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
public class LiturgicalYearTest extends TestCase {

  public LiturgicalYearTest(String testName) {
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

    Map<LocalDate, String> testData = new HashMap<LocalDate, String>();

    testData.put(LocalDate.of(2010, 3, 28), "Palmsöndagen");
    testData.put(LocalDate.of(2011, 4, 21), "Skärtorsdagen");
    testData.put(LocalDate.of(2013, 3, 29), "Långfredagen");
    testData.put(LocalDate.of(2022, 4, 17), "Påskdagen");
    testData.put(LocalDate.of(2026, 4, 3), "Långfredagen");
    testData.put(LocalDate.of(2012, 4, 9), "Annandag påsk");

    testData.put(LocalDate.of(2011, 1, 2), "Söndagen efter nyår");
    testData.put(LocalDate.of(2015, 1, 4), "Söndagen efter nyår");
    testData.put(LocalDate.of(2012, 1, 6), "Trettondedag jul");

    testData.put(LocalDate.of(2011, 1, 9), "Första söndagen efter trettondedagen");
    testData.put(LocalDate.of(2019, 1, 13), "Första söndagen efter trettondedagen");
    testData.put(LocalDate.of(2011, 1, 16), "Andra söndagen efter trettondedagen");
    testData.put(LocalDate.of(2012, 1, 22), "Tredje söndagen efter trettondedagen");

    testData.put(LocalDate.of(2011, 2, 13), "Sjätte söndagen efter trettondedagen");
    testData.put(LocalDate.of(2057, 2, 11), "Sjätte söndagen efter trettondedagen");

    testData.put(LocalDate.of(2011, 3, 27), "Jungfru Marie bebådelsedag");
    testData.put(LocalDate.of(2012, 3, 25), "Jungfru Marie bebådelsedag");
    testData.put(LocalDate.of(2013, 3, 17), "Jungfru Marie bebådelsedag");

    testData.put(LocalDate.of(2012, 2, 5), "Kyndelsmässodagen");
    testData.put(LocalDate.of(2014, 2, 2), "Kyndelsmässodagen");
    testData.put(LocalDate.of(2013, 2, 3), "Kyndelsmässodagen");
    testData.put(LocalDate.of(2012, 2, 12), "Sexagesima");

    testData.put(LocalDate.of(2012, 6, 23), "Midsommardagen");
    testData.put(LocalDate.of(2011, 6, 25), "Midsommardagen");
    testData.put(LocalDate.of(2018, 6, 23), "Midsommardagen");
    testData.put(LocalDate.of(2020, 6, 20), "Midsommardagen");

    testData.put(LocalDate.of(2013, 7, 14), "Kristi förklarings dag");
    testData.put(LocalDate.of(2008, 7, 6), "Kristi förklarings dag");
    testData.put(LocalDate.of(2011, 8, 7), "Kristi förklarings dag");

    testData.put(LocalDate.of(2011, 10, 9), "Tacksägelsedagen");
    testData.put(LocalDate.of(2014, 10, 12), "Tacksägelsedagen");
    testData.put(LocalDate.of(2024, 10, 13), "Tacksägelsedagen");

    testData.put(LocalDate.of(2011, 10, 2), "Den helige Mikaels dag");


    Iterator it = testData.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry) it.next();
      LocalDate date = (LocalDate) pairs.getKey();
      int year = date.getYear();
      assertEquals(new LiturgicalYear(year).getDaysOfYear().get(date).name(), pairs.getValue()); // Replaced getName() with name()
    }

    Map<LocalDate, String> testDataChristmas = new HashMap<LocalDate, String>();

    testDataChristmas.put(LocalDate.of(2013, 12, 29), "Söndagen efter jul");
    testDataChristmas.put(LocalDate.of(2020, 12, 27), "Söndagen efter jul");
    testDataChristmas.put(LocalDate.of(2015, 11, 29), "Första söndagen i advent");
    testDataChristmas.put(LocalDate.of(2014, 11, 30), "Första söndagen i advent");
    testDataChristmas.put(LocalDate.of(2024, 12, 29), "Söndagen efter jul");
    testDataChristmas.put(LocalDate.of(2023, 12, 24), "Fjärde söndagen i advent");
    testDataChristmas.put(LocalDate.of(2020, 12, 6), "Andra söndagen i advent");

    it = testDataChristmas.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry) it.next();
      LocalDate date = (LocalDate) pairs.getKey();
      int year = date.getYear();
      year++;
      assertEquals(new LiturgicalYear(year).getDaysOfYear().get(date).name(), pairs.getValue()); // Replaced getName() with name()
    }


  }

  public void testLiturgicalYearNotEqualToCalendarYear() {
    LiturgicalYear y2021 = new LiturgicalYear(2021);
    assertTrue(y2021.getDaysOfYear().containsKey(LocalDate.of(2020, 12, 6)));
  }


  public void testReadingCycle() {
    assertEquals(2, new LiturgicalYear(2007).getReadingCycle());
    assertEquals(2, new LiturgicalYear(2010).getReadingCycle());
    assertEquals(3, new LiturgicalYear(2011).getReadingCycle());
    assertEquals(2, new LiturgicalYear(2013).getReadingCycle());
    assertEquals(3, new LiturgicalYear(2020).getReadingCycle());
    assertEquals(1, new LiturgicalYear(2021).getReadingCycle());
  }



  public void testEasterSeries() {
    assertEquals(1, LiturgicalYear.getEasterSeries(2004));
    assertEquals(1, LiturgicalYear.getEasterSeries(2012));
    assertEquals(3, LiturgicalYear.getEasterSeries(2010));
    assertEquals(2, LiturgicalYear.getEasterSeries(2009));
    assertEquals(1, LiturgicalYear.getEasterSeries(2008));
    assertEquals(4, LiturgicalYear.getEasterSeries(2011));
    assertEquals(1, LiturgicalYear.getEasterSeries(2020));
    assertEquals(2, LiturgicalYear.getEasterSeries(2021));

  }

    /*
    public void testToString() {
        LiturgicalYear y = new LiturgicalYear(2011);
        System.out.println(y.toString());
    }

  public void testGetCalendarDays() {
      LiturgicalYear y = new LiturgicalYear(2011);
      StringBuilder sb = new StringBuilder();
      sb.append("CalendarYear{ ");
      Set<LocalDate> days = y.getDaysOfYear().keySet();
      System.out.println("Days: " + days);
      for (LocalDate ld : days) {
          sb.append(y.getDaysOfYear().get(ld).toString());
          sb.append(" \n ");
      }
      sb.append(" }");
      System.out.println(sb.toString());
  }*/

  public void testNextWeekdayOfType() {
    assertEquals(LocalDate.of(1985, 1, 7), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.MONDAY, LocalDate.of(1984, 12, 31)));
    assertEquals(LocalDate.of(1976, 3, 2), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.TUESDAY, LocalDate.of(1976, 2, 29)));
    assertEquals(LocalDate.of(2017, 10, 25), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.WEDNESDAY, LocalDate.of(2017, 10, 19)));
    assertEquals(LocalDate.of(2001, 6, 14), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.THURSDAY, LocalDate.of(2001, 6, 11)));
    assertEquals(LocalDate.of(2013, 1, 4), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.FRIDAY, LocalDate.of(2012, 12, 30)));
    assertEquals(LocalDate.of(2015, 2, 21), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.SATURDAY, LocalDate.of(2015, 2, 14)));
    assertEquals(LocalDate.of(1967, 11, 12), LiturgicalYear.nextWeekdayOfType(
      DayOfWeek.SUNDAY, LocalDate.of(1967, 11, 7)));
  }

  public void testGetHolyDayByNameBehavior() {
    LiturgicalYear year2021 = new LiturgicalYear(2021);

    // Test with a known holy day
    HolyDay easterDay = year2021.getHolyDayByName("Påskdagen");
    assertNotNull("Påskdagen should be found as a HolyDay in 2021", easterDay);
    assertTrue("Day object within Påskdagen HolyDay should report isHolyDay() as true", easterDay.day().isHolyDay());
    assertNotNull("Påskdagen HolyDay should have non-null readings", easterDay.readings());
    assertEquals("Påskdagen theme should match", easterDay.readings().getTheme(), easterDay.day().theme());


    // Test with a non-existent day name
    HolyDay nonExistentDay = year2021.getHolyDayByName("En Ickeexisterande Dag");
    assertNull("A non-existent day should not be found as a HolyDay", nonExistentDay);

    // Test with a day name that exists but might not have readings (or is not considered a "HolyDay" by getHolyDayByName)
    // "Onsdag i Stilla veckan" is created by LiturgicalYear.
    // Whether it's a "HolyDay" via getHolyDayByName depends on whether it has readings in lectio.json
    // and if getHolyDayByName filters for days with readings (which it now does).
    HolyDay wednesdayHolyWeek = year2021.getHolyDayByName("Onsdag i Stilla veckan");
    Day dayObjectWednesday = year2021.getDaysOfYear().get(CalculateEaster.forYear(2021).minusDays(4));
    assertNotNull("Onsdag i Stilla veckan should exist as a Day object", dayObjectWednesday);

    if (dayObjectWednesday.readings() != null) {
      assertNotNull("If 'Onsdag i Stilla veckan' has readings, getHolyDayByName should return it", wednesdayHolyWeek);
      assertTrue(wednesdayHolyWeek.day().isHolyDay());
    } else {
      assertNull("If 'Onsdag i Stilla veckan' has no readings, getHolyDayByName should return null", wednesdayHolyWeek);
      assertFalse(dayObjectWednesday.isHolyDay());
    }
  }

  public void testDayReadingsPopulation() {
    LiturgicalYear year2021 = new LiturgicalYear(2021);
    Map<LocalDate, Day> daysOfYear = year2021.getDaysOfYear();

    // Easter Sunday in 2021 is April 4th
    LocalDate easterDate2021 = LocalDate.of(2021, 4, 4);
    Day easterDay = daysOfYear.get(easterDate2021);
    assertNotNull("Easter Sunday (2021-04-04) should exist in the map", easterDay);
    assertTrue("Easter Sunday should be a holy day", easterDay.isHolyDay());
    assertNotNull("Easter Sunday should have non-null readings", easterDay.readings());
    assertEquals("Påskdagen", easterDay.name());

    // Wednesday in Holy Week (2021-03-31)
    LocalDate wednesdayHolyWeekDate = CalculateEaster.forYear(2021).minusDays(4); // 2021-03-31
    Day wednesdayHolyWeek = daysOfYear.get(wednesdayHolyWeekDate);
    assertNotNull("Wednesday in Holy Week should exist in the map", wednesdayHolyWeek);
    assertEquals("Onsdag i Stilla veckan", wednesdayHolyWeek.name());
    // Check if it has readings - this depends on lectio.json
    if (wednesdayHolyWeek.readings() != null) {
      assertTrue("If Wednesday in Holy Week has readings, isHolyDay() should be true", wednesdayHolyWeek.isHolyDay());
      assertNotNull("Theme should not be null if readings exist", wednesdayHolyWeek.theme());
    } else {
      assertFalse("If Wednesday in Holy Week has no readings, isHolyDay() should be false", wednesdayHolyWeek.isHolyDay());
      assertNull("Theme should be null if no readings", wednesdayHolyWeek.theme());
    }

    // A day that is likely not a "named" holy day with specific readings, e.g., a random day after Christmas
    // However, LiturgicalYear only populates specific days.
    // Let's check "Juldagen" (Christmas Day for the liturgical year starting Advent 2020)
    // This would be 2020-12-25 for LiturgicalYear(2021)
    LocalDate christmasDayDate = LocalDate.of(2020, 12, 25);
    Day christmasDay = daysOfYear.get(christmasDayDate);
    assertNotNull("Christmas Day (2020-12-25) should exist for LiturgicalYear 2021", christmasDay);
    assertEquals("Juldagen", christmasDay.name());
    if (christmasDay.readings() != null) {
        assertTrue("Christmas Day should be a holy day if it has readings", christmasDay.isHolyDay());
        assertNotNull(christmasDay.readings());
        assertNotNull(christmasDay.theme());
    } else {
        assertFalse("Christmas Day should not be a holy day if it has no readings", christmasDay.isHolyDay());
        assertNull(christmasDay.readings());
        assertNull(christmasDay.theme());
    }
  }
}
