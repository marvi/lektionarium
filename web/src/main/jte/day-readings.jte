@param java.util.Map<String, Object> dayData

<h1>${String.valueOf(dayData.get("day"))}</h1>
@if(dayData.containsKey("theme"))
    <h2>${String.valueOf(dayData.get("theme"))}</h2>
@endif
<p>Datum: ${String.valueOf(dayData.get("date"))}</p>

<div class="navigation">
    <button hx-get="/htmx-previous/${String.valueOf(dayData.get("date"))}" hx-target="#readings-container">Föregående dag</button>
    <button hx-get="/htmx-next/${String.valueOf(dayData.get("date"))}" hx-target="#readings-container">Nästa dag</button>
</div>

@if(dayData.containsKey("readings"))
    <div class="readings">
        <h2>Läsningar</h2>
        @for(var reading : (java.util.List<?>)dayData.get("readings"))
            @if(reading instanceof java.util.Map)
                <div class="reading">
                    <h3>${String.valueOf(((java.util.Map<?, ?>)reading).get("type"))}</h3>
                    <p>${String.valueOf(((java.util.Map<?, ?>)reading).get("text"))}</p>
                    @if(((java.util.Map<?, ?>)reading).containsKey("content"))
                        <p>${String.valueOf(((java.util.Map<?, ?>)reading).get("content"))}</p>
                    @endif
                </div>
            @endif
        @endfor
    </div>
@endif

@if(dayData.containsKey("memorials") && !((java.util.List<?>)dayData.get("memorials")).isEmpty())
    <div class="memorials">
        <h2>Minnesdagar</h2>
        <ul>
            @for(var memorial : (java.util.List<?>)dayData.get("memorials"))
                <li>${String.valueOf(memorial)}</li>
            @endfor
        </ul>
    </div>
@endif
