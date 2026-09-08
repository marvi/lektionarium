/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import lectio.cal.ReadingCycles.Cycle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Läser evangelieboken från den medföljande XML-filen.
 * <p>
 * Filen ändras aldrig i drift, så den tolkas en enda gång och delas sedan av
 * alla anropare. {@link ReadingCycles} är oföränderlig efter inläsning och kan
 * därför användas från flera trådar.
 *
 * @author marvi
 */
public final class LectioRepository {

  private static final String LECTIONARY = "/lectio/svk_lektionarium_sans_text.xml";

  private static final Pattern PARAGRAPH_BREAK = Pattern.compile("\\n\\s*\\n");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  private LectioRepository() {
  }

  /**
   * Initialiseras vid första anropet till {@link #getLectio()} och aldrig mer.
   * Klassladdaren garanterar att det sker exakt en gång.
   */
  private static final class Holder {
    static final ReadingCycles INSTANCE = parse(LECTIONARY);
  }

  /** @return den medföljande evangelieboken, utan bibeltext, tolkad en gång */
  public static ReadingCycles getLectio() {
    return Holder.INSTANCE;
  }

  /**
   * Läser en evangeliebok från en fil i stället för den medföljande.
   * <p>
   * Avsett för en driftsättning som har rätt att visa bibeltext. Filen läses
   * medvetet från filsystemet och inte från classpath: en fil som ligger
   * utanför projektet kan inte råka packas in i en jar-fil eller en
   * container-avbild och därmed spridas vidare.
   *
   * @param file sökväg till en evangeliebok i samma XML-format
   * @return evangelieboken
   * @throws IllegalStateException om filen saknas eller inte går att tolka
   */
  public static ReadingCycles load(Path file) {
    if (!Files.isReadable(file)) {
      throw new IllegalStateException("Kan inte läsa evangelieboken: " + file);
    }
    try (InputStream in = Files.newInputStream(file)) {
      return parse(readDocument(in, file.toString()));
    } catch (IOException ex) {
      throw new UncheckedIOException("Kunde inte läsa " + file, ex);
    }
  }

  private static ReadingCycles parse(String resource) {
    InputStream in = LectioRepository.class.getResourceAsStream(resource);
    if (in == null) {
      throw new IllegalStateException("Hittar inte evangelieboken på classpath: " + resource);
    }
    try (in) {
      return parse(readDocument(in, resource));
    } catch (IOException ex) {
      throw new UncheckedIOException("Kunde inte läsa " + resource, ex);
    }
  }

  private static ReadingCycles parse(Document document) {
    ReadingCycles cycles = new ReadingCycles();

    for (Element dayElement : childElements(document.getDocumentElement())) {
      String name = dayElement.getAttribute("name");
      String theme = dayElement.getAttribute("theme");

      for (Element cycleElement : childElements(dayElement)) {
        Map<String, Reading> byType = new HashMap<>();
        for (Element readingElement : childElements(cycleElement)) {
          byType.put(readingElement.getAttribute("type"), new Reading(
            readingElement.getAttribute("svref"),
            readingElement.getAttribute("enref"),
            normalizeText(readingElement.getTextContent())));
        }
        Readings readings = new Readings(theme, byType.get("ot"), byType.get("ep"),
          byType.get("go"), byType.get("ps"), byType.get("alt"));
        cycles.add(name, cycleOf(cycleElement, name), readings);
      }
    }
    return cycles;
  }

  /**
   * Plockar bort XML-filens egen formatering ur bibeltexten.
   * <p>
   * Filen är radbruten och indragen för att vara läsbar som fil. Det är
   * formatering av dokumentet och inte av texten, så radbrytningar och indrag
   * ska inte följa med ut till den som läser texten. Tomrader behålls som
   * styckebrytning, men förekommer inte i den nuvarande filen.
   *
   * @param text texten så som den står i XML-filen
   * @return texten som löpande stycken, utan indrag
   */
  private static String normalizeText(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    return PARAGRAPH_BREAK.splitAsStream(text.strip())
      .map(paragraph -> WHITESPACE.matcher(paragraph).replaceAll(" ").strip())
      .filter(paragraph -> !paragraph.isEmpty())
      .collect(Collectors.joining("\n\n"));
  }

  private static Cycle cycleOf(Element cycleElement, String dayName) {
    String raw = cycleElement.getAttribute("cycle");
    try {
      return Cycle.of(Integer.parseInt(raw));
    } catch (IllegalArgumentException ex) {
      throw new IllegalStateException(
        "Ogiltig läsningsserie '" + raw + "' för " + dayName + " i " + LECTIONARY, ex);
    }
  }

  private static Document readDocument(InputStream in, String source) {
    try {
      return secureDocumentBuilderFactory().newDocumentBuilder().parse(in);
    } catch (IOException ex) {
      throw new UncheckedIOException("Kunde inte läsa " + source, ex);
    } catch (ParserConfigurationException | SAXException ex) {
      throw new IllegalStateException("Kunde inte tolka " + source, ex);
    }
  }

  /** Filen är vår egen och saknar DTD. Stäng av allt externt för säkerhets skull. */
  private static DocumentBuilderFactory secureDocumentBuilderFactory() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    factory.setXIncludeAware(false);
    factory.setExpandEntityReferences(false);
    return factory;
  }

  private static List<Element> childElements(Node parent) {
    NodeList children = parent.getChildNodes();
    List<Element> elements = new ArrayList<>();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i) instanceof Element element) {
        elements.add(element);
      }
    }
    return elements;
  }
}
