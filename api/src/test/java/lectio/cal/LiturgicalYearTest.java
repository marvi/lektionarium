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
      assertEquals(new LiturgicalYear(year).getDaysOfYear().get(date).name(), pairs.getValue());
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
      assertEquals(new LiturgicalYear(year).getDaysOfYear().get(date).name(), pairs.getValue());
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

}
