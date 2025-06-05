/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
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
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.property.CalendarScale;
import biweekly.property.Description;
import biweekly.property.Summary;
import biweekly.property.Uid;
import biweekly.property.Version;
import biweekly.ICalVersion;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;
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
    SortedMap<LocalDate, Day> daysOfYear = lyf.getDaysOfLiturgicalYear(year).getDaysOfYear();
    ICalendar calendar = new ICalendar();
    calendar.setProductId("-//marvi.io//lektionarium//EN");
    calendar.setVersion(ICalVersion.V2_0);
    calendar.setCalendarScale(CalendarScale.gregorian());

    for (Entry<LocalDate, Day> entry : daysOfYear.entrySet()) {
      LocalDate date = entry.getKey();
      Day d = entry.getValue();

      VEvent event = new VEvent();
      event.setSummary(new Summary(d.getName()));
      event.setDateStart(java.util.Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
      event.setUid(new Uid(UUID.randomUUID().toString()));

      if (d instanceof HolyDay) {
        HolyDay hd = (HolyDay) d;
        addReadings(event, hd.getReadings());
      }
      calendar.addEvent(event);
    }

    StringWriter strwr = new StringWriter();
    ICalWriter writer = new ICalWriter(strwr, ICalVersion.V2_0);
    try {
      writer.write(calendar);
    } catch (IOException ex) {
      Logger.getLogger(IcalFormat.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException("Could not write calendar", ex);
    } finally {
      try {
        writer.close();
      } catch (IOException ex) {
        Logger.getLogger(IcalFormat.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return strwr.toString();
  }




  private static void addReadings(VEvent event, Readings r) {
    String descValue = r.getTheme() + "\r\n" +
      "GT: " + r.getOt().getSweRef() + "\r\n" +
      "Ep: " + r.getEp().getSweRef()+ "\r\n" +
      "Ev: " + r.getGo().getSweRef() + "\r\n" +
      "Ps: " + r.getPs().getSweRef() + "\r\n";
    event.setDescription(new Description(descValue));
  }

}
