package lectio.format;

import lectio.cal.Day; // Added import for Day
import lectio.cal.LiturgicalYearFactory;
import org.json.JSONArray;

import java.time.LocalDate; // Added import for LocalDate
import java.util.SortedMap;

/**
 * Generate an JSON document for a liturgical calendar year
 *
 * @author marvi
 */
public class JsonFormat {

  /**
   * @param year The calendar year to generate JSON data for
   * @return The calendar content as a JSON String
   */
  public static String getJsonForYear(int year) {
    LiturgicalYearFactory lyf = new LiturgicalYearFactory();
    // Changed raw type SortedMap to SortedMap<LocalDate, Day>
    SortedMap<LocalDate, Day> daysWithReadings = lyf.getDaysOfLiturgicalYear(year).getDaysOfYear();
    JSONArray jsonCal = new JSONArray(daysWithReadings.values());
    return jsonCal.toString(4);
  }

}
