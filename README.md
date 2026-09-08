# Lektionarium

![Lektionarium](web/src/main/resources/static/img/logo.svg)

[lektionarium.se](https://lektionarium.se)

En evighetskalender för Svenska kyrkans kyrkoår. Räknar ut vilken dag i
kyrkoåret ett datum är, vilka texter ur evangelieboken som hör till den, och
skriver ut kalendern som text, CSV, JSON eller iCalendar.

## Moduler

| Modul | Beskrivning |
| :--- | :--- |
| `api` | Biblioteket. Räknar ut kyrkoåret och skriver ut det i olika format. **Inga beroenden.** |
| `cli` | Kommandoradsprogram som skriver ut en kalender i skalet. |
| `web` | Webbplatsen och HTTP-API:t. Spring Boot med serverrenderade JTE-mallar och htmx. |

Allt är skrivet i Java 21. Ingen databas: evangelieboken läses ur en XML-fil som
följer med i `api`-modulen, och varje uträknat kyrkoår sparas i minnet.

## Använda biblioteket från ett annat projekt

`api`-modulen är avsiktligt **beroendefri**. Den kan dras in i vilket
JVM-projekt som helst utan att föra med sig transitivt bagage eller
licensfrågor.

```xml
<dependency>
  <groupId>io.marvi</groupId>
  <artifactId>lektionarium-api</artifactId>
  <version>2.1</version>
</dependency>
```

```java
LiturgicalYearFactory kalender = new LiturgicalYearFactory();

// Vilken dag i kyrkoåret är det?
Day idag = kalender.getCurrentDay(LocalDate.now());
System.out.println(idag.name());          // "Fjortonde söndagen efter trefaldighet"

// Dagens texter, om dagen har några
idag.findReadings().ifPresent(texter -> {
  System.out.println(texter.theme());     // "Enheten i Kristus"
  System.out.println(texter.go().sweRef()); // "Joh 17:18-23"
});

// Eller mönstermatcha: Day är en förseglad typ
String rad = switch (idag) {
  case HolyDay dag -> dag.name() + " – " + dag.theme();
  case OrdinaryDay dag -> dag.name();
};

// Hela året i ett format
String ics = IcalFormat.forLiturgicalYear(2026);
String csv = CsvFormat.forCalendarYear(2026);
```

`LiturgicalYearFactory` sparar varje uträknat kyrkoår och är trådsäker. Skapa
en instans och återanvänd den.

### Kyrkoår eller kalenderår

De sammanfaller inte: kyrkoåret 2026 börjar första söndagen i advent 2025.
Formatklasserna gissar därför inte, utan har en metod för vardera —
`forLiturgicalYear` och `forCalendarYear`.

## Bygga

Projektet använder [Maven](https://maven.apache.org/) via wrappern i repot, så
det enda som behövs är en [JDK 21](https://adoptium.net/).

```sh
./mvnw verify              # bygger allt och kör testerna
./mvnw install -DskipTests # bara paketera
```

## Kommandoraden

```sh
./mvnw install -DskipTests
java -jar cli/target/lektionarium-cli-*.jar 2026
java -jar cli/target/lektionarium-cli-*.jar 2026 --format ical --output kyrkoaret.ics
java -jar cli/target/lektionarium-cli-*.jar 2026 --kalenderar --format csv
```

`--help` visar alla flaggor.

## Webbplatsen

```sh
./mvnw install -DskipTests    # första gången, bygger api-modulen
./mvnw spring-boot:run -pl web
```

Sidan ligger sedan på <http://localhost:8080>. Den renderas på servern med
[JTE](https://jte.gg/) och använder [htmx](https://htmx.org/) för att bläddra
mellan dagar utan att ladda om sidan. Länkarna fungerar även utan JavaScript.

## HTTP-API

Vill du visa söndagens texter på er egen webbplats kan du anropa API:et direkt.
Svaren har `Access-Control-Allow-Origin: *`.

| Ändpunkt | Ger |
| :--- | :--- |
| `GET /day` | Dagen i kyrkoåret just nu |
| `GET /day/{datum}` | Dagen i kyrkoåret för ett datum |
| `GET /next/{datum}` | Nästa dag i kyrkoåret |
| `GET /previous/{datum}` | Föregående dag i kyrkoåret |
| `GET /ical` | **Kyrkoåret att prenumerera på.** Rullande fönster, alltid aktuellt |
| `GET /json/{år}` | Hela året som JSON |
| `GET /ical/{år}` | Ett enskilt år som iCalendar, att ladda hem |
| `GET /csv/{år}` | Hela året som CSV |
| `GET /txt/{år}` | Hela året som text |

Årsändpunkterna tar `?basis=CALENDAR` om årtalet ska tolkas som kalenderår i
stället för kyrkoår.

```sh
curl https://lektionarium.se/day
```

```json
{
  "day": "Fjortonde söndagen efter trefaldighet",
  "date": "2026-09-06",
  "color": "GREEN",
  "memorials": [],
  "readings": {
    "theme": "Enheten i Kristus",
    "ot": { "sweRef": "Amos 9:11-15", "enRef": "Amos 9:11-15", "text": "" },
    "ep": { "sweRef": "1 Kor 1:10-13", "enRef": "1Cor. 1:10-13", "text": "" },
    "go": { "sweRef": "Joh 17:18-23", "enRef": "Joh. 17:18-23", "text": "" },
    "ps": { "sweRef": "Ps 95:1-7", "enRef": "Psa. 95:1-7", "text": "" },
    "alt": null
  }
}
```

`text` är tom i den publicerade datafilen: bibeltexten ur Bibel 2000 är
upphovsrättsskyddad och följer inte med i repot. Se `tools/README.md`.

## Prenumerera på kalendern

`https://lektionarium.se/ical` är en beständig adress som alltid är aktuell.
Klistra in den i Google Calendar, Apple Kalender eller Outlook som en
prenumeration, så behöver ingen någonsin hämta ett nytt år för hand.

På webbplatsen ligger den under **Kalender** i menyn. Länken där använder
`webcal://`, som kalenderklienterna har registrerat sig för — ett klick blir
en prenumeration i stället för en engångsnedladdning, vilket en vanlig
https-länk till en `.ics`-fil hade blivit.

Adressen innehåller inget årtal. Varje anrop räknar ut ett fönster kring dagens
datum — föregående, innevarande och nästa kyrkoår — så innehållet följer med
tiden av sig självt. Kalenderklienter hämtar om med jämna mellanrum, och
eftersom varje dags `UID` härleds ur datum och namn känner klienten igen
dagarna och uppdaterar dem i stället för att lägga till dubbletter.

Fönstret är avsiktligt framtungt. `REFRESH-INTERVAL` är en rekommendation som
klienterna inte är skyldiga att följa — Google Calendar hämtar när det passar
dem — så flödet sträcker sig ett helt kyrkoår framåt. Även en klient som legat
stilla i månader har därmed kommande söndagar.

Innehållet ändras bara när ett nytt kyrkoår börjar, alltså vid första advent.
Däremellan är svaret identiskt byte för byte, och en klient som skickar
`If-Modified-Since` får `304 Not Modified` utan att något genereras.

De årsvisa adresserna finns kvar för den som hellre laddar hem en fil en gång.

## Bibeltexten

Evangelieboken som följer med i `api`-modulen innehåller bara bibelhänvisningar.
Bibeltexten är upphovsrättsskyddad och ligger därför inte i repot.

Har du rätt att återge texten kan du lägga en egen fil med texten i projektets
rot som `svk_lektionarium.xml`. Finns den läses den, annars används den
medföljande. Sökvägen kan pekas om:

```properties
lektionarium.lectionary-file=/etc/lektionarium/svk_lektionarium.xml
lektionarium.text-days=3
```

Filen läses från filsystemet och **aldrig från classpath**. En fil under
`src/main/resources` hade packats in i jar-filen, följt med i container-avbilden
och publicerats till GitHub Packages. Filnamnet ligger dessutom i `.gitignore`,
och vid start loggas vilken fil som lästes och om den bär text.

### Var texten visas

Rätten att återge gäller begränsade mängder, så texten lämnas bara ut för den
dag vi befinner oss i och de två närmast följande. Den visas inte direkt utan
när man klickar på bibelhänvisningen.

Principen är att stryka som standard. Att lämna ut en dag går via
`BibleTextPolicy.redact`, i stället för att förlita sig på att just den
ändpunkten råkar vara ofarlig:

| | Bibeltext |
| :--- | :--- |
| `/` och `/dag/{datum}` inom fönstret | ja |
| `/day` | ja |
| `/dag/{datum}` utanför fönstret | nej |
| `/day/{datum}`, `/next`, `/previous` | nej, cachas immutable i 30 dagar |
| `/json/{år}`, `/csv`, `/txt`, `/ical` | nej, ett helt år är aldrig en begränsad mängd |

De datumstyrda ändpunkterna kan anropas för vilket datum som helst och vore
annars ett sätt att hämta hem hela evangelieboken. Svar som bär text får
`Cache-Control: private`, så de inte blir kvar i mellanliggande cachar efter
att fönstret flyttat sig.

`JsonFormat.forDays` stryker alltid texten, oavsett anropare. `TextFormat`,
`CsvFormat` och `IcalFormat` skriver aldrig annat än hänvisningar.

## Cachning

Kalenderdata är dyr att räkna ut och ändras nästan aldrig, så det cachas i två
lager.

**I minnet.** Varje uträknat kyrkoår sparas i `LiturgicalYearFactory`. Cachen
har ett tak på 64 år och slänger det minst använda: ett kyrkoår tar omkring
8 kB, och utan tak skulle en anropare som får välja årtal kunna fylla heapen.
Evangelieboken tolkas en gång per process.

**Över HTTP.** Ändpunkterna delas i två slag:

| Slag | Ändpunkter | Policy |
| :--- | :--- | :--- |
| Rena funktioner av adressen | `/day/{datum}`, `/next`, `/previous`, `/ical/{år}`, `/csv/{år}`, `/txt/{år}`, `/json/{år}` | `max-age=30d, immutable` |
| Beror på dagens datum | `/day` | Livslängd fram till nästa dag i kyrkoåret |
| Rullande fönster | `/ical` | `max-age=6h` + `Last-Modified` |

`/day` får ingen gissad siffra utan en livslängd som räknas fram till den
tidpunkt då svaret faktiskt slutar gälla — infaller nästa kyrkodag om fem
dagar blir svaret giltigt i fem dagar.

Formatklasserna ger **samma bytes för samma innehåll**. `DTSTAMP` härleds ur
kalenderinnehållet och inte ur anropstidpunkten, vilket är förutsättningen för
att `Last-Modified` och `304 Not Modified` ska betyda något.

Vilken dag det är avgörs i `lektionarium.zone` (Europe/Stockholm), inte i
JVM:ens standardzon — en container som kör i UTC skulle annars byta dag två
timmar för sent svensk sommartid.

Statiska filer ligger på adresser utan versionsnummer och cachas därför bara
ett dygn, med `must-revalidate`.

## Släppa en version

Testerna körs vid varje push. Publiceringen utlöses av en tagg.

```sh
tools/release.sh --dry-run   # visa vad som skulle hända
tools/release.sh             # gör det
```

Skriptet tar versionen som står i pom-filerna, kör igenom hela bygget, taggar
`vX.Y`, höjer pom-filerna ett steg och skickar upp alltihop. Taggen startar
`release.yml`, som bygger om, publicerar till GitHub Packages och skapar en
GitHub-release med jar-filerna.

Versionerna går i steg om 0.1, med överslag till nästa heltal:

```
2.1 -> 2.2 -> ... -> 2.9 -> 3.0
```

En annan version går att tvinga fram med `--version 3.0`.

Skriptet vägrar köra om arbetskopian är smutsig, om du står på fel gren, om
grenen ligger efter `origin`, eller om versionen inte är högre än den högsta
befintliga taggen. Det sista är värt att ha: projektet släpptes som `2.0.0`
redan 2020, och en release med lägre nummer hade räknats som äldre av Maven.

Arbetsflödet kontrollerar dessutom att taggen och pom-filerna är överens innan
något publiceras, så en artefakt aldrig får ett annat versionsnummer än taggen
utlovar.

## Köra som container

`Dockerfile` bygger webbapplikationen i två steg och kör den som en icke-root
användare. Fungerar med både podman och docker.

```sh
podman build -t lektionarium .
podman run --rm -p 8080:8080 lektionarium
```

Avbilden innehåller **inte** evangelieboken med bibeltext. Filen ligger i
`.dockerignore` och kan därför inte följa med in i avbilden ens av misstag.
Montera in den skrivskyddat vid körning i stället:

```sh
podman run --rm -p 8080:8080 \
  -v /srv/lektionarium/svk_lektionarium.xml:/data/lektionarium.xml:ro \
  -e LEKTIONARIUM_LECTIONARYFILE=/data/lektionarium.xml \
  -e LEKTIONARIUM_BASEURL=https://lektionarium.se \
  lektionarium
```

Utan filen startar den ändå, med enbart bibelhänvisningar. Startloggen säger
alltid vilken fil som lästes och om den bär text.

`LEKTIONARIUM_BASEURL` behöver sättas till den publika adressen, eftersom det
är den prenumerationsflödet uppger att klienter ska hämta om ifrån. Den läses
inte ur `Host`-huvudet.

Tidszonen behöver inte sättas. Applikationen räknar dagar i `lektionarium.zone`
(Europe/Stockholm) oavsett vad containern har för zon.

### Bakom en omvänd proxy

Applikationen lyssnar på 8080 och terminerar inte TLS. Med Caddy framför:

```caddyfile
lektionarium.se {
    reverse_proxy 127.0.0.1:8080
}
```

### Publicerad avbild

Releasen bygger och publicerar avbilden till GitHub Container Registry när
taggen skapas:

```
ghcr.io/marvi/lektionarium:2.1
ghcr.io/marvi/lektionarium:latest
```

Servern behöver då inte bygga något — Ansible hämtar hem den färdiga avbilden.

Avbilden byggs för `linux/amd64`. Vill du ha `arm64` med krävs QEMU i
arbetsflödet, och då körs hela Maven-bygget under emulering.

**Ett nytt paket på ghcr.io är privat.** Ska servern kunna hämta utan
inloggning måste paketet göras publikt en gång, under *Packages → lektionarium
→ Package settings → Change visibility*. Annars behöver podman på servern en
token:

```sh
podman login ghcr.io -u <användare>
```

Bygga lokalt går fortfarande bra, och ger då `localhost/lektionarium:latest`.

### Hälsokontroll

`GET /actuator/health` svarar `{"status":"UP"}` när kalendern kan räkna ut
dagens dag. Kontrollen rör vid evangelieboken, årscachen och datumlogiken på
en gång, så den säger något mer än att processen lever.

Svaret innehåller inga detaljer, så ändpunkten kan nås utan att röja något om
driftsättningen. Vill du se dagens dag och om bibeltexten är inläst, sätt
`management.endpoint.health.show-details=always` — men bara när ändpunkten
stannar på loopback. Inga andra actuator-ändpunkter är exponerade.

Grupperna `/actuator/health/liveness` och `/actuator/health/readiness` finns
också. Avbilden har en `HEALTHCHECK` inbyggd, så podman följer tillståndet
utan att du behöver ange något.

### Som systemd-tjänst

Enheten hör hemma i ditt Ansible-projekt, inte här. Med Quadlet, i
`/etc/containers/systemd/lektionarium.container`:

```ini
[Unit]
Description=Lektionarium
After=network-online.target
Wants=network-online.target

[Container]
Image=ghcr.io/marvi/lektionarium:2.1
ContainerName=lektionarium

# Caddy terminerar TLS och proxar hit. Bind bara till loopback.
PublishPort=127.0.0.1:8080:8080

# Evangelieboken med bibeltext. z behövs om SELinux är enforcing.
Volume=/srv/lektionarium/svk_lektionarium.xml:/data/lektionarium.xml:ro,z

Environment=LEKTIONARIUM_LECTIONARYFILE=/data/lektionarium.xml
Environment=LEKTIONARIUM_BASEURL=https://lektionarium.se

PodmanArgs=--memory=512m

[Service]
Restart=always
TimeoutStartSec=90

[Install]
WantedBy=multi-user.target
```

Quadlet genererar enheten vid `systemctl daemon-reload`; det finns ingen
`.service`-fil att aktivera för hand.

Filen på värden måste vara läsbar för containerns användare, som är UID 10001
och inte root. Med rootless podman mappas den dessutom till ett subuid, så
`chmod 0644` behövs i praktiken.

Kontrollera att texten hittades:

```sh
journalctl -u lektionarium | grep Evangeliebok
```

### Inställningar

| Miljövariabel | Standard | Styr |
| :--- | :--- | :--- |
| `LEKTIONARIUM_LECTIONARYFILE` | `svk_lektionarium.xml` | Sökväg inne i containern, ska matcha monteringen |
| `LEKTIONARIUM_BASEURL` | `https://lektionarium.se` | Adressen prenumerationsflödet uppger |
| `LEKTIONARIUM_TEXTDAYS` | `3` | Hur många dagar framåt bibeltexten visas |
| `LEKTIONARIUM_ZONE` | `Europe/Stockholm` | Tidszonen dagens dag räknas i |
| `SERVER_PORT` | `8080` | Porten inne i containern |
| `JAVA_TOOL_OPTIONS` | `-XX:MaxRAMPercentage=75.0` | Sätt om du ändrar minnesgränsen |

## Släppa en version

Testerna körs vid varje push. Publiceringen utlöses av en tagg.

```sh
tools/release.sh --dry-run   # visa vad som skulle hända
tools/release.sh             # gör det
```

Skriptet tar versionen som står i pom-filerna, kör igenom hela bygget, taggar
`vX.Y`, höjer pom-filerna ett steg och skickar upp alltihop. Taggen startar
`release.yml`, som bygger om, publicerar till GitHub Packages och skapar en
GitHub-release med jar-filerna.

Versionerna går i steg om 0.1, med överslag till nästa heltal:

```
2.1 -> 2.2 -> ... -> 2.9 -> 3.0
```

En annan version går att tvinga fram med `--version 3.0`.

Skriptet vägrar köra om arbetskopian är smutsig, om du står på fel gren, om
grenen ligger efter `origin`, eller om versionen inte är högre än den högsta
befintliga taggen. Det sista är värt att ha: projektet släpptes som `2.0.0`
redan 2020, och en release med lägre nummer hade räknats som äldre av Maven.

Arbetsflödet kontrollerar dessutom att taggen och pom-filerna är överens innan
något publiceras, så en artefakt aldrig får ett annat versionsnummer än taggen
utlovar.

## Köra som container

`Dockerfile` bygger webbapplikationen i två steg och kör den som en icke-root
användare. Fungerar med både podman och docker.

```sh
podman build -t lektionarium .
podman run --rm -p 8080:8080 lektionarium
```

Avbilden innehåller **inte** evangelieboken med bibeltext. Filen ligger i
`.dockerignore` och kan därför inte följa med in i avbilden ens av misstag.
Montera in den skrivskyddat vid körning i stället:

```sh
podman run --rm -p 8080:8080 \
  -v /srv/lektionarium/svk_lektionarium.xml:/data/lektionarium.xml:ro \
  -e LEKTIONARIUM_LECTIONARYFILE=/data/lektionarium.xml \
  -e LEKTIONARIUM_BASEURL=https://lektionarium.se \
  lektionarium
```

Utan filen startar den ändå, med enbart bibelhänvisningar. Startloggen säger
alltid vilken fil som lästes och om den bär text.

`LEKTIONARIUM_BASEURL` behöver sättas till den publika adressen, eftersom det
är den prenumerationsflödet uppger att klienter ska hämta om ifrån. Den läses
inte ur `Host`-huvudet.

Tidszonen behöver inte sättas. Applikationen räknar dagar i `lektionarium.zone`
(Europe/Stockholm) oavsett vad containern har för zon.

### Bakom en omvänd proxy

Applikationen lyssnar på 8080 och terminerar inte TLS. Med Caddy framför:

```caddyfile
lektionarium.se {
    reverse_proxy 127.0.0.1:8080
}
```

### Som systemd-tjänst

Enheten hör hemma i ditt Ansible-projekt, inte här, men i korthet:

```ini
[Unit]
Description=Lektionarium
After=network-online.target
Wants=network-online.target

[Service]
Restart=always
ExecStartPre=-/usr/bin/podman rm -f lektionarium
ExecStart=/usr/bin/podman run --rm --name lektionarium \
  --publish 127.0.0.1:8080:8080 \
  --memory 512m \
  --volume /srv/lektionarium/svk_lektionarium.xml:/data/lektionarium.xml:ro \
  --env LEKTIONARIUM_LECTIONARYFILE=/data/lektionarium.xml \
  --env LEKTIONARIUM_BASEURL=https://lektionarium.se \
  localhost/lektionarium:latest
ExecStop=/usr/bin/podman stop lektionarium

[Install]
WantedBy=multi-user.target
```

Kör du podman finns också `podman generate systemd` och Quadlet, som gör
enheten åt dig.

## Omfattning

Evangelieboken som följer med gäller från 2003 års kyrkohandbok, så
uträkningen stöder kyrkoår från och med **2004**. Påskdagen räknas ut med
Meeus/Jones/Butchers algoritm och stämmer långt utanför det intervallet.

## Licens

> Copyright (c) 2010, 2026, marvi ab. All rights reserved.
>
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
