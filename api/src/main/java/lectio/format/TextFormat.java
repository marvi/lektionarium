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
      desc.append(d.date() + " "); // Replaced getDate() with date()
      desc.append(d.name() + "\n"); // Replaced getName() with name()
      if (d instanceof HolyDay hd) { // Used pattern matching for instanceof
        // HolyDay hd = (HolyDay) d; // Cast removed
        Readings r = hd.readings(); // Replaced getReadings() with readings()
        desc.append("  " + hd.theme() + "\n"); // Replaced getTheme() with theme()
        desc.append("  Gammaltestamentlig text: " + r.getOt().getSweRef() + "\n");
        desc.append("  Epistel: " + r.getEp().getSweRef() + "\n");
        desc.append("  Evangelium: " + r.getGo().getSweRef() + "\n");
        desc.append("  Psaltarpsalm: " + r.getPs().getSweRef() + "\n");
        if (r.getAlt() != null) {
          desc.append(r.getAlt().getSweRef() + "\n");
        } else {
          desc.append("\n");
        }
      }
    }
    return desc.toString();
  }


}
