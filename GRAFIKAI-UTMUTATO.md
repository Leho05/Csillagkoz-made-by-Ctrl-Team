# Csillagköz – helyi grafikai előnézet

## Elindítás IntelliJ IDEA-ban

1. Nyisd meg **ezt a Csillagkoz-visual mappát** külön projektként. A D: meghajtón lévő eredeti projektet nem kell átállítani.
2. Project SDK: **JDK 21**. A gépen ellenőrzött változat: Microsoft OpenJDK 21.0.13.0.2.
3. Az IntelliJ Maven ablakában válaszd az **Execute Maven Goal** lehetőséget.
4. A munkamappa ez a mappa legyen, a parancs pedig:

   ```text
   -f visual-pom.xml compile javafx:run
   ```

   Ha az IntelliJ külön kéri a parancs elejét, terminálban:

   ```text
   mvn -f visual-pom.xml compile javafx:run
   ```

Az eredeti `pom.xml` szerinti egyszerű `javafx:run` továbbra is az eredeti megjelenést indítja. A grafikai változathoz a **visual-pom.xml** szükséges.

Alternatíva ezen a gépen: dupla kattintás a `start-visual.cmd` fájlra. Ez megkeresi a Maven parancsot, vagy a jelenlegi IntelliJ beépített Mavenjét használja. A szkript nem commitol és nem kommunikál a GitHubbal. Hiányzó Maven-függőséget a Maven szokásos módon letölthet; az ellenőrzött gépen az offline fordítás és indítás is működött.

Az indítás a másolat adatbázisát a **meglévő programkód szerint** újrainicializálja. A D: meghajtón lévő eredeti adatbázishoz ez a másolat nem nyúl.

## Munkavégzés és az eredeti példány védelme

- Eredeti projekt: `D:\Egyetem\Csillagkoz-made-by-Ctrl-Team\Csillagkoz-made-by-Ctrl-Team`.
- Kiinduló Git-állapot: `main...origin/main`; egyetlen módosult fájl: `csillagkoz.db`.
- Kiinduló commit: `72f6eda749fc5c4c0a024c0d42a4d10cbdd42730`.
- A másolatba az eredeti 32 fájl került át; a `.git` könyvtár és a korábbi fordítási eredmények nem.
- **Meglévő fájlból egyet sem kellett módosítani**, a másolatban sem. Az App, a vezérlők, az öt FXML, a modellek, a DAO-k, a szolgáltatás és az eredeti pom.xml változatlan.
- A forrásfájlok és az eredeti adatbázis épségét SHA-256 ellenőrzőösszegekkel is ellenőriztem.
- A grafikus futtatások külön tesztpéldányokban történtek.
- Nem készült commit, branch, push, pull request vagy GitHub-módosítás.

## Mi készült el?

### Egységes háttér és elrendezés

Mind az öt meglévő képernyő ugyanazt a méretarányt tartó űrhátteret kapja. A középső űrlap sötét, jól elkülönített felületen marad, világos mezőkkel. A háttér középre igazított kivágást használ: nagyobb ablakban többet mutat a forrásképből, és nem nyújtja szét azt.

960 képpont körüli szélességtől három oszlop látható. 860 képpont alatt az űrlap felülre, a két képes panel alá kerül; a képes előnézetek kisebbek lesznek. A túl hosszú tartalmak függőlegesen görgethetők. A minimális ablakméret 680 × 540.

A kezdőképernyőn balra az indulási objektum, jobbra a célállomás jelenik meg. Az indulási helyek és célállomások forrása a meglévő felület, illetve adatbázis marad. Új célállomás felvétele nem igényel Java-kódmódosítást a tartalékkép megjelenéséhez.

### Animációk

- Három dekorációs bolygó: 21–31 másodperces lassú lebegés és enyhe, oda-vissza dőlés.
- A főoldalon két fekete lyuk, a további képernyőkön egy: 34–38 másodperces finom mozgás. A harmadik kép az erőforrások között megvan, de nem zsúfolja a kompozíciót.
- Három üstökös: eltérő kezdési idő, útvonal, 14/19/25 másodperces áthaladás és köztük pihenőidő. Két eltérő irány és eltérő magasságok.
- Külön, kisméretű űrhajó: 26 másodperces távoli átrepülés, két repülés között 18 másodperces szünet.
- Minden mozgás JavaFX-animáció: TranslateTransition, RotateTransition, ParallelTransition és egy átméretezéshez alkalmazkodó Transition.
- A dekorációs réteg egérátlátszó. A képek gyorsítótárból jönnek; nincs képkockánkénti újratöltés.
- A fejlécben az animációk szüneteltethetők. Ikonizáláskor szünetelnek; lapváltáskor a régi animációk leállnak, és a régi figyelők lecsatlakoznak. Ablakbezáráskor a grafikai réteg felszabadítja a saját erőforrásait.

### A választásokra reagáló képek

| Választás | Grafikai eredmény |
|---|---|
| Indulási hely | A megfelelő Föld-, Hold- vagy Mars-kép |
| Célállomás | A megfelelő bolygó/hold, hiányzó hozzárendelésnél vagy fájlnál az ismeretlen bolygó |
| Gyermek / Diák | Fiatal űrlény |
| Teljes árú (Felnőtt) | Felnőtt űrlény |
| Nyugdíjas | Idős űrlény |
| Nincs kiválasztott utastípus | Felnőtt űrlény |
| Economy | A leharcoltabb űrhajó |
| Business | A normál/modernebb űrhajó |
| First Class | A luxusűrhajó |

A személyesadat-képernyő a korábban kiválasztott korosztály űrlényét mutatja. A grafikai állapot nem módosítja az életkori feltételeket vagy az árképzést.

### Élő jegykártya

Az űrjegykép nem volt a megadott mappában. A felhasználó jóváhagyása alapján **ideiglenes, CSS-sel formázott kártya** készült. Ez nem egy másik képpel helyettesített űrjegy.

A vezetéknév, keresztnév, születési dátum, okmányszám, e-mail és telefonszám szerkesztés közben frissül rajta. Az üres mezők helykitöltő szöveget mutatnak. A dátum szerkesztés közbeni szövegét is megjeleníti; annak érvényességét továbbra is a meglévő űrlap ellenőrzi. Visszalépés után a meglévő BookingModelből visszatöltött adatokat tükrözi.

Az előnézet nem ment adatot, nem hoz létre jegyet, nem hív DAO-t vagy foglalási szolgáltatást, és nem naplóz személyes adatot. A dekoratív csíkok nem valós vonalkódot jelentenek. A kártyán egyértelműen szerepel, hogy még nem érvényes jegy.

## Új fájlok

| Fájl vagy mappa | Feladat |
|---|---|
| `src/main/java/org/csillagkoz/VisualApp.java` | Külön, választható belépési pont az eredeti App alosztályaként |
| `src/main/java/org/csillagkoz/visual/SpaceTheme.java` | A grafikai réteg csatlakoztatása a lapváltásokhoz és az ablak életciklusához |
| `src/main/java/org/csillagkoz/visual/SpaceView.java` | Rugalmas elrendezés, meglévő vezérlők vizuális kiegészítése és képcserék |
| `src/main/java/org/csillagkoz/visual/SpaceBackdrop.java` | Méretarányt tartó háttér és dekorációs animációk |
| `src/main/java/org/csillagkoz/visual/SpaceAssets.java` | Classpath-képek, gyorsítótár, névnormalizálás és tartalékkép |
| `src/main/java/org/csillagkoz/visual/TicketPreview.java` | A csak megjelenítésre szolgáló élő jegykártya |
| `src/main/resources/org/csillagkoz/visual/space.css` | Külön vizuális stílus |
| `src/main/resources/org/csillagkoz/visual/assets.properties` | Bővíthető kép-hozzárendelések |
| `src/main/resources/org/csillagkoz/visual/images/` | A 26 eredeti PNG másolatai, áttekinthető alkönyvtárakban |
| `visual-pom.xml` | Önálló grafikai Maven-indítás; az eredeti pom.xml változatlan |
| `start-visual.cmd` | Kényelmi helyi indító |
| `visual-checks/org/csillagkoz/visual/AssetChecks.java` | Önálló, adatbázist nem érintő képfeloldási regressziótesztek |
| `GRAFIKAI-UTMUTATO.md` | Ez az útmutató és összefoglaló |

A `target-visual/` fordítási eredmény, újragenerálható. A grafikai bővítés nem használ további külső Java-könyvtárat.

## Képek leltára

Minden bemeneti fájl PNG. Az eredeti fájlokat nem alakítottam át: a másolatok csak hordozható, egyszerűbb neveket kaptak.

| Eredeti fájl | Új útvonal az images/ mappán belül |
|---|---|
| `hatter.png` | `background/space.png` |
| `bolygo_1.png`, `bolygo_2.png`, `bolygo_3.png` | `decor/planet-1.png`, `planet-2.png`, `planet-3.png` |
| `Feketelyuk_1.png`, `Feketelyuk_2.png`, `Feketelyuk_3.png` | `decor/blackhole-1.png`, `blackhole-2.png`, `blackhole-3.png` |
| `ustokos_1.png`, `ustokos_2.png`, `ustokos_3.png` | `decor/comet-1.png`, `comet-2.png`, `comet-3.png` |
| `urhalyó.png` | `decor/flyby.png` |
| `Fold.png`, `Hold.png`, `Mars.png` | `planets/earth.png`, `moon.png`, `mars.png` |
| `Titan.png`, `Europa.png`, `Venusz.png`, `Ceres.png` | `planets/titan.png`, `europa.png`, `venus.png`, `ceres.png` |
| `Ismeretlen_Bolygo.png` | `planets/unknown.png` |
| `Fiatal.png`, `Felnott.png`, `Idos.png` | `aliens/young.png`, `adult.png`, `senior.png` |
| `Alap_urleny.png` | `aliens/base.png` – megőrzött tartalék grafika |
| `urhajo_lepukkant.png`, `urhajo_normal.png`, `urhajo_luxus.png` | `cabins/economy.png`, `business.png`, `first-class.png` |

## Új célállomás képének hozzárendelése

Az `assets.properties` a grafikai csomag központi névjegyzéke. A célállomás nevét a program kisbetűssé alakítja, az ékezeteket, szóközöket és írásjeleket elhagyja.

Például a „Neptunusz Kutatóállomás” kulcsa:

```properties
destination.neptunuszkutatoallomas=images/planets/neptunusz.png
```

Ha nincs ilyen bejegyzés, vagy a hivatkozott kép hiányzik, az ismeretlen bolygó jelenik meg. Az adatbázis tartalmát a grafikai réteg nem módosítja.

## Ellenőrzések

- JDK 21-es Maven-fordítás: sikeres.
- A külön `visual-pom.xml` szerinti Maven-indítás: ténylegesen elindított grafikus alkalmazással ellenőrizve, elkülönített tesztpéldányban.
- A képfeloldás 47 automatikus ellenőrzése sikeres. Egy külön, szándékosan hiányzó Europa-képes tesztpéldány további 47 ellenőrzése is sikeres. A teszt többek között a jelenlegi hozzárendeléseket, az ismeretlen célállomást, az eltérő űrlényeket, kabinokat és a hiányzó fájl tartalékkezelését ellenőrzi.
- Grafikus felületen ellenőriztem a bolygó-, utastípus- és kabinképek cseréjét, mindhárom kabinoldal megnyitását, a személyes adatokra lépést, a hat adatmező azonnali előnézetét, valamint a visszalépés utáni adatvisszatöltést.
- A fejléc animációszünete működik; a mozgó objektumok több képernyőmegfigyelésen láthatóan haladtak. Számszerű FPS- vagy terhelésmérés nem történt.
- A 960 × 600-as induló nézetet, a teljes méretű ablakot és egy körülbelül 720 × 560-as keskeny ablakot is megtekintettem. Kis ablakban egyes tartalmak görgethetők.
- Az eredeti kereső üresmező-hibajelzései megmaradtak.

## Megmaradó korlátok és finomítási lehetőségek

- A végleges, képes űrjegyhátteret később még be kell építeni. A kártya tartalmának és méretének ehhez igazítása külön grafikai munka.
- A szaggatott útvonal és a mezőre mutató űrlény a kérés szerint nem készült el.
- A meglévő kabinoldalakon továbbra is az eredeti helykitöltő ülésfelület szerepel; ülésválasztási logikát ez a fejlesztés nem ad hozzá.
- A korábbi állapotfelmérésben jelzett üzleti hiányosságok – például a tényleges jegymentés hiánya és az indításkori adattörlés – változatlanok.
- A meglévő véglegesítő vezérlő konzolra ír személyes adatokat. Ezt nem módosítottam; az új jegyelőnézet nem ad hozzá ilyen naplózást.
- A meglévő Maven-függőségek SLF4J-figyelmeztetést adhatnak. A grafikai tesztfutást ez nem akadályozta; a függőségeket nem írtam át a látvány kedvéért.
- A Windowson most elérhető méreteken kívüli felbontások, más képernyőskálázások és más operációs rendszerek még nincsenek végigtesztelve.
- Az eredeti vezérlők és az FXML-struktúra változásakor érdemes újraellenőrizni a vizuális adapterben használt `fx:id` kapcsolatokat.
