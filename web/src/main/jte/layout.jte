@import gg.jte.Content
@param String title
@param Content content
@param java.util.List<Integer> years = java.util.List.of()
@param String feedWebcal = ""
<!DOCTYPE html>
<html lang="sv">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="theme-color" content="#111111">
  <meta name="description" content="Svenska kyrkans kyrkoår: dagens namn, tema och texter ur evangelieboken.">
  <title>${title}</title>
  <link rel="icon" href="/favicon.ico">
  <link rel="apple-touch-icon" href="/img/apple-touch-icon-180x180.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Cinzel+Decorative:wght@400;700&family=Libre+Baskerville:ital,wght@0,400;0,700;1,400&display=swap">
  <link rel="stylesheet" href="/css/lektionarium.css">
  <script src="/webjars/htmx.org/dist/htmx.min.js" defer></script>
</head>
<body>
<header class="topbar">
  <a class="brand" href="/">
    <img src="/img/logo_notext.svg" alt="" width="28" height="28">
    <span>Lektionarium</span>
  </a>
  @if(!years.isEmpty())
    <nav class="downloads" aria-label="Ladda hem kalender">
      <details>
        <summary>Kalender</summary>
        <ul>
          <li class="feed">
            <a href="${feedWebcal}">Evighetskalender &ndash; prenumerera</a>
            <p>Alltid uppdaterad. Läggs till en gång och sköter sig sedan själv.</p>
          </li>
          <li class="heading">Ladda hem ett enskilt år</li>
          @for(Integer year : years)
            <li><a href="/ical/${year}">Kyrkoåret ${year} (.ics)</a></li>
          @endfor
          @for(Integer year : years)
            <li><a href="/csv/${year}">Kyrkoåret ${year} (.csv)</a></li>
          @endfor
        </ul>
      </details>
    </nav>
  @endif
</header>

<main>
  ${content}
</main>

<footer class="sitefooter">
  <img src="/img/logo_transparent.svg" alt="Lektionarium" width="120">
  <p>Lektionarium för kyrkoåret i Svenska kyrkan</p>
  <ul>
    <li><a href="https://marvi.io">Gjort med &#9829; av marvi.io</a></li>
    <li><a href="https://github.com/marvi/lektionarium">Källkod och buggrapporter</a></li>
    <li><a href="https://bibeln.se">Bibeltext från Bibel 2000. &#169; Svenska Bibelsällskapet</a></li>
  </ul>
</footer>
</body>
</html>
