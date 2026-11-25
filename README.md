# Csillagközi Légitársaság - Jegyfoglaló Rendszer (Ctrl-Team)

Egy asztali JavaFX alkalmazás, amely intergalaktikus űrutazások jegyfoglalását, kabinválasztását, fizetési folyamatát és adminisztrációját kezeli helyi SQLite adatbázis támogatással.

---

## Főbb funkciók

- **Felhasználó- és jogosultságkezelés**: Kétlépcsős bejelentkezés (felhasználói belépés, adminisztrátori belépés, regisztráció, vendég mód) `USER` és `ADMIN` szerepkörökkel, munkamenet-kezeléssel, valamint mentett személyes adatok automatikus kitöltésével és profilba mentésével.
- **Dinamikus járatkezelés**: A célállomások (Mars, Hold, külső bolygók és holdak bázisai), alapárak és szabad helyek közvetlenül a helyi SQLite adatbázisból töltődnek be (`FlightDao.getAllFlights()`).
- **Intelligens naptárkezelés**: A felület nem enged múltbeli dátumra foglalást indítani, a személyes adatoknál pedig tiltja a jövőbeli születési dátumokat, színezéssel jelezve a nem választható napokat.
- **Többosztályos kabinkezelés és ülésválasztás**: Foglalási lehetőség Turista (Economy), Üzleti (Business) és Első osztályon (First Class) interaktív székválasztóval (`SeatSelectionHelper`), amely jelzi a szabad, kiválasztott és már lefoglalt helyeket.
- **Kosárkezelés és több jegy vásárlása**: A személyes adatok megadása után a jegy kosárba tehető (`CartItem`), így egy menetben több jegy is összeállítható, a fizetési felületen pedig megtekinthetők és törölhetők a kosár tételei.
- **Automatikus árkalkuláció (`BookingService`)**: A végső jegyár elég bonyolult: a kiválasztott járat adatbázisbeli alapárából, a célállomás típusából (Hold úti célok esetén 25% kedvezmény), az utastípusból (gyermek 50%, diák 20%, nyugdíjas 30% kedvezmény) és a kabinosztály-szorzóból (Business: 1.5x, First Class: 2.5x) számolódik.
- **Személyes adatok, autofill és okmányok validációja**: Weboldalszerű lenyíló automatikus kitöltés bejelentkezett felhasználóknak, születési év választó, névformátum, korhatár (18+), e-mail cím, telefonszám, valamint útlevél és személyi igazolvány formai ellenőrzése.
- **Fizetési terminál szimuláció és tranzakciós mentés**: Bankkártyás fizetési felület kártyaszám- (16 számjegy), lejárat- (`HH/ÉÉ`, nem lejárt kártya) és CVC-ellenőrzéssel (3 számjegy), amely sikeres ellenőrzés után egyetlen adatbázis-tranzakcióban rögzíti az utasokat, a jegyeket és csökkenti a járatok szabad helyeit.
- **Adminisztrációs központ (`AdminController`)**: Az `ADMIN` szerepkörrel belépett felhasználók megtekinthetik és kezelhetik a rögzített jegyfoglalásokat, járatokat és regisztrált felhasználói fiókokat (beleértve a kijelölt jegyek, járatok és fiókok törlését).
- **Hibabejelentő (`ErrorReportDialog`)**: Minden főbb képernyőről elérhető beépített hibabejelentő ablak.
---

## Használt technológiák

- **Programozási nyelv**: Java 11+ (JDK 21 támogatással)
- **Grafikus felület (GUI)**: JavaFX 13 (FXML és Controls)
- **Adatbázis & Perzisztencia**: SQLite (`sqlite-jdbc`)
- **Build rendszer**: Apache Maven
- **Naplózás**: SLF4J (`slf4j-nop`)

---

## Projektstruktúra és architektúra

A projekt rétegzett MVC (Model-View-Controller) és DAO (Data Access Object) mintára épül:

```text
Csillagkoz-made-by-Ctrl-Team/
│
├── pom.xml                                  # Eredeti Maven projektleíró (App)
├── visual-pom.xml                           # Külön Maven projektleíró a grafikai előnézethez (VisualApp)
├── start-visual.cmd                         # Kényelmi indítószkript a grafikai előnézethez (Windows)
├── GRAFIKAI-UTMUTATO.md                     # Grafikai előnézet dokumentációja
├── csillagkoz.db                            # Helyi SQLite adatbázis (első futtatáskor jön létre)
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── org/csillagkoz/
    │       ├── App.java                         # Alap belépési pont, JavaFX életciklus, DB inicializálás
    │       ├── VisualApp.java                   # Opcionális grafikai belépési pont
    │       ├── controller/                      # GUI vezérlők
    │       │   ├── LoginController.java         # Bejelentkezés, regisztráció és vendég mód vezérlője
    │       │   ├── TicketBookingController.java # Fő foglalási felület vezérlője
    │       │   ├── EconomyController.java       # Turista osztály nézet
    │       │   ├── BusinessController.java      # Üzleti osztály nézet
    │       │   ├── FirstClassController.java    # Első osztály nézet
    │       │   ├── SeatSelectionHelper.java     # Közös székválasztó rács és foglaltságkezelés
    │       │   ├── PersonalDataController.java  # Utas adatok validációja és árkalkuláció
    │       │   ├── PaymentController.java       # Fizetési felület, kártyaellenőrzés és mentés
    │       │   ├── AdminController.java         # Adminisztrációs felület vezérlője
    │       │   ├── ErrorReportController.java   # Hibabejelentő ablak vezérlője
    │       │   ├── ErrorReportDialog.java       # Hibabejelentő felugró ablak segédosztály
    │       │   └── ModelAware.java              # Interfész a modell megosztásához a vezérlők között
    │       ├── database/                        # Adatbázis réteg
    │       │   ├── Database.java                # Kapcsolatkezelés és inicializálás/seeding
    │       │   ├── FlightDao.java               # Járatok lekérdezése és módosítása
    │       │   ├── PassengerDao.java            # Utasok mentése és lekérdezése
    │       │   ├── TicketDao.java               # Lefoglalt jegyek mentése és összesítése
    │       │   └── UserDao.java                 # Felhasználók hitelesítése és regisztrációja
    │       ├── model/                           # Entitások és adatmodellek
    │       │   ├── BookingModel.java            # Központi foglalási adatáramlási modell és kosár
    │       │   ├── CartItem.java                # Kosárba tett jegy adatait tároló modell
    │       │   ├── Destination.java             # Célállomás (név, típus, alapár)
    │       │   ├── Flight.java                  # Járat modell (kapacitás, szabad helyek, határidők, státusz)
    │       │   ├── Passenger.java               # Utas modell (galaktikus ID, név, kor)
    │       │   ├── Ticket.java                  # Jegy modell (ár, foglalási dátum, osztály)
    │       │   └── User.java                    # Felhasználó modell (felhasználónév, jelszó, szerepkör)
    │       ├── service/                         # Üzleti logika és munkamenet
    │       │   ├── BookingService.java          # Jegyár-számítás, validáció és tranzakciós mentés
    │       │   └── SessionManager.java          # Bejelentkezett felhasználó munkamenetének kezelése
    │       └── visual/                          # Opcionális grafikai réteg (VisualApp)!!!!
    │           ├── SpaceAssets.java
    │           ├── SpaceBackdrop.java
    │           ├── SpaceRoute.java
    │           ├── SpaceTheme.java
    │           ├── SpaceView.java
    │           └── TicketPreview.java
    └── resources/org/csillagkoz/                # FXML felületi nézetek és vizuális erőforrások
        ├── login.fxml                           # Bejelentkező és regisztrációs nézet
        ├── ticket-booking.fxml                  # Főoldali kereső és szűrő nézet
        ├── economy-seats.fxml                   # Turista osztály nézet
        ├── business-seats.fxml                  # Üzleti osztály nézet
        ├── firstclass-seats.fxml                # Első osztály nézet
        ├── personal-data.fxml                   # Személyes adatbekérő űrlap
        ├── payment.fxml                         # Fizetési terminál nézet
        ├── admin.fxml                           # Adminisztrációs központ nézet
        ├── error-report.fxml                    # Hibabejelentő ablak nézet
        └── visual/                              # Grafikai stíluslap, leképezés és képek
```

---

## Futtatás és tesztfiókok

### Alap alkalmazás indítása
Az alkalmazás elindítható az `App.java` osztály `main` metódusával az IDE-ből, vagy parancssorból Maven segítségével:

```bash
mvn clean javafx:run
```

### Grafikai előnézeti mód indítása
```bash
mvn -f visual-pom.xml compile javafx:run
```
Windows alatt a `start-visual.cmd` fájllal is elindítható.

### Alapértelmezett tesztfiókok
Az adatbázis inicializálásakor (`Database.java`) az alábbi két fiók jön létre automatikusan:

| Felhasználónév | Jelszó | Szerepkör |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `utas` | `utas123` | `USER` |
