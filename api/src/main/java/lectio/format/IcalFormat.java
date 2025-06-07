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
// Standard Java Calendar is still used for date manipulation before converting to ical4j Date
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
    RandomUidGenerator ug = new RandomUidGenerator(); // Re-instantiate RandomUidGenerator
    SortedMap<LocalDate, Day> daysOfYear = lyf.getDaysOfLiturgicalYear(year).getDaysOfYear();
    Calendar calendar = new Calendar(); // Changed back to net.fortuna.ical4j.model.Calendar
    calendar.getProperties().add(new ProdId("-//marvi.io//lektionarium//EN"));
    calendar.getProperties().add(Version.VERSION_2_0);
    calendar.getProperties().add(CalScale.GREGORIAN);
    for (Entry<LocalDate, Day> entry : daysOfYear.entrySet()) {
      LocalDate localDate = entry.getKey(); // Renamed to avoid conflict with ical4j.model.Date
      Day d = entry.getValue();

      // Convert LocalDate to java.util.Calendar for ical4j.model.Date constructor
      java.util.Calendar javaCal = java.util.Calendar.getInstance();
      javaCal.clear();
      javaCal.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 9, 0, 0);

      // Create ical4j VEvent
      VEvent event = new VEvent(new Date(javaCal.getTime()), d.name());
      event.getProperties().add(ug.generateUid());

      if (d.isHolyDay()) {
        // addReadings expects a non-null Readings object.
        // d.isHolyDay() ensures d.readings() is not null.
        addReadings(event, d.readings());
      }
      calendar.getComponents().add(event);
    }

    StringWriter strwr = new StringWriter();
    CalendarOutputter outputter = new CalendarOutputter(false); // Use CalendarOutputter
    try {
      outputter.output(calendar, strwr); // Use CalendarOutputter
    } catch (IOException ex) {
      Logger.getLogger(IcalFormat.class.getName()).log(Level.SEVERE, null, ex);
      throw new RuntimeException("Could not write calendar", ex);
    }

    return strwr.toString();
  }




  private static void addReadings(VEvent event, Readings r) {
    String descText = r.getTheme() + "\r\n" +
      "GT: " + r.getOt().getSweRef() + "\r\n" +
      "Ep: " + r.getEp().getSweRef()+ "\r\n" +
      "Ev: " + r.getGo().getSweRef() + "\r\n" +
      "Ps: " + r.getPs().getSweRef() + "\r\n";
    event.getProperties().add(new Description(descText)); // Revert to adding Description property
  }

}
