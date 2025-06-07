package lectio.format;

import lectio.cal.Day;
import lectio.cal.HolyDay;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.Readings;

import java.time.LocalDate;
import java.util.Map.Entry;

/**
 * Generate an calendar in CSV format for a liturgical calendar year
 * Sample:
 * <pre>
 * Datum;Namn;Tema;GT;Epistel;Evangelium;Alternativ text
 * 2020-01-01;Nyårsdagen;I Jesu namn;Klag 3:22-26;Apg 10:42-43;Joh 2:23-25;Ps 121;;
 * 2020-01-05;Söndagen efter nyår;Guds hus;1 Kung 8:20,27-30;Fil 2:1-4;Joh 2:13-22;Ps 84:2-5;;
 * 2020-01-06;Trettondedag jul;Guds härlighet i Kristus;1 Kung 10:1-7;Ef 2:17-19;Matt 2:1-12;Ps 72:10-15;;
 * </pre>
 * @author marvi
 */
public class CsvFormat {

  /**
   * @param year The calendar year to generate calendar data for
   * @return The calender content as a CSV String
   */
  public static String getCsvForYear(int year) {
    LiturgicalYearFactory lym = new LiturgicalYearFactory();
    StringBuilder desc = new StringBuilder();
    desc.append("Datum;Namn;Tema;GT;Epistel;Evangelium;Alternativ text\n");
    for (Entry<LocalDate, Day> entry : lym.getDaysOfCalendarYear(year).entrySet()) {
      Day d = entry.getValue();
      desc.append(d.date()).append(";");
      desc.append(d.name()).append(";");
      if (d.isHolyDay()) {
        Readings r = d.readings();
        // d.isHolyDay() ensures r is not null
        desc.append(d.theme()).append(";");
        // Check if Reading objects and their SweRefs are null before appending
        desc.append(r.getOt() != null && r.getOt().getSweRef() != null ? r.getOt().getSweRef() : "").append(";");
        desc.append(r.getEp() != null && r.getEp().getSweRef() != null ? r.getEp().getSweRef() : "").append(";");
        desc.append(r.getGo() != null && r.getGo().getSweRef() != null ? r.getGo().getSweRef() : "").append(";");
        desc.append(r.getPs() != null && r.getPs().getSweRef() != null ? r.getPs().getSweRef() : "").append(";");
        if (r.getAlt() != null && r.getAlt().getSweRef() != null) {
          desc.append(r.getAlt().getSweRef()).append(";\n");
        } else {
          desc.append(";\n"); // Still need a semicolon for the alt field, then newline
        }
      } else {
        // For Days that are not HolyDays, add empty fields for theme and all readings
        desc.append(";;;;;;\n"); // 6 empty fields: theme, OT, Ep, Go, Ps, Alt
      }
    }
    return desc.toString();
  }


}
