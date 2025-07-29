## Wprowadzone zmiany
- Dodanie pola `favourite` do tabeli `note` oraz dodanie go do obiektu `CreateNoteBody`
- Dodanie klasy `NoteModel` w aplikacji Androidowej, w celu zmniejszenia zależności na schemacie
backendowej bazy danych
- Wydzielenie istniejących widoków do osobnych plików i funkcji, w celu zwiększenia ich reużywalności
- Stworzenie `MainUiState`
- Dodanie filtrów `ALL` i `FAVOURITES` do listy notatek
- Dodanie bazy danych do aplikacji poprzez bibliotekę Room
- Dodanie DI do aplikacji przy użyciu Koin- 

## Znalezione błędy
- Utrata treści tworzonej notatki przy zmianie orientacji ekranu - naprawione poprzez przeniesienie 
stanu pola tekstowego do ViewModelu
- Brak zmiany koloru tła przy przełączeniu na tryb ciemny - opakowanie istniejącego UI w Scaffold

## Sposób realizacji trybu offline
Przy użyciu WorkManagera, każda akcja (dodanie/usunięcie notatki, dodanie/usunięcie z ulubionych) 
zostaje zakolejkowana. Zadania zostają ograniczone do wykonania jedynie kiedy urządzenie ma dostęp
do internetu. Worker dla każdego z zadań, w wypadku problemów z połączeniem się z backendem podejmuje
ponowne próby. Kolejne akcje wykonane offline, są kolejkowane jako zadania zależne od poprzednich.
Notatka dodana w trybie offline nie może być ustawiona jako ulubiona, ze względu na zależność istniejącej
implementacji na id notatki na backendzie.