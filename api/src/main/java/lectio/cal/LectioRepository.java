/*
 * Copyright (c) 2006, 2020, marvi ab. All rights reserved.
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
      Node day = days.item(i);
      if (day instanceof Element) {
        Element dayElem = (Element) day;
        // Extract data from Node
        String name = dayElem.getAttribute("name");
        String theme = dayElem.getAttribute("theme");
        Readings c1 = null;
        Readings c2 = null;
        Readings c3 = null;
        Readings c4 = null;
        NodeList cycles = day.getChildNodes();
        for (int j = 0; j < cycles.getLength(); j++) {
          Node cycle = cycles.item(j);
          if (cycle instanceof Element) {
            Element cycleElem = (Element) cycle;
            // Extract data from Node
            String cycleNum = cycleElem.getAttribute("cycle");
            NodeList readings = cycleElem.getChildNodes();
            Reading ot = null;
            Reading ep = null;
            Reading go = null;
            Reading ps = null;
            for (int k = 0; k < readings.getLength(); k++) {
              Node reading = readings.item(k);
              if (reading instanceof Element) {
                Element readingElem = (Element) reading;
                // Extract data from Node
                String type = readingElem.getAttribute("type");
                String svref = readingElem.getAttribute("svref");
                String enref = readingElem.getAttribute("enref");
                String content = readingElem.getTextContent();

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
            if (cycleNum.equals("1")) {
              readingCycles.addCycle(name, 1, new Readings(theme, ot, ep, go, ps, null));
            }
            if (cycleNum.equals("2")) {
              readingCycles.addCycle(name, 2, new Readings(theme, ot, ep, go, ps, null));
            }
            if (cycleNum.equals("3")) {
              readingCycles.addCycle(name, 3, new Readings(theme, ot, ep, go, ps, null));
            }
            if (cycleNum.equals("4")) {
              readingCycles.addCycle(name, 4, new Readings(theme, ot, ep, go, ps, null));
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
