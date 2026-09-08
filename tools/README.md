# Verktyg

## release.sh

Släpper en ny version. Se avsnittet "Släppa en version" i projektets README.

```sh
tools/release.sh --dry-run
```

## remove_text.py

Tar bort bibeltexten ur evangelieboken och skriver en textlös variant.

Den fullständiga filen `svk_lektionarium.xml` innehåller bibeltext ur Bibel 2000
och ligger därför **inte** i det här repot. Det som är incheckat är resultatet,
`api/src/main/resources/lectio/svk_lektionarium_sans_text.xml`, som bara har
bibelhänvisningar.

Kör från den här katalogen med den fullständiga filen på plats:

```sh
python3 remove_text.py
```
