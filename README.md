# lektionarium

Skapa en kyrkoårskalender för Svenska kyrkans kyrkoår. 

Projektet består av flera delar:

1. api - Grundläggande kod för att räkna ut kalendern och skapa olika utformat
2. cli - Ett enkelt kommandoradsprogram för att skapa kalendrar i ditt skal
3. web - Spring Boot-applikation för att skapa HTTP-kontaktpunkter för att hämta kalenderdata
4. frontend - En React-application för att presentera kalenderdata i en webbkläsare. 

Större delen av programmet är skrivet i Java. Webbapplikationen är skriven i Groovy och frontend i Javascript. 

Ingen databas används utan kalenderdata läses från en XML-fil vid anrop. Då kalenderdata inte ändras cachas data ganska aggressivt. 

# Bygga Lektionarium
* Installera en JDK https://adoptopenjdk.net/
* Gå till root-katalogen i projektet
* Kör `./gradlew build` (gradlew.bat på windows)


> Lektionarium is free software: you can redistribute it and/or modify
> it under the terms of the GNU General Public License as published by
> the Free Software Foundation, either version 3 of the License, or
> (at your option) any later version.
> 
> Lektionarium is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.
> 
> You should have received a copy of the GNU General Public License
> along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
