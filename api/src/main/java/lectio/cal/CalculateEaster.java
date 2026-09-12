package lectio.cal;

import java.time.LocalDate;

/**
 * Räknar ut påskdagen i den gregorianska kalendern.
 * <p>
 * Algoritmen är Meeus/Jones/Butcher, se
 * <a href="https://en.wikipedia.org/wiki/Date_of_Easter">Date of Easter</a>.
 * Den gäller alla år från 1583 och framåt.
 *
 * @author marvi
 */
public final class CalculateEaster {

  private CalculateEaster() {
  }

  /**
   * @param year kalenderår
   * @return påskdagen det året
   */
  public static LocalDate forYear(int year) {

    int a = year % 19;
    int b = year / 100;
    int c = year % 100;
    int d = b / 4;
    int e = b % 4;
    int f = (b + 8) / 25;
    int g = (b - f + 1) / 3;
    int h = (19 * a + b - d - g + 15) % 30;
    int i = c / 4;
    int k = c % 4;
    int L = (32 + 2 * e + 2 * i - h - k) % 7;
    int m = (a + 11 * h + 22 * L) / 451;

    int month = (h + L - 7 * m + 114) / 31;
    int day = ((h + L - 7 * m + 114) % 31) + 1;

    return LocalDate.of(year, month, day);
  }
}
