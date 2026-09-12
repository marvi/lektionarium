/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

/**
 * Liturgisk färg, den färg som används i kyrkorummet en viss dag.
 * <p>
 * Kyrkoåret i det här biblioteket använder vit, röd, violett och grön.
 * Övriga färger finns med för att kunna beskriva dagar från andra
 * kalendrar eller lokala bruk.
 *
 * @author marvi
 */
public enum LiturgicalColor {
  /** Fest och glädje: jul, påsk, kyrkoårets stora högtider. */
  WHITE,
  /** Anden och martyrerna: Apostladagen, Johannes Döparens dag. */
  RED,
  /** Bot och förberedelse: advent och fastan. */
  VIOLET,
  /** Alternativ till violett i advent, används i vissa församlingar. */
  BLUE,
  /** Sorg, traditionellt långfredagen. */
  BLACK,
  /** Växande och vardag: tiden efter trettondedagen och efter trefaldighet. */
  GREEN,
  /** Lättnad mitt i fastan, används i vissa traditioner på Midfastosöndagen. */
  PINK,
  /** Färgen är inte angiven. */
  UNSPECIFIED
}
