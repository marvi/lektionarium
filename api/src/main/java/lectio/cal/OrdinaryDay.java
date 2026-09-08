/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * En dag i kyrkoåret utan egna texter i evangelieboken.
 *
 * @author marvi
 */
public record OrdinaryDay(String name, LocalDate date, LiturgicalColor color,
                          List<Memorial> memorials) implements Day {

  public OrdinaryDay {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(color, "color");
    memorials = List.copyOf(memorials);
  }

  public OrdinaryDay(String name, LocalDate date, LiturgicalColor color) {
    this(name, date, color, List.of());
  }

  /** En dag utan läsningar har ingen text att stryka. */
  @Override
  public Day withoutText() {
    return this;
  }
}
