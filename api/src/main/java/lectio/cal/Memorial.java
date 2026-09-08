/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

/**
 * En minnesdag knuten till ett datum i kyrkoåret.
 *
 * @param name        personens eller händelsens namn
 * @param yearOfDeath dödsår, som text eftersom äldre årtal är osäkra
 * @param description kort beskrivning
 * @author marvi
 */
public record Memorial(String name, String yearOfDeath, String description) {
}
