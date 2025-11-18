# Poradce pro výběr oboru na SPŠE Ječná

Tento jednoduchý desktopový program slouží jako interaktivní dotazník, který má studentům prvních ročníků SPŠE Ječná pomoci s rozhodováním při výběru studijního oboru. Na základě série otázek typu "ano/ne" aplikace vyhodnotí, zda má student větší předpoklady pro obor **Informační technologie** nebo **Elektrotechnika a robotika**.

## Jak spustit aplikaci

Aplikace je napsaná v Javě a pro její spuštění je potřeba mít nainstalované prostředí JRE (Java Runtime Environment) ve verzi 17 nebo novější.

### Spuštění pomocí `run.bat` (pro Windows)

Nejjednodušší způsob, jak aplikaci spustit, je pomocí přiloženého skriptu `run.bat`.

1.  **Sestavení projektu:** Ujistěte se, že máte projekt sestavený (např. pomocí Gradle: `./gradlew build`). Výsledný `.jar` soubor by se měl nacházet ve složce `build/libs/`.
2.  **Konfigurace:** Všechny potřebné soubory (otázky, obrázky, logo) musí být ve složce `config/`.
3.  **Úprava `run.bat`:** Otevřete soubor `run.bat` v textovém editoru a **upravte cesty** k vaší instalaci Javy (`JAVA_HOME`), ke složce s `.jar` souborem (`JAR_DIR`) a ke konfigurační složce projektu (`PROJECT_DIR`).
4.  **Spuštění:** Po uložení změn jednoduše poklepejte na soubor `run.bat`.

#### Příklad souboru `run.bat`

```batch
rem @ECHO OFF

rem Upravte tuto cestu tak, aby ukazovala na vaši instalaci JDK 17 nebo novější.
SET JAVA_HOME=C:\cesta\k\vasemu\jdk-17

rem Upravte tuto cestu na složku 'build/libs' ve vašem projektu.
SET JAR_DIR=C:\cesta\k\projektu\SpseCourseAdvisor\build\libs

rem Upravte tuto cestu na složku 'config' ve vašem projektu.
SET PROJECT_DIR=C:\cesta\k\projektu\SpseCourseAdvisor\config

%JAVA_HOME%\bin\java.exe -jar %JAR_DIR%\SpseCourseAdvisor-1.0-SNAPSHOT.jar %PROJECT_DIR%

PAUSE
```

> **Důležité:** Bez správného nastavení cest v souboru `run.bat` se aplikace nespustí!

## Jak to funguje

1.  **Úvodní obrazovka:** Po spuštění se zobrazí uvítací obrazovka s logem školy.
2.  **Dotazník:** Po kliknutí na tlačítko "Začít formulář" se spustí samotný dotazník.
3.  **Odpovídání:** Na každou otázku student odpovídá "Ano" nebo "Ne". Některé otázky mohou být doplněny obrázkem pro lepší představu.
4.  **Vyhodnocení:** Po zodpovězení všech otázek aplikace zobrazí výsledek – doporučený obor a procentuální shodu.
5.  **Restart:** Dotazník je možné kdykoliv opakovat pomocí tlačítka "Zkusit znovu".

Aplikaci lze kdykoliv ukončit stisknutím klávesy `ESC`.

## Konfigurace a úpravy

Chování dotazníku, otázky a obrázky lze snadno upravovat bez nutnosti měnit kód aplikace. Vše je definováno v souboru `config/questions.json`.

### Struktura `questions.json`

Soubor má následující strukturu:

```json
{
  "title": "Dotazník pro výběr oboru",
  "questions": [
    {
      "prompt": "Baví tě skládat a rozebírat věci, abys zjistil, jak fungují?",
      "fieldForYes": "Elektrotechnika a robotika",
      "image": "obrazek1.jpg"
    },
    {
      "prompt": "Zajímá tě, jak fungují webové stránky a chtěl bys tvořit vlastní?",
      "fieldForYes": "Informační technologie",
      "image": null
    }
  ]
}
```

**Popis polí:**

-   `prompt`: Text otázky, který se zobrazí uživateli.
-   `fieldForYes`: Název oboru, ke kterému se přičte bod, pokud uživatel odpoví "Ano". Musí přesně odpovídat názvům oborů používaným v aplikaci ("Informační technologie", "Elektrotechnika a robotika").
-   `image`: (Nepovinné) Název souboru s obrázkem, který se má zobrazit u otázky. Obrázek musí být umístěn ve složce `config/`. Pokud obrázek není potřeba, nastavte hodnotu na `null` nebo pole úplně vynechte.

Otázky se při každém spuštění dotazníku náhodně zamíchají.

---

*Tento projekt vznikl jako nástroj pro usnadnění volby budoucího zaměření studentů SPŠE Ječná.*
