/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

/**
 * Texterna för en dag i kyrkoåret, en läsningsserie.
 *
 * @param theme dagens tema
 * @param ot    gammaltestamentlig text
 * @param ep    epistel
 * @param go    evangelium
 * @param ps    psaltarpsalm
 * @param alt   alternativ text, ofta null
 * @author marvi
 */
public record Readings(String theme, Reading ot, Reading ep, Reading go, Reading ps, Reading alt) {

  /** @return true om någon av läsningarna bär bibeltext */
  public boolean hasText() {
    return has(ot) || has(ep) || has(go) || has(ps) || has(alt);
  }

  /**
   * @return samma texter med enbart bibelhänvisningar kvar
   */
  public Readings withoutText() {
    return hasText()
      ? new Readings(theme, strip(ot), strip(ep), strip(go), strip(ps), strip(alt))
      : this;
  }

  private static boolean has(Reading reading) {
    return reading != null && reading.hasText();
  }

  private static Reading strip(Reading reading) {
    return reading == null ? null : reading.withoutText();
  }
}
