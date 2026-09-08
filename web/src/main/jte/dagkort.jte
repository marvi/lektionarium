@param lectio.web.DayView day
<article id="dagkort" class="daycard" aria-live="polite">
  <p class="date">${day.humanDate()}</p>
  <h1>${day.name()}</h1>
  <p class="color">
    <span class="swatch swatch-${day.color()}" aria-hidden="true"></span>
    Liturgisk färg: ${day.colorName()}
  </p>

  @if(day.hasReadings())
    <h2 class="theme">${day.theme()}</h2>
    <ul class="readings">
      @for(lectio.web.DayView.ReadingView reading : day.readings())
        <li>
          @if(reading.hasText())
            <details>
              <summary><span class="label">${reading.label()}</span> ${reading.sweRef()}</summary>
              <p class="bibletext">${reading.text()}</p>
            </details>
          @else
            <span class="label">${reading.label()}</span> ${reading.sweRef()}
          @endif
        </li>
      @endfor
    </ul>
  @endif

  <nav class="pager">
    <a class="button" href="/dag/${day.previousDate()}"
       hx-get="/dag/${day.previousDate()}" hx-target="#dagkort"
       hx-swap="outerHTML" hx-push-url="true" rel="prev">&larr; Förra</a>
    <a class="button" href="/dag/${day.nextDate()}"
       hx-get="/dag/${day.nextDate()}" hx-target="#dagkort"
       hx-swap="outerHTML" hx-push-url="true" rel="next">Nästa &rarr;</a>
  </nav>
</article>
