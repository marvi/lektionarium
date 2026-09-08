/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

/**
 * En enskild bibelläsning.
 *
 * @param sweRef svensk bibelhänvisning, t.ex. "Sak 9:9-10"
 * @param enRef  engelsk bibelhänvisning, t.ex. "Zech. 9:9-10"
 * @param text   själva bibeltexten, tom när den utelämnats av upphovsrättsskäl
 * @author marvi
 */
public record Reading(String sweRef, String enRef, String text) {

  public Reading(String sweRef, String enRef) {
    this(sweRef, enRef, "");
  }
}
