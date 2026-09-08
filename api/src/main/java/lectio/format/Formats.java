/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.format;

import lectio.cal.LiturgicalYearFactory;

/**
 * Delad kalender för formatklassernas bekvämlighetsmetoder, så att upprepade
 * anrop återanvänder redan uträknade kyrkoår.
 */
final class Formats {

  static final LiturgicalYearFactory SHARED = new LiturgicalYearFactory();

  private Formats() {
  }
}
