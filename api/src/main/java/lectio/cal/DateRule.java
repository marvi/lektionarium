/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

/**
 * Ett recept för när en dag i kyrkoåret infaller, uttryckt så som
 * kyrkoordningen uttrycker det: ett fast datum, ett avstånd från påskdagen
 * eller advent, eller en veckodag inom ett datumintervall.
 * <p>
 * Ett recept ger tomt om dagen inte infaller det året, som "Söndagen efter
 * nyår" de år ingen söndag ryms 2-5 januari.
 */
sealed interface DateRule {

  Optional<LocalDate> resolve(Anchors anchors);

  /** Fast datum. {@code yearOffset} är -1 för dagar i december året innan. */
  record Fixed(int month, int day, int yearOffset) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      return Optional.of(LocalDate.of(anchors.year() + yearOffset, month, day));
    }
  }

  /** Dagar räknat från påskdagen, negativt före. */
  record FromEaster(int days) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      return Optional.of(anchors.easter().plusDays(days));
    }
  }

  /** Dagar räknat från första söndagen i advent som inleder kyrkoåret. */
  record FromFirstAdvent(int days) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      return Optional.of(anchors.firstAdvent().plusDays(days));
    }
  }

  /** Dagar räknat från första söndagen i advent som avslutar kyrkoåret, negativt före. */
  record FromNextAdvent(int days) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      return Optional.of(anchors.nextAdvent().plusDays(days));
    }
  }

  /**
   * Veckodagen inom ett datumintervall, båda ändarna inräknade. Tomt om
   * intervallet inte rymmer veckodagen. {@code yearOffset} är -1 för
   * intervall i slutet av året innan.
   */
  record WeekdayBetween(DayOfWeek weekday, MonthDay from, MonthDay to, int yearOffset) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      int year = anchors.year() + yearOffset;
      LocalDate candidate = from.atYear(year).with(TemporalAdjusters.nextOrSame(weekday));
      return candidate.isAfter(to.atYear(year)) ? Optional.empty() : Optional.of(candidate);
    }
  }

  /** Receptet, men en vecka bakåt om det hamnar på samma datum som ett annat. */
  record BackedOffFrom(DateRule rule, DateRule collidesWith) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      Optional<LocalDate> date = rule.resolve(anchors);
      return date.equals(collidesWith.resolve(anchors)) ? date.map(d -> d.minusDays(7)) : date;
    }
  }

  /** Det tidigaste av två recept. */
  record Earliest(DateRule first, DateRule second) implements DateRule {
    @Override
    public Optional<LocalDate> resolve(Anchors anchors) {
      Optional<LocalDate> a = first.resolve(anchors);
      Optional<LocalDate> b = second.resolve(anchors);
      if (a.isEmpty()) {
        return b;
      }
      if (b.isEmpty()) {
        return a;
      }
      return a.get().isAfter(b.get()) ? b : a;
    }
  }

  static DateRule fixed(int month, int day) {
    return new Fixed(month, day, 0);
  }

  static DateRule fromEaster(int days) {
    return new FromEaster(days);
  }

  static DateRule sundayBetween(MonthDay from, MonthDay to) {
    return new WeekdayBetween(DayOfWeek.SUNDAY, from, to, 0);
  }

  static DateRule saturdayBetween(MonthDay from, MonthDay to) {
    return new WeekdayBetween(DayOfWeek.SATURDAY, from, to, 0);
  }
}
