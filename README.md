# Asteroid Clicker

### ⚠️ Projekt uniwersytecki - używaj na własne ryzyko!

Asteroid Clicker to gra mobilna na system Android, w której gracz klika w asteroidę, aby zdobywać zasoby, kupować ulepszenia i odblokowywać osiągnięcia. Projekt został zrealizowany przy użyciu nowoczesnych narzędzi i rekomendowanej architektury dla platformy Android.

## 🚀 Funkcjonalności
- **Gra:** Interaktywna asteroida z animacjami, system automatycznego wydobycia (auto-mining) oraz ulepszania mocy kliknięcia.
- **Profil gracza:** Śledzenie statystyk (łączna gotówka, kliknięcia, wykupione ulepszenia, odblokowane osiągnięcia).
- **Ustawienia aplikacji:** Konfiguracja dźwięku i wibracji, wsparcie wielojęzyczności (Polski / Angielski) oraz opcja całkowitego resetu postępów.
- **Menu i Nawigacja:** Ekran powitalny prowadzący do panelu Gry, Profilu, Osiągnięć oraz panelu Kredytów.

## 🛠 Technologie i Architektura
- **Język:** Kotlin
- **UI:** Jetpack Compose (w tym Material Design 3 oraz system animacji).
- **Nawigacja:** Navigation Compose.
- **Baza danych:** Room Database (z użyciem Kotlin Flow do reaktywnego odświeżania UI).
- **Architektura:** MVVM (Model-View-ViewModel) z wykorzystaniem Coroutines.

---

## 🗺 Makiety przejść (Navigation Flow)
Poniżej znajduje się wizualizacja ścieżek nawigacji użytkownika w aplikacji (np. Menu Główne $\rightarrow$ Gra, Profil). 

*(Zaktualizuj poniższą ścieżkę do pliku po pobraniu obrazków z innej gałęzi)*

![Makieta przejść aplikacji](https://github.com/Sadowski-Krystian/AM-clicker/blob/chore/project-data/makieta_przejsc.png)

---

## 🗄 Schemat bazy danych
Aplikacja przechowuje stan gry lokalnie z wykorzystaniem biblioteki **Room**. Baza danych składa się z trzech głównych encji:
1. **`user_stats`** - Globalne statystyki gracza (np. `currentCash`, `clickPower`, `passiveIncomePerSecond`) oraz jego preferencje (`isSoundEnabled`, `selectedLanguage`).
2. **`upgrades`** - Informacje o posiadanych ulepszeniach i ich poziomach.
3. **`achievements`** - Rejestr postępów osiągnięć gracza.

*(Zaktualizuj poniższą ścieżkę do pliku po pobraniu obrazków z innej gałęzi)*

![Schemat Bazy Danych (Room)](https://github.com/Sadowski-Krystian/AM-clicker/blob/chore/project-data/database_scheme.png)

---

## ⚙️ Uruchomienie projektu
1. Sklonuj repozytorium na swój dysk lokalny.
2. Otwórz projekt w środowisku **Android Studio**.
3. Upewnij się, że synchronizacja Gradle (Gradle Sync) zakończyła się pomyślnie.
4. Zbuduj i uruchom aplikację na emulatorze lub podłączonym urządzeniu fizycznym z systemem Android (Minimalne API: 24, Docelowe API: 36).
