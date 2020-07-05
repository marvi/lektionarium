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
      desc.append(d.getDate()).append(";");
      desc.append(d.getName()).append(";");
      if (d instanceof HolyDay) {
        HolyDay hd = (HolyDay) d;
        Readings r = hd.getReadings();
        desc.append(hd.getTheme()).append(";");
        desc.append(r.getOt()).append(";");
        desc.append(r.getEp()).append(";");
        desc.append(r.getGo()).append(";");
        desc.append(r.getPs()).append(";");
        if (r.getAlt() != null) {
          desc.append(r.getAlt()).append(";\n");
        } else {
          desc.append(";\n");
        }
      }
    }
    return desc.toString();
  }


}
