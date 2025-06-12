@param java.util.Map<String, Object> dayData

<!DOCTYPE html>
<html>
<head>
    <title>Lektionarium - ${String.valueOf(dayData.get("day"))}</title>
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
        h1, h2, h3 {
            color: #333;
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
        .readings {
            margin-top: 20px;
        }
        .reading {
            margin-bottom: 15px;
        }
        .memorials {
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <h1>${String.valueOf(dayData.get("day"))}</h1>
    @if(dayData.containsKey("theme"))
        <h2>${String.valueOf(dayData.get("theme"))}</h2>
    @endif
    <p>Datum: ${String.valueOf(dayData.get("date"))}</p>

    <div class="navigation">
        <button hx-get="/web-previous/${String.valueOf(dayData.get("date"))}" hx-target="body">Föregående dag</button>
        <button hx-get="/web-next/${String.valueOf(dayData.get("date"))}" hx-target="body">Nästa dag</button>
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
</body>
</html>
