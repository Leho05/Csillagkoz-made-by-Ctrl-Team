# Csillagközi Légitársaság - Jegyfoglaló Rendszer (Ctrl-Team)

Egy asztali JavaFX alkalmazás, amely intergalaktikus űrutazások jegyfoglalását, ülésválasztását és adminisztrációját kezeli helyi SQLite adatbázis támogatással.

---

## Főbb funkciók

* **Dinamikus járatkezelés**: A célállomások és járatinformációk közvetlenül a helyi SQLite adatbázisból töltődnek be (`FlightDao.getAllFlights()`).
* **Intelligens naptárkezelés**: A felület nem enged múltbeli dátumra foglalást indítani: a naptár automatikusan letiltja és színezéssel jelzi a lezárult napokat.
* **Többosztályos kabinkezelés**: Foglalási lehetőség Turista (Economy), Üzleti (Business) és Első osztályon (First Class), külön vizuális üléskiosztással.
* **Automatikus árkalkuláció**: Üzleti szabályok szerinti kedvezmények számítása célállomás típus (pl. Hold úti célok esetén kedvezmény) és kabinosztály-szorzók (Business: 1.5x, First Class: 2.5x) alapján.
* **Automatikus adatbázis-inicializálás és Seeding**: Az alkalmazás indításakor felépíti a relációs sémát és előkészíti a tesztjáratokat.

---

## Használt technológiák

* **Programozási nyelv**: Java 17+ (Java 21 támogatással)
* **Grafikus felület (GUI)**: JavaFX (FXML és Controls)
* **Adatbázis & Perzisztencia**: SQLite (JDBC driver)
* **Build rendszer**: Apache Maven
* **Naplózás**: SLF4J (NOP logger)

---

## Projektstruktúra és architektúra

A projekt rétegzett MVC (Model-View-Controller) és DAO (Data Access Object) mintára épül:

```text
Csillagkoz-made-by-Ctrl-Team/
│
├── pom.xml                                  # Maven projektleíró és függőségek
├── csillagkoz.db                            # Helyi SQLite adatbázis (első futtatáskor jön létre)
└── src/main/
    ├── java/org/csillagkoz/
    │   ├── App.java                         # Belépési pont, JavaFX életciklus, DB inicializálás
    │   ├── controller/                      # GUI vezérlők
    │   │   ├── TicketBookingController.java # Fő foglalási felület vezérlője
    │   │   ├── EconomyController.java       # Turista osztály ülésválasztása
    │   │   ├── BusinessController.java      # Üzleti osztály ülésválasztása
    │   │   ├── FirstClassController.java    # Első osztály ülésválasztása
    │   │   └── PersonalDataController.java  # Utas személyes adatainak bekérése
    │   ├── database/                        # Adatbázis réteg
    │   │   ├── Database.java                # Kapcsolatkezelés és inicializálás/seeding
    │   │   ├── FlightDao.java               # Járatok lekérdezése és módosítása
    │   │   ├── PassengerDao.java            # Utasok mentése és lekérdezése
    │   │   └── TicketDao.java               # Lefoglalt jegyek mentése
    │   ├── model/                           # Entitások és adatmodellek
    │   │   ├── Destination.java             # Célállomás (név, típus, alapár)
    │   │   ├── Flight.java                  # Járat modell (kapacitás, határidők, státusz)
    │   │   ├── Passenger.java               # Utas modell (galaktikus ID, név, kor)
    │   │   └── Ticket.java                  # Jegy modell (ár, foglalási dátum, osztály)
    │   └── service/                         # Üzleti logika
    │       └── BookingService.java          # Jegyár-számítás és foglalási validáció
    └── resources/org/csillagkoz/            # FXML felületi nézetek
        ├── ticket-booking.fxml              # Főoldali kereső és szűrő nézet
        ├── economy-seats.fxml               # Turista osztály üléstérképe
        ├── business-seats.fxml              # Üzleti osztály üléstérképe
        ├── firstclass-seats.fxml            # Első osztály üléstérképe
        └── personal-data.fxml               # Adatbekérő űrlap