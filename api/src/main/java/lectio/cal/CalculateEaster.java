package lectio.cal;

import java.time.LocalDate;

/**
 * Calculate Easter Sunday
 * <p>
 * Implements Meeus/Jones/Butcher Gregorian algorithm<br>
 * From: <http://en.wikipedia.org/wiki/Computus><br>
 */
public class CalculateEaster {

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
