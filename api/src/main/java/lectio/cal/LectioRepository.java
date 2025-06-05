/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marvi
 */
public class LectioRepository {

  @NotNull
  public static ReadingCycles getLectio() {
    Document d = getDocument("/lectio/svk_lektionarium_sans_text.xml");
    Element root = d.getDocumentElement();
    NodeList days = root.getChildNodes();
    ReadingCycles readingCycles = new ReadingCycles();

    for (int i = 0; i < days.getLength(); i++) {
      Node dayNode = days.item(i); // Renamed to avoid conflict with pattern variable
      if (dayNode instanceof Element day) { // Pattern matching for instanceof
        // Element dayElem = (Element) day; // Cast removed
        // Extract data from Node
        String name = day.getAttribute("name");
        String theme = day.getAttribute("theme"); // Use pattern variable 'day'
        Readings c1 = null;
        Readings c2 = null;
        Readings c3 = null;
        Readings c4 = null;
        NodeList cycles = day.getChildNodes(); // Use pattern variable 'day'
        for (int j = 0; j < cycles.getLength(); j++) {
          Node cycleNode = cycles.item(j); // Renamed to avoid conflict
          if (cycleNode instanceof Element cycle) { // Pattern matching for instanceof
            // Element cycleElem = (Element) cycle; // Cast removed
            // Extract data from Node
            String cycleNum = cycle.getAttribute("cycle"); // Use pattern variable 'cycle'
            NodeList readings = cycle.getChildNodes(); // Use pattern variable 'cycle'
            Reading ot = null;
            Reading ep = null;
            Reading go = null;
            Reading ps = null;
            for (int k = 0; k < readings.getLength(); k++) {
              Node readingNode = readings.item(k); // Renamed to avoid conflict
              if (readingNode instanceof Element reading) { // Pattern matching for instanceof
                // Element readingElem = (Element) reading; // Cast removed
                // Extract data from Node
                String type = reading.getAttribute("type"); // Use pattern variable 'reading'
                String svref = reading.getAttribute("svref"); // Use pattern variable 'reading'
                String enref = reading.getAttribute("enref"); // Use pattern variable 'reading'
                String content = reading.getTextContent(); // Use pattern variable 'reading'

                if (type.equals("ot")) {
                  ot = new Reading(svref, enref, content);
                }
                if (type.equals("ep")) {
                  ep = new Reading(svref, enref, content);
                }
                if (type.equals("go")) {
                  go = new Reading(svref, enref, content);
                }
                if (type.equals("ps")) {
                  ps = new Reading(svref, enref, content);
                }
              }
            }// End loop over readings
            // Convert if statements to switch expression
            switch (cycleNum) {
              case "1":
                readingCycles.addCycle(name, 1, new Readings(theme, ot, ep, go, ps, null));
                break;
              case "2":
                readingCycles.addCycle(name, 2, new Readings(theme, ot, ep, go, ps, null));
                break;
              case "3":
                readingCycles.addCycle(name, 3, new Readings(theme, ot, ep, go, ps, null));
                break;
              case "4":
                readingCycles.addCycle(name, 4, new Readings(theme, ot, ep, go, ps, null));
                break;
              // Optional: default case if needed, though original code had no else
            }
          }
        }// End loop over cycles
      } // End loop over days
    }
    return readingCycles;
  }

  private static Document getDocument(String file) {
    try {
      DocumentBuilderFactory dbf =
        DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse(LectioRepository.class.getResourceAsStream(file));
    } catch (SAXException | ParserConfigurationException | IOException ex) {
      Logger.getLogger(LectioRepository.class.getName()).log(Level.SEVERE,
        "Error reading or parsing Lectio data", ex);
    }
    return null;
  }
}
