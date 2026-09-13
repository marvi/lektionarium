# Kyrkoårets matematik

Så här räknas Svenska kyrkans kyrkoår fram, från ett enda årtal till ett
sextiotal namngivna dagar med datum. Beskrivningen följer kyrkoordningen
och evangelieboken från 2003, som gäller från och med kyrkoåret 2004.

Citaten kommer från *Kyrkoordning för Svenska kyrkan*, lydelse 1 januari
2025. Kyrkoåret regleras i 28 kap., och alla dagarna med sina regler står
i en enda paragraf, 28 kap. 1 §. Paragrafen är en tabell med dagens namn i
vänsterspalten och regeln i högerspalten; citaten nedan återger regeln.
Evangelieboken tillför två dagar som inte står i kyrkoordningen, Julnatten
och Påsknatten, samt texterna och färgerna.

## Vad ett kyrkoår är

> Kyrkan har sitt eget år, kyrkoåret, och sin egen kalender, som utgör en
> grund för och huvudsakligen sammanfaller med den borgerliga kalendern.
> I kyrkoåret, som börjar med första söndagen i advent och slutar med
> veckan efter domssöndagen, framställs Guds frälsningshandlingar med
> människan.
>
> — 28 kap., inledningen

Kyrkoåret börjar alltså första söndagen i advent och slutar lördagen före
nästa första söndag i advent. Det sammanfaller inte med kalenderåret:
ungefär fem veckor av kyrkoåret ligger i december året innan.

Ett kyrkoår benämns efter det kalenderår där dess påskdag infaller.
Kyrkoåret 2027 börjar den 29 november 2026 och slutar den 27 november 2027.

## Tre ankare

Allt annat räknas från tre datum.

**Påskdagen** definieras i kyrkoordningen med en enda mening:

> söndagen närmast efter den fullmåne som infaller på eller närmast efter
> den 21 mars
>
> — 28 kap. 1 §, påskdagen

Fullmånen är kyrkans egen tabellmåne, inte den astronomiska, och den
21 mars är kyrkans fasta vårdagjämning. Den gregorianska formeln (Meeus,
Jones och Butcher) tar årtalet *Y* och ger månad och dag:

| Steg | Beräkning |
|---|---|
| a | Y mod 19 |
| b, c | Y div 100, Y mod 100 |
| d, e | b div 4, b mod 4 |
| f | (b + 8) div 25 |
| g | (b − f + 1) div 3 |
| h | (19a + b − d − g + 15) mod 30 |
| i, k | c div 4, c mod 4 |
| l | (32 + 2e + 2i − h − k) mod 7 |
| m | (a + 11h + 22l) div 451 |
| månad | (h + l − 7m + 114) div 31 |
| dag | (h + l − 7m + 114) mod 31 + 1 |

Här är *a* årets plats i den nittonåriga måncykeln, *h* antalet dagar
från den 21 mars till tabellfullmånen, och *l* antalet dagar därifrån till
nästa söndag. Påskdagen infaller tidigast den 22 mars och senast den
25 april.

**Första söndagen i advent** anger kyrkoordningen bakifrån:

> första–fjärde söndagen i advent: de fyra söndagarna före juldagen
>
> — 28 kap. 1 §

Fjärde advent är den sista söndagen före den 25 december, alltså
18–24 december, och första advent tre veckor tidigare: söndagen
27 november–3 december. Kyrkoåret behöver den två gånger: advent året
innan, som inleder året, och advent det egna kalenderåret, som avslutar
det.

## Fyra sätt att ange en dag

Varje dag i kyrkoåret har ett recept av ett av fyra slag.

### Fast datum

| Dag | Datum |
|---|---|
| Julnatten | 24 december |
| Juldagen | 25 december |
| Annandag jul | 26 december |
| Nyårsdagen | 1 januari |
| Trettondedag jul | 6 januari |

Decemberdagarna hör till kyrkoåret som börjar i advent innan, alltså till
det kyrkoår som är numrerat med nästa kalenderår.

Kyrkoordningen anger dem som datum rakt av: "juldagen: den 25 december",
"nyårsdagen: den 1 januari", "trettondedag jul: den 6 januari". Julnatten
står inte i kyrkoordningen utan i evangelieboken.

### Avstånd från påskdagen

Hela fastan, stilla veckan, påsktiden och pingst ligger ett fast antal dagar
från påskdagen. Alla söndagarna ligger på multipler av sju.

Kyrkoordningen uttrycker det i led: Fastlagssöndagen är "sjunde söndagen
före påskdagen", Askonsdagen "onsdagen efter fastlagssöndagen", Första till
tredje söndagen i fastan "de tre söndagarna efter fastlagssöndagen",
Midfastosöndagen "den fjärde söndagen efter fastlagssöndagen" och Femte
söndagen i fastan "söndagen efter midfastosöndagen". Septuagesima är
"andra söndagen före fastlagssöndagen" och Sexagesima "söndagen före
fastlagssöndagen". Räknat om till dagar från påsk blir det tabellen nedan.

| Dagar före påsk | Dag |
|---|---|
| 63 | Septuagesima |
| 56 | Sexagesima |
| 49 | Fastlagssöndagen |
| 46 | Askonsdagen (onsdag) |
| 42 | Första söndagen i fastan |
| 35 | Andra söndagen i fastan |
| 28 | Tredje söndagen i fastan |
| 21 | Midfastosöndagen |
| 14 | Femte söndagen i fastan |
| 7 | Palmsöndagen |
| 3 | Skärtorsdagen |
| 2 | Långfredagen |
| 1 | Påsknatten |

| Dagar efter påsk | Dag |
|---|---|
| 0 | Påskdagen |
| 1 | Annandag påsk |
| 7, 14, 21, 28 | Andra till femte söndagen i påsktiden |
| 35 | Bönsöndagen |
| 39 | Kristi himmelsfärds dag (torsdag) |
| 42 | Söndagen före pingst |
| 49 | Pingstdagen |
| 50 | Annandag pingst |
| 56 | Heliga trefaldighets dag |

Efter påsk räknar kyrkoordningen på samma sätt: "andra–femte söndagen i
påsktiden: de fyra första söndagarna efter påskdagen", "bönsöndagen: den
femte söndagen efter påskdagen", "Kristi himmelsfärds dag: sjätte
torsdagen efter påskdagen", "söndagen före pingst: den sjätte söndagen
efter påskdagen", "pingstdagen: sjunde söndagen efter påskdagen" och
"heliga trefaldighets dag: söndagen efter pingstdagen". Alla står i
28 kap. 1 §.

### Avstånd från advent

| Dag | Läge |
|---|---|
| Första till fjärde söndagen i advent | 0, 7, 14 och 21 dagar efter första advent |
| Söndagen före domssöndagen | 14 dagar före nästa första advent |
| Domssöndagen | 7 dagar före nästa första advent |

> domssöndagen: söndagen före första söndagen i advent
>
> — 28 kap. 1 §

Söndagen före domssöndagen har ingen egen regel i kyrkoordningen, namnet
är regeln.

### Veckodag i ett datumintervall

Kyrkoordningen använder samma formulering för alla dessa dagar:

> kyndelsmässodagen eller jungfru Marie kyrkogångsdag: den söndag som
> infaller under tiden den 2–8 februari
>
> midsommardagen: den lördag som infaller under tiden den 20–26 juni
>
> alla helgons dag: den lördag som infaller under tiden den 31 oktober –
> den 6 november
>
> — 28 kap. 1 §

Sju dagar rymmer alltid exakt en av varje veckodag, så "den söndag som
infaller under tiden" pekar ut ett enda datum. Är intervallet kortare än
sju dagar finns dagen bara vissa år.

| Dag | Veckodag | Intervall |
|---|---|---|
| Söndagen efter jul | söndag | 27–31 december |
| Söndagen efter nyår | söndag | 2–5 januari |
| Kyndelsmässodagen | söndag | 2–8 februari |
| Jungfru Marie bebådelsedag | söndag | 22–28 mars |
| Midsommardagen | lördag | 20–26 juni |
| Den helige Johannes Döparens dag | söndag | 21–27 juni |
| Den helige Mikaels dag | söndag | 29 september–5 oktober |
| Tacksägelsedagen | söndag | 8–14 oktober |
| Alla helgons dag | lördag | 31 oktober–6 november |
| Söndagen efter alla helgons dag | söndag | 1–7 november |
| Första söndagen i advent | söndag | 27 november–3 december |

Två av dagarna anges inte med intervall utan i förhållande till en annan
dag, men det ger samma sak: "den helige Johannes döparens dag: den söndag
som infaller efter midsommardagen" är söndagen 21–27 juni, och
"tacksägelsedagen: andra söndagen i oktober" är söndagen 8–14 oktober.

De två korta intervallen ger dagar som inte finns varje år. Kyrkoordningen
skriver "söndagen efter jul: den söndag som infaller 27–31 december" och
"söndagen efter nyår: den söndag som infaller under tiden den 2–5 januari".
Söndagen efter jul saknas när juldagen eller annandag jul är en söndag, två
år av sju. Söndagen efter nyår saknas när nyårsdagen är en söndag, måndag
eller tisdag, tre år av sju.

## De numrerade söndagarna

Två perioder fylls med söndagar som bara har ett nummer.

**Söndagarna efter trettondedagen** anges som "första–sjätte söndagen efter
trettondedagen: de sex söndagarna efter trettondedag jul". De börjar med
söndagen 7–13 januari och fortsätter varje vecka fram till men inte med
Septuagesima. Antalet platser beror på påsken: en enda
vid tidigast möjliga påsk, sex vid den senaste. Eftersom Kyndelsmässodagen
tar en av platserna de flesta år blir det en till fem namngivna söndagar.

**Söndagarna efter trefaldighet** anger kyrkoordningen i fyra led:

> första–fjärde söndagen efter trefaldighet: de fyra första söndagarna
> efter heliga trefaldighets dag
>
> apostladagen: den femte söndagen efter heliga trefaldighets dag
>
> Kristi förklarings dag: den sjunde söndagen efter heliga trefaldighets
> dag
>
> åttonde–tjugofemte söndagen efter trefaldighet: de söndagar som infaller
> mellan Kristi förklarings dag och söndagen före domssöndagen
>
> — 28 kap. 1 §

Serien börjar alltså 63 dagar efter påsk, veckan efter Heliga
trefaldighets dag, och fortsätter fram till men inte med Söndagen före
domssöndagen. Två av platserna har egna namn: den femte är Apostladagen
och den sjunde Kristi förklarings dag. Antalet platser är 20 till 25
beroende på påsken, och 16 till 21 av dem får behålla sitt namn sedan
höstens helgdagar tagit sina.

Numret sätts efter platsen i följden, inte efter hur många söndagar som
faktiskt fått namnet. Trängs den fjärde söndagen undan av en helgdag heter
nästa ändå den femte. Därför finns det år utan Fjärde söndagen efter
trettondedagen, och år utan Apostladagen.

## När två dagar hamnar på samma datum

Kyrkoåret har fler recept än det finns datum för. Krockarna löses på två
sätt: nästan alltid genom att en dag viker, två gånger genom att en dag
flyttar.

### Rang

Kyrkoordningen löser krockarna med en markering i tabellen och en mening
under den:

> Med ** markerade söndagar utgår om en annan helgdag infaller samtidigt.
>
> — 28 kap. 1 §

Markerade är söndagarna efter trettondedagen, Septuagesima, Sexagesima,
fastans fem söndagar, första till fjärde söndagen efter trefaldighet,
Apostladagen och åttonde till tjugofemte söndagen efter trefaldighet.
Omarkerade är advent, Fastlagssöndagen, Palmsöndagen, påsktidens söndagar,
Söndagen efter jul och efter nyår, sjätte söndagen efter trefaldighet,
Kristi förklarings dag och domssöndagarna. De omarkerade kan aldrig hamna
på samma datum som en helgdag, vilket följer av intervallen, så
markeringen är fullständig.

I beräkningen uttrycks samma sak som en rang. Hamnar två dagar på samma
datum vinner den med högre rang, och den andra utgår det året.

| Rang | Dagar |
|---|---|
| Helgdag | Kyndelsmässodagen, bebådelsedagen, Johannes Döparens dag, Mikaelidagen, Tacksägelsedagen, Söndagen efter alla helgons dag, de fasta dagarna utom Julnatten, samt vardagarna med egna texter |
| Söndag | Advent, fastan, påsktiden, Söndagen efter jul och efter nyår, domssöndagarna |
| Natt | Julnatten, Påsknatten |
| Numrerad söndag | Söndagarna efter trettondedagen och efter trefaldighet, Apostladagen och Kristi förklarings dag inräknade |

Det ger följande utfall, som alla inträffar med några års mellanrum:

- Kyndelsmässodagen tränger undan Septuagesima när påsken infaller
  6–12 april, och Sexagesima när påsken infaller 23 mars–5 april. I det
  senare fallet är det ibland den flyttade Kyndelsmässodagen som gör det.
- Bebådelsedagen tränger undan den fastesöndag den hamnar på. Det sker
  varje år, eftersom 22–28 mars alltid ligger i fastan.
- Johannes Döparens dag tränger undan en söndag efter trefaldighet, ibland
  Apostladagen.
- Mikaelidagen, Tacksägelsedagen och Söndagen efter alla helgons dag
  tränger undan var sin söndag efter trefaldighet.

Två dagar med samma rang hamnar aldrig på samma datum. Det är inte en
tillfällighet utan följer av intervallen: de fasta dagarna ligger utanför
alla söndagsintervall, och söndagsserierna slutar där nästa period börjar.

### Flytt

Två dagar flyttar i stället för att vika.

**Kyndelsmässodagen**:

> Om denna söndag är fastlagssöndagen flyttas kyndelsmässodagen till
> närmast föregående söndag.
>
> — 28 kap. 1 §

Den läggs alltså en vecka tidigare, på söndagen 26 januari–1 februari, om
söndagen 2–8 februari är Fastlagssöndagen. Det inträffar när påsken
infaller 23–29 mars.

**Jungfru Marie bebådelsedag**:

> Om denna söndag är palmsöndagen eller påskdagen flyttas jungfru Marie
> bebådelsedag till söndagen närmast före palmsöndagen.
>
> — 28 kap. 1 §

Söndagen 22–28 mars är antingen palmsöndagen, påskdagen eller en söndag
före palmsöndagen, aldrig något däremellan. Regeln kan därför uttryckas
som: bebådelsedagen är det tidigaste av söndagen 22–28 mars och söndagen
fjorton dagar före påsk. Tidigast möjliga datum är den 8 mars, vid påsk
den 22 mars.

### Undantaget som modellen inte rymmer

När julafton är en söndag firas både Fjärde söndagen i advent och
Julnatten samma dag, den förra på förmiddagen och den senare vid midnatt.
Det är den enda gången på kyrkoåret då två uppsättningar läsningar hör till
samma datum. Kyrkoordningen känner bara fjärde advent, eftersom Julnatten
inte står i 28 kap. 1 § utan i evangelieboken. I dag ger rangen söndagen
företräde och Julnatten utgår de åren. Det inträffar 2028, 2034, 2045,
2051 och 2056.

## Alternativa namn

Fem dagar har två namn i kyrkoordningen, skilda med "eller": "annandag jul
eller den helige Stefanos dag", "sexagesima eller reformationsdagen",
"heliga trefaldighets dag eller missionsdagen", "kyndelsmässodagen eller
jungfru Marie kyrkogångsdag" och "söndagen efter alla helgons dag eller
alla själars dag". Beräkningen använder det första namnet, som också är
det evangelieboken har.

Kyrkoordningen markerar dessutom med en asterisk de dagar som är kyrkliga
högtidsdagar utan att vara allmänna helgdagar enligt lagen: Askonsdagen,
Skärtorsdagen och Annandag pingst. Det påverkar inte beräkningen.

## Läsningsserierna

Evangelieboken har tre uppsättningar texter för varje dag, och kyrkoåret
växlar mellan dem.

**Läsningsserien** går i treårscykler med start 2003. Serien för kyrkoåret
*Y* är (*Y* − 2003) mod 3 + 1. Kyrkoåret 2026 har serie 3, 2027 serie 1.

**Påskserien** går i fyraårscykler med start 2004 och gäller bara
Palmsöndagen, Skärtorsdagen, Långfredagen, Påsknatten och Påskdagen.
Serien för kyrkoåret *Y* är (*Y* − 2004) mod 4 + 1. Kyrkoåret 2026 har
påskserie 3, 2027 påskserie 4.

Tre- och fyraårscyklerna sammanfaller vart tolfte år, så samma kombination
av texter återkommer först då.

## Spännvidder

Några gränsvärden som följer av reglerna ovan.

| | Tidigast | Senast |
|---|---|---|
| Påskdagen | 22 mars | 25 april |
| Septuagesima | 18 januari | 21 februari |
| Askonsdagen | 4 februari | 10 mars |
| Kyndelsmässodagen | 26 januari | 8 februari |
| Jungfru Marie bebådelsedag | 8 mars | 28 mars |
| Kristi himmelsfärds dag | 30 april | 3 juni |
| Pingstdagen | 10 maj | 13 juni |
| Heliga trefaldighets dag | 17 maj | 20 juni |
| Domssöndagen | 20 november | 26 november |
| Första söndagen i advent | 27 november | 3 december |

Antalet dagar i ett kyrkoår är 65 eller 66. Det varierar så lite för att
de villkorliga dagarna och de undanträngda söndagarna tar ut varandra: ett
år med tidig påsk har färre söndagar efter trettondedagen men fler efter
trefaldighet.
