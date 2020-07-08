/*
 * Copyright (c) 2006, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.format;

import lectio.cal.Day;
import lectio.cal.HolyDay;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.Readings;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generate an iCalendar String for a liturgical calendar year
 *
 * @author marvi
 */
public class IcalFormat {

  /**
   * @param year The calendar year to generate iCaledanr data for
   * @return The iCalendar content as a String
   */
  public static String getIcalForYear(int year) {
    LiturgicalYearFactory lyf = new LiturgicalYearFactory();
    RandomUidGenerator ug = new RandomUidGenerator();
    SortedMap<LocalDate, Day> daysOfYear = lyf.getDaysOfLiturgicalYear(year).getDaysOfYear();
    Calendar calendar = new Calendar();
    calendar.getProperties().add(new ProdId("-//marvi.io//lektionarium//EN"));
    calendar.getProperties().add(Version.VERSION_2_0);
    calendar.getProperties().add(CalScale.GREGORIAN);
    for (Entry<LocalDate, Day> entry : daysOfYear.entrySet()) {
      LocalDate date = entry.getKey();
      Day d = entry.getValue();
      java.util.Calendar cal = java.util.Calendar.getInstance();
      cal.clear();
      cal.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth(), 9, 0, 0);
      VEvent day = new VEvent(new Date(cal.getTime()), d.getName());
      day.getProperties().add(ug.generateUid());
      if (d instanceof HolyDay) {
        HolyDay hd = (HolyDay) d;
        addReadings(day, hd.getReadings());
      }
      calendar.getComponents().add(day);
    }
    CalendarOutputter outputter = new CalendarOutputter(false);
    StringWriter strwr = new StringWriter();
    try {
      outputter.output(calendar, strwr);
    } catch (IOException ex) {
      Logger.getLogger(IcalFormat.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException("Could not write calendar", ex);
    }

    return strwr.toString();
  }




  private static void addReadings(VEvent e, Readings r) {
    String desc = r.getTheme() + "\r\n" +
      "GT: " + r.getOt().getSweRef() + "\r\n" +
      "Ep: " + r.getEp().getSweRef()+ "\r\n" +
      "Ev: " + r.getGo().getSweRef() + "\r\n" +
      "Ps: " + r.getPs().getSweRef() + "\r\n";
    e.getProperties().add(new Description(desc));
  }

}
