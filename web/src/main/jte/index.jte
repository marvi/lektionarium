@param java.util.List<Integer> years
@param java.util.Map<String, Object> dayData

<!DOCTYPE html>
<html>
<head>
    <title>Lektionarium</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            max-width: 800px;
            margin: 0 auto;
        }
        h1 {
            color: #333;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 20px;
        }
        .button:hover {
            background-color: #45a049;
        }
        .year-select {
            margin-top: 20px;
            padding: 10px;
            border-radius: 4px;
            border: 1px solid #ddd;
        }
        .download-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #2196F3;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-left: 10px;
            cursor: pointer;
        }
        .download-button:hover {
            background-color: #0b7dda;
        }
        .calendar-section {
            margin-top: 30px;
        }
        .readings-section {
            margin-top: 30px;
        }
        .readings {
            margin-top: 20px;
        }
        .reading {
            margin-bottom: 15px;
        }
        .memorials {
            margin-top: 20px;
        }
        .navigation {
            display: flex;
            justify-content: space-between;
            margin: 20px 0;
        }
        .navigation button {
            padding: 8px 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .navigation button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>

<h1>Välkommen till Lektionarium</h1>

<p>Detta är en webbapplikation för att visa liturgiska dagar och läsningar.</p>

<div class="readings-section">
    <h2>Dagens läsningar</h2>
    <div id="readings-container">
        @if(dayData != null)
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
        @endif
    </div>
</div>

<div class="calendar-section">
    <h2>Ladda ner kalender</h2>
    <p>Välj ett år för att ladda ner kalendern i iCal-format:</p>

    <select id="yearSelect" class="year-select">
        @for(Integer year : years)
            <option value="${year}">${year}</option>
        @endfor
    </select>

    <button id="downloadButton" class="download-button">Ladda ner kalender</button>
</div>

<script>
    document.getElementById('downloadButton').addEventListener('click', function() {
        const selectedYear = document.getElementById('yearSelect').value;
        window.location.href = '/ical/' + selectedYear;
    });
</script>

</body>
</html>
