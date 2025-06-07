package lectio.cal;

import junit.framework.TestCase;
import java.time.LocalDate;
import java.util.*;

/**
  * @author marvi
  */
public class LiturgicalYearFactoryTest extends TestCase {

   public void testLiturgicalYearNotEqualToLiturgicalYear() {
     LiturgicalYearFactory lyf = new LiturgicalYearFactory();
     Day d = lyf.getCurrentDay(LocalDate.of(2020, 12, 6));
     LiturgicalYear y = lyf.getDaysOfLiturgicalYear(2021);
     SortedMap<LocalDate, Day> doy = y.getDaysOfYear();
     assertEquals("Andra söndagen i advent", d.name());
   }

  public void testGetPrevDay() {
    Map<LocalDate, LocalDate> findPrev = new HashMap<>();
    findPrev.put(LocalDate.of(2020, 12, 6),
      LocalDate.of(2020, 11, 29));
    findPrev.put(LocalDate.of(2020, 12, 27),
      LocalDate.of(2020, 12, 26));
    findPrev.put(LocalDate.of(2020, 12, 27),
      LocalDate.of(2020, 12, 26));
    findPrev.put(LocalDate.of(2021, 1, 1),
      LocalDate.of(2020, 12, 27));
    findPrev.put(LocalDate.of(2022, 1, 2),
      LocalDate.of(2022, 1, 1));
    findPrev.put(LocalDate.of(2022, 11, 27),
      LocalDate.of(2022, 11, 20));

    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    Iterator it = findPrev.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<LocalDate, LocalDate> pairs = (Map.Entry) it.next();
      assertEquals(pairs.getValue(), fact.getPreviousDay(pairs.getKey()).date());
    }
  }

  public void testLiturgicalYear() {
    Map<LocalDate, Integer> cornerCases = new HashMap<>();
    cornerCases.put(LocalDate.of(2020, 11, 29), 2021);
    cornerCases.put(LocalDate.of(2024, 11, 29), 2024);
    cornerCases.put(LocalDate.of(2026, 9, 29), 2026);
    cornerCases.put(LocalDate.of(2021, 4, 3), 2021);
    cornerCases.put(LocalDate.of(2023, 12, 3), 2024);
    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    Iterator it = cornerCases.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<LocalDate, LocalDate> pairs = (Map.Entry) it.next();
      assertEquals(pairs.getValue(), fact.getLiturgicalYear(pairs.getKey())
        .getYear());
    }
  }

  public void testGetNextDay() {
    Map<LocalDate, LocalDate> findPrev = new HashMap<>();
    findPrev.put(LocalDate.of(2020, 11, 29),
      LocalDate.of(2020, 12, 6));
    findPrev.put(LocalDate.of(2020, 12, 26),
      LocalDate.of(2020, 12, 27));
    findPrev.put(LocalDate.of(2020, 12, 26),
      LocalDate.of(2020, 12, 27));
    findPrev.put(LocalDate.of(2020, 12, 27),
      LocalDate.of(2021, 1, 1));
    findPrev.put(LocalDate.of(2022, 1, 1),
      LocalDate.of(2022, 1, 2));
    findPrev.put(LocalDate.of(2022, 11, 20),
      LocalDate.of(2022, 11, 27));
    findPrev.put(LocalDate.of(2025, 11, 25),
      LocalDate.of(2025, 11, 30));
    findPrev.put(LocalDate.of(2025, 12, 28),
      LocalDate.of(2026, 1, 1));

    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    Iterator it = findPrev.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<LocalDate, LocalDate> pairs = (Map.Entry) it.next();
      assertEquals(pairs.getValue(), fact.getNextDay(pairs.getKey()).date());
    }
  }


 }

