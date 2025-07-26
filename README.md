## Wprowadzone zmiany
- Dodanie pola `favourite` do tabeli `note` oraz dodanie go do obiektu `CreateNoteBody`
- Dodanie klasy `NoteModel` w aplikacji Androidowej, w celu zmniejszenia zależności na schemacie
backendowej bazy danych
- Wydzielenie istniejących widoków do osobnych plików i funkcji, w celu zwiększenia ich reużywalności
- Stworzenie `MainUiState`
- Dodanie filtrów `ALL` i `FAVOURITES` do listy notatek

## Znalezione błędy
- Utrata treści tworzonej notatki przy zmianie orientacji ekranu - naprawione poprzez przeniesienie 
stanu pola tekstowego do ViewModelu
- Brak zmiany koloru tła przy przełączeniu na tryb ciemny - opakowanie istniejącego UI w Scaffold