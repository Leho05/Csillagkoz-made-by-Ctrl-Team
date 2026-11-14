# Csillagközi Légitársaság - Jegyfoglaló Rendszer (Ctrl-Team)

Egy asztali JavaFX alkalmazás, amely intergalaktikus űrutazások jegyfoglalását, ülésválasztását, fizetési folyamatát és felhasználói adminisztrációját kezeli helyi SQLite adatbázis támogatással.

---

## Főbb funkciók

- **Felhasználó- és jogosultságkezelés**: Regisztráció és bejelentkezés szerepkörök (`USER` és `ADMIN`) megkülönböztetésével, munkamenet-kezeléssel (`SessionManager`), valamint regisztráció nélküli vendég móddal.
- **Dinamikus járatkezelés**: A célállomások (Mars, Hold, külső bolygók és holdak bázisai) és járatinformációk közvetlenül a helyi SQLite adatbázisból töltődnek be (`FlightDao.getAllFlights()`).
- **Intelligens naptárkezelés**: A felület nem enged múltbeli dátumra foglalást indítani, a személyes adatoknál pedig tiltja a jövőbeli születési dátumokat, színezéssel jelezve a nem választható napokat.
- **Többosztályos kabinkezelés**: Foglalási lehetőség Turista (Economy), Üzleti (Business) és Első osztályon (First Class), külön vizuális üléskiosztással.
- **Automatikus árkalkuláció**: Üzleti szabályok szerinti kedvezmények számítása célállomás típus (Hold úti célok esetén 25% kedvezmény), utastípus (gyermek, diák, nyugdíjas) és kabinosztály-szorzók (Business: 1.5x, First Class: 2.5x) alapján.
- **Személyes adatok és okmányok validációja**: Névformátum, korhatár (18+), e-mail cím, telefonszám, valamint útlevél és személyi igazolvány formai ellenőrzése.
- **Fizetési terminál szimuláció**: Bankkártyás fizetési felület a kártyaszám (16 számjegy), a lejárati dátum (HH/ÉÉ) és a CVC kód (3 számjegy) formai ellenőrzésével.
- **Tranzakciókezelés és adatbázis-seeding**: Az alkalmazás indításakor automatikusan felépíti a relációs sémát, előkészíti a tesztjáratokat és az alapértelmezett fiókokat. A foglalások mentése adatbázis-tranzakcióban (`commit` / `rollback`) történik az adatkonzisztencia megőrzése érdekében.
- **Központi állapotkezelés (BookingModel & ModelAware)**: A nézetek közötti adatáramlást egy dedikált `BookingModel` entitás és a `ModelAware` interfész biztosítja a kereséstől a fizetés lezárásáig.

---

## Használt technológiák

- **Programozási nyelv**: Java 17+ (Java 21 támogatással)
- **Grafikus felület (GUI)**: JavaFX (FXML és Controls)
- **Adatbázis & Perzisztencia**: SQLite (JDBC driver)
- **Build rendszer**: Apache Maven
- **Naplózás**: SLF4J (NOP logger)

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
    │   │   ├── LoginController.java         # Bejelentkezés, regisztráció és vendég mód vezérlője
    │   │   ├── TicketBookingController.java # Fő foglalási felület vezérlője
    │   │   ├── EconomyController.java       # Turista osztály ülésválasztása
    │   │   ├── BusinessController.java      # Üzleti osztály ülésválasztása
    │   │   ├── FirstClassController.java    # Első osztály ülésválasztása
    │   │   ├── PersonalDataController.java  # Utas személyes adatainak bekérése és validációja
    │   │   ├── PaymentController.java       # Fizetési felület és kártyaadatok ellenőrzése
    │   │   └── ModelAware.java              # Interfész a modell megosztásához a vezérlők között
    │   ├── database/                        # Adatbázis réteg
    │   │   ├── Database.java                # Kapcsolatkezelés és inicializálás/seeding
    │   │   ├── FlightDao.java               # Járatok lekérdezése és módosítása
    │   │   ├── PassengerDao.java            # Utasok mentése és lekérdezése
    │   │   ├── TicketDao.java               # Lefoglalt jegyek mentése
    │   │   └── UserDao.java                 # Felhasználók hitelesítése és regisztrációja
    │   ├── model/                           # Entitások és adatmodellek
    │   │   ├── BookingModel.java            # Központi foglalási adatáramlási modell
    │   │   ├── Destination.java             # Célállomás (név, típus, alapár)
    │   │   ├── Flight.java                  # Járat modell (kapacitás, határidők, státusz)
    │   │   ├── Passenger.java               # Utas modell (galaktikus ID, név, kor)
    │   │   ├── Ticket.java                  # Jegy modell (ár, foglalási dátum, osztály)
    │   │   └── User.java                    # Felhasználó modell (felhasználónév, jelszó, szerepkör)
    │   └── service/                         # Üzleti logika és munkamenet
    │       ├── BookingService.java          # Jegyár-számítás, validáció és tranzakciós mentés
    │       └── SessionManager.java          # Bejelentkezett felhasználó munkamenetének kezelése
    └── resources/org/csillagkoz/            # FXML felületi nézetek
        ├── login.fxml                       # Bejelentkező és regisztrációs nézet
        ├── ticket-booking.fxml              # Főoldali kereső és szűrő nézet
        ├── economy-seats.fxml               # Turista osztály üléstérképe
        ├── business-seats.fxml              # Üzleti osztály üléstérképe
        ├── firstclass-seats.fxml            # Első osztály üléstérképe
        ├── personal-data.fxml               # Személyes adatbekérő űrlap
        └── payment.fxml                     # Fizetési terminál nézet
```
---

## Futtatás és tesztfiókok

Az alkalmazás elindítható közvetlenül az `App.java` osztály `main` metódusával a fejlesztői környezetből, vagy parancssorból Maven segítségével:

```bash
mvn clean javafx:run