package lectio.cal;

import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;

/**
  * @author marvi
  */
public class LiturgicalYearFactoryTest extends TestCase {

   public void testLiturgicalYearNotEqualToLiturgicalYear() {
     // Test that the correct liturgical day is returned for a specific date
     LiturgicalYearFactory lyf = new LiturgicalYearFactory();
     LocalDate testDate = LocalDate.of(2020, 12, 6);
     LiturgicalDay currentDay = lyf.getCurrentDay(testDate);
     assertEquals("Expected 'Andra söndagen i advent' for 2020-12-06", "Andra söndagen i advent", currentDay.name());
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

    // Test previous day calculation for a set of dates
    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    for (Map.Entry<LocalDate, LocalDate> entry : findPrev.entrySet()) {
      LocalDate input = entry.getKey();
      LocalDate expectedPrev = entry.getValue();
      LiturgicalDay prevDay = fact.getPreviousDay(input);
      assertEquals("Previous day for " + input + " should be " + expectedPrev,
                   expectedPrev, prevDay.date());
    }
  }

  public void testLiturgicalYear() {
    Map<LocalDate, Integer> cornerCases = new HashMap<>();
    cornerCases.put(LocalDate.of(2020, 11, 29), 2021);
    cornerCases.put(LocalDate.of(2024, 11, 29), 2024);
    cornerCases.put(LocalDate.of(2026, 9, 29), 2026);
    cornerCases.put(LocalDate.of(2021, 4, 3), 2021);
    cornerCases.put(LocalDate.of(2023, 12, 3), 2024);
    // Test liturgical year calculation for corner cases
    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    for (Map.Entry<LocalDate, Integer> entry : cornerCases.entrySet()) {
      LocalDate input = entry.getKey();
      int expectedYear = entry.getValue();
      int actualYear = fact.getLiturgicalYear(input).getYear();
      assertEquals("Liturgical year for date " + input + " should be " + expectedYear,
                   expectedYear, actualYear);
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

    // Test next day calculation for a set of dates
    LiturgicalYearFactory fact = new LiturgicalYearFactory();
    for (Map.Entry<LocalDate, LocalDate> entry : findPrev.entrySet()) {
      LocalDate input = entry.getKey();
      LocalDate expectedNext = entry.getValue();
      LiturgicalDay nextDay = fact.getNextDay(input);
      assertEquals("Next day for " + input + " should be " + expectedNext,
                   expectedNext, nextDay.date());
    }
  }


 }
