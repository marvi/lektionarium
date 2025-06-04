# lektionarium


![foo](frontend/public/logo.svg)

[lektionarium.se](https://lektionarium.se)

Skapa en kyrkoårskalender för Svenska kyrkans kyrkoår. 

Projektet består av flera delar:

| Modul | Beskrivning |
| :--- | :---  |
| api  |Grundläggande kod för att räkna ut kalendern och skapa olika utformat  |
| cli  | Ett enkelt kommandoradsprogram för att skapa kalendrar i ditt skal  |
| web  | Spring Boot-applikation för att skapa HTTP-kontaktpunkter för att hämta kalenderdata  |
| frontend  | En React-application för att presentera kalenderdata i en webbläsare.  |


Större delen av programmet är skrivet i Java. Webbapplikationen är skriven i Groovy och frontend i Javascript. 

Ingen databas används utan kalenderdata läses från en XML-fil vid anrop. Då kalenderdata inte ändras cachas data ganska aggressivt. 

## Bygga Lektionarium

Detta projekt använder [Apache Maven](https://maven.apache.org/) som byggsystem.

* Installera en [JDK](https://adoptopenjdk.net/) (Java 8 eller senare rekommenderas).
* Installera [Apache Maven](https://maven.apache.org/install.html) om du inte redan har det.
* Gå till root-katalogen i projektet.
* Kör `mvn clean install` för att bygga alla moduler och köra tester.
    * För att bygga utan att köra tester: `mvn clean install -DskipTests`
    * För att bara paketera (t.ex. skapa JAR-filer): `mvn clean package`

Om du använder en IDE (som IntelliJ IDEA, Eclipse, VS Code), importera projektet som ett Maven-projekt.

## Starta webbapplikationen vid utveckling

För att utveckla webbapplikationen lokalt:

* Installera en [JDK](https://adoptopenjdk.net/).
* Installera [NodeJS](https://nodejs.org/en/) (för frontend-utveckling).
* Installera [Apache Maven](https://maven.apache.org/install.html).

Det finns två huvudsakliga delar att köra: backend (Spring Boot) och frontend (React).

**1. Starta Backend (Spring Boot-applikationen):**
* I ett terminalfönster, från projektets root-katalog, kör:
  `mvn spring-boot:run -pl web`
  Detta startar webbservern på port 8080 (eller annan konfigurerad port).

**2. Starta Frontend (React utvecklingsserver):**
* I ett annat terminalfönster, navigera till `frontend`-katalogen:
  `cd frontend`
* Installera frontend-beroenden (behövs bara första gången eller efter ändringar i `package.json`):
  `npm install`
* Starta React utvecklingsservern:
  `npm start`
  Detta öppnar vanligtvis ett webbläsarfönster med frontend-applikationen (ofta på port 3000) och den kommer att anropa backend på port 8080.

**Notera:** För en produktionsbyggnad kommer `mvn clean install` att bygga frontend-resurserna och inkludera dem i Spring Boot JAR-filen, så du behöver inte köra `npm start` separat i produktion.

## API
Om du vill anropa lektionarium från en annan webbplats, t.ex. för att visa söndagens texter på er webb,
kan du anropa API:et från Javascript.

`curl https://lektionarium.se/day` 

Svaret kommer i JSON-format enligt följande:

```json
{
  "day": "Fjärde söndagen efter trefaldighet",
  "date": "2020-07-05",
  "memorials": [],
  "readings": {
    "theme": "Att inte döma",
    "ot": {
      "sweRef": "Hes 18:30-32",
      "enRef": "Ezek. 18:30-32",
      "text": "bibeltext"
    },
    "ep": {
      "sweRef": "Gal 6:1-7",
      "enRef": "Gal. 6:1-7",
      "text": "bibeltext"
    },
    "go": {
      "sweRef": "Luk 6:36-42",
      "enRef": "Luke 6:36-42",
      "text": "bibeltext"
    },
    "ps": {
      "sweRef": "Ps 62:2-9",
      "enRef": "Psa. 62:2-9",
      "text": "bibeltext"
    },
    "alt": null
  }
}
```


### Villkor för användning av källkoden

> Copyright (c) 2010, 2020, marvi ab. All rights reserved.
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
> along with Lektionarium.  If not, see <https://www.gnu.org/licenses/>.
