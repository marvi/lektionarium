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
      desc.append(d.date()).append(" ");
      desc.append(d.name()).append("\n");
      if (d.isHolyDay()) {
        Readings r = d.readings();
        // d.isHolyDay() ensures r is not null.
        desc.append("  ").append(d.theme()).append("\n"); // Use d.theme()
        if (r.getOt() != null && r.getOt().getSweRef() != null) desc.append("  Gammaltestamentlig text: ").append(r.getOt().getSweRef()).append("\n");
        if (r.getEp() != null && r.getEp().getSweRef() != null) desc.append("  Epistel: ").append(r.getEp().getSweRef()).append("\n");
        if (r.getGo() != null && r.getGo().getSweRef() != null) desc.append("  Evangelium: ").append(r.getGo().getSweRef()).append("\n");
        if (r.getPs() != null && r.getPs().getSweRef() != null) desc.append("  Psaltarpsalm: ").append(r.getPs().getSweRef()).append("\n");
        if (r.getAlt() != null && r.getAlt().getSweRef() != null) {
          desc.append("  Alternativ text: ").append(r.getAlt().getSweRef()).append("\n"); // Added a label for clarity
        } else {
          desc.append("\n"); // Keep newline for spacing if no alt text
        }
      }
    }
    return desc.toString();
  }


}
