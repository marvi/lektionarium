/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
/**
 * Kyrkoåret i Svenska kyrkans tradition: vilka dagar som finns, när de
 * infaller och vilka texter som läses.
 * <p>
 * Ingången är {@link lectio.cal.LiturgicalYearFactory}, som räknar ut och
 * återanvänder {@link lectio.cal.LiturgicalYear}. Varje dag är en
 * {@link lectio.cal.Day}: en {@link lectio.cal.HolyDay} med
 * {@link lectio.cal.Readings} ur evangelieboken, eller en
 * {@link lectio.cal.OrdinaryDay} utan.
 * <p>
 * Evangelieboken läses in av {@link lectio.cal.LectioRepository}. Den
 * medföljande varianten har bara bibelhänvisningar; den som har en fil med
 * bibeltext kan läsa in den i stället.
 */
package lectio.cal;
