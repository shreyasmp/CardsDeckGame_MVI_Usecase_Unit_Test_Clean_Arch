# Card Deck Game

An Android app that uses the [Deck of Cards API](https://deckofcardsapi.com/) to shuffle, draw, and inspect cards with a polished UI, offline support, and clear architecture.

---

## Architecture

The app is built around **MVI (Model–View–Intent)** with a **use-case layer** and **modular** structure so the deck feature can be reused in other card games.

### MVI (Model–View–Intent)

Data flows in one direction: **View** sends **Intents** → **ViewModel** calls **Use cases** → **ViewModel** updates **State** → **View** renders **State**.

```
┌─────────────┐   Intents    ┌─────────────┐   StateFlows   ┌─────────────┐
│   (View)    │ ───────────► │ ViewModel   │ ─────────────► │   (View)    │
│ DeckScreen │               │ (thin)      │                │ DeckScreen  │
└─────────────┘               └──────┬─────┘                └─────────────┘
                                      │
                                      │ calls
                                      ▼
                               ┌─────────────┐
                               │ Use cases   │  ShuffleDeck, DrawCards,
                               │ (business   │  ReshuffleDeck, LoadLastDrawnCards
                               │  logic)     │
                               └──────┬──────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │ Repository  │  (API + Room)
                               └─────────────┘
```

- **Intent** – A single user or system action (e.g. `ShuffleDeck`, `DrawCards(1)`, `CardClicked(card)`). The UI only calls `viewModel.handle(intent)`.
- **State** – One in-memory `DeckState` in the ViewModel, but the UI subscribes to **split flows** so only the right composables recompose:
  - **`deckUiState`** – `deckId`, `remaining`, `isLoading`, `errorMessage` (deck area + error snackbar).
  - **`drawnCards`** – list of drawn cards (card grid only).
  - **`selectedCard`** – selected card for the detail dialog (dialog only).
- **ViewModel** – Thin: only dispatches intents to use cases and maps their results to state. No business rules (e.g. “need deck to draw”, “append cards”) — those live in use cases. Easy to unit test by mocking use cases.
- **Use cases** – Encapsulate business rules and call the repository. E.g. `DrawCardsUseCase` enforces “deck required”, calls `repository.draw()`, and returns an outcome with the full updated list (existing + new cards). `ReshuffleDeckUseCase` returns an outcome that clears drawn cards.

This keeps the UI dumb, business logic testable in use cases, and state changes predictable. **Recomposition** is limited: when you draw a card, only the deck area (remaining count) and the card list recompose; the top bar and static labels do not.

### Module layout

| Module | Role |
|--------|------|
| **`:app`** | Entry point: `MainActivity`, Compose theme, NavHost, Koin setup. Depends on core + feature. |
| **`:core:common`** | Shared types (e.g. `Result<T>` for success/error/loading). No Android UI. |
| **`:core:network`** | Deck of Cards API: Retrofit interface, DTOs, OkHttp/Retrofit creation. No Koin. |
| **`:core:database`** | Room: `GameSessionEntity`, `DrawnCardEntity`, DAOs, `AppDatabase`. No Koin. |
| **`:feature:deck`** | Deck feature: domain (models + **use cases**), data (Repository), presentation (MVI + DeckScreen with **split state**). Koin module. |

The **app** provides `DeckOfCardsApi`, `GameSessionDao`, `DrawnCardDao`, and Room in Koin. The **feature** declares `DeckRepository`, the four use cases, and `DeckViewModel(use cases)`. The feature exposes `DeckFeature.koinModule` and `DeckFeature.DeckRoute()` so another app can add the module and show the same deck UI.

### Dependency injection (Koin)

- **App module** – OkHttp, Retrofit, `DeckOfCardsApi`, Room `AppDatabase`, `GameSessionDao`, `DrawnCardDao`.
- **Deck feature module** – `DeckRepository(api, gameSessionDao, drawnCardDao)`; `ShuffleDeckUseCase`, `DrawCardsUseCase`, `ReshuffleDeckUseCase`, `LoadLastDrawnCardsUseCase` (each takes repository); `DeckViewModel(shuffleDeck, drawCards, reshuffleDeck, loadLastDrawnCards)`.

The app’s `CardDeckApplication` runs `startKoin { androidContext(this); modules(appModule, DeckFeature.koinModule) }`. The screen uses `koinViewModel()` to get `DeckViewModel`.

### Data flow (single source of truth)

- **Remote** – Deck of Cards API: shuffle, draw, reshuffle. The repository uses **try/catch** (not `runCatching`) and rethrows `CancellationException` so coroutine cancellation is preserved. It maps DTOs to domain models (`Card`, `DeckSession`) and returns `Result<T>`.
- **Local** – Room stores the latest game session and drawn cards. On launch, `LoadLastDrawnCardsUseCase` runs and the ViewModel pre-fills `drawnCards` so the user sees their last session.
- **In-memory** – A single `DeckState` in the ViewModel is the source of truth. The ViewModel also exposes **split flows** (`deckUiState`, `drawnCards`, `selectedCard`) via `.map { ... }.distinctUntilChanged().stateIn(...)` so the UI can subscribe per section and avoid full-screen recomposition.

---

## How the game works

### User flow

1. **Start** – User opens the app and sees the deck screen. If there was a previous session, drawn cards are restored from Room and shown.
2. **Shuffle** – User taps **“Shuffle deck”**. The app calls the API to create a new shuffled deck, stores the session in Room, and updates state with `deckId` and `remaining: 52`. The deck image (back of card) appears and the buttons **“New deck”**, **“Reshuffle”**, and **“Draw”** are shown.
3. **Draw** – User taps **“Draw”** (or the deck image) to draw one card. The API returns a card and the new remaining count. The card is appended to `drawnCards`, saved in Room, and shown in the grid. User can keep drawing until the deck is empty.
4. **Card details** – Tapping a drawn card opens a dialog with the card image and “Value of Suit”. Closing the dialog sends `DismissCardDetail` and clears `selectedCard`.
5. **Reshuffle** – **“Reshuffle”** calls the API to reshuffle the current deck (remaining cards). The use case returns an outcome that clears drawn cards so the UI shows a fresh deck state.
6. **New deck** – **“New deck”** is the same as the first shuffle: a new deck is created and the UI shows the new deck and remaining count. (Drawn cards from the previous deck remain in the list until the app is restarted or you add a “clear” action.)
7. **Errors** – Network or API errors set `errorMessage` in state. The UI shows a snackbar and the user can dismiss it (e.g. via Retry intent).

### API usage

- **Shuffle** – `GET /api/deck/new/shuffle/?deck_count=1` → returns `deck_id`, `remaining: 52`.
- **Draw** – `GET /api/deck/{deck_id}/draw/?count=1` → returns `cards[]` (image URL, value, suit, code) and new `remaining`.
- **Reshuffle** – `GET /api/deck/{deck_id}/shuffle/` → reshuffles remaining cards.
- **Back of card** – Image from `https://deckofcardsapi.com/static/img/back.png`.

All network calls go through `DeckRepository`, which returns `Result<T>` and maps API responses to domain models.

### Persistence (offline support)

- After a shuffle, the app saves a **game session** (e.g. `deckId`, `remaining`) in Room.
- After each draw, the drawn cards are inserted into **drawn_cards** linked to that session.
- On next launch, the ViewModel calls `loadLastDrawnCards()` and, if present, sets `drawnCards` in the initial state so the user sees their last drawn cards even without network.

---

## Tech stack

| Area | Choice |
|------|--------|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVI + use cases (thin ViewModel; business logic in use cases) |
| State / recomposition | Split StateFlows (deckUiState, drawnCards, selectedCard) so only affected composables recompose |
| DI | Koin |
| Network | Retrofit, OkHttp (logging interceptor) |
| Local DB | Room (entities + DAOs) |
| Repository | try/catch, rethrow CancellationException (no runCatching) |
| Images | Coil (Compose) |
| Async | Kotlin Coroutines, Flow |
| Tests | JUnit, MockK, Coroutines test; Compose UI tests |

---

## Project structure (reference)

```
app/                    → MainActivity, theme, Koin, NavHost → DeckFeature.DeckRoute()
core/
  common/               → Result<T>
  network/              → DeckOfCardsApi, DTOs, DeckApiProvider
  database/             → AppDatabase, GameSessionDao, DrawnCardDao, entities
feature/
  deck/
    domain/             → Card, DeckSession, Pile (DeckModels.kt)
    domain/usecase/     → ShuffleDeckUseCase, DrawCardsUseCase, ReshuffleDeckUseCase, LoadLastDrawnCardsUseCase
    data/               → DeckRepository (try/catch, no runCatching)
    presentation/       → DeckIntent, DeckState, DeckUiState
                          DeckViewModel (thin; split flows: deckUiState, drawnCards, selectedCard)
                          DeckScreen → DeckAreaSection, DrawnCardsListSection, SelectedCardDialogSection
    di/                 → deckModule (repository, use cases, viewModel)
                          DeckFeature.koinModule, DeckFeature.DeckRoute()
```

---

## Running the app

1. Open the project in Android Studio and sync Gradle.
2. Run on a device or emulator (minSdk 26).
3. Ensure the device has network access so shuffle and draw work.

---

## Tests

- **Unit tests** (no device):
  ```bash
  ./gradlew :feature:deck:testDebugUnitTest
  ```
- **UI tests** (device/emulator; one test needs network):
  ```bash
  ./gradlew :app:connectedDebugAndroidTest
  ```

---

## API reference

- Base URL: `https://deckofcardsapi.com/`
- Shuffle: `GET /api/deck/new/shuffle/?deck_count=1`
- Draw: `GET /api/deck/{deck_id}/draw/?count=1`
- Reshuffle: `GET /api/deck/{deck_id}/shuffle/`
- Back of card image: `https://deckofcardsapi.com/static/img/back.png`
