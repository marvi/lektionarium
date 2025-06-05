package lectio.format;

import lectio.cal.Day;
import lectio.cal.HolyDay;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.Readings;

import java.time.LocalDate;
import java.util.Map.Entry;
import java.util.SortedMap;

/**
 * Generate an text based liturgical calendar year
 *
 * @author marvi
 */
public class TextFormat {

  /**
   * @param year The calendar year to generate calendar data for
   * @return The calendar in plain text format as a String
   */
  public static String getTextForYear(int year) {
    LiturgicalYearFactory lyf = new LiturgicalYearFactory();
    SortedMap<LocalDate, Day> daysWithReadings = lyf.getDaysOfLiturgicalYear(year).getDaysOfYear();
    StringBuffer desc = new StringBuffer();
    for (Entry<LocalDate, Day> entry : daysWithReadings.entrySet()) {
      Day d = entry.getValue();
      desc.append(d.date()).append(" "); // Use Day's date accessor
      desc.append(d.name()).append("\n"); // Use Day's name accessor
      if (d.isHolyDay()) { // Use the new method from Day record
        Readings r = d.readings(); // Access readings directly from Day
        // Ensure readings is not null before calling methods on it, though isHolyDay should guarantee it.
        if (r != null) {
          desc.append("  ").append(d.theme()).append("\n"); // Access theme via Day's theme() method
          desc.append("  Gammaltestamentlig text: ").append(r.getOt().getSweRef()).append("\n");
          desc.append("  Epistel: ").append(r.getEp().getSweRef()).append("\n");
          desc.append("  Evangelium: ").append(r.getGo().getSweRef()).append("\n");
          desc.append("  Psaltarpsalm: ").append(r.getPs().getSweRef()).append("\n");
          if (r.getAlt() != null) {
            desc.append(r.getAlt().getSweRef()).append("\n");
          } else {
            desc.append("\n");
          }
        }
      }
    }
    return desc.toString();
  }
// No corresponding REPLACE block, this section is removed.


}
