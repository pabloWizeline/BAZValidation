# BAZValidation — Code Challenge Project

Android code challenge for interviewing level-3 Android developers.

## Build & test

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest          # unit tests only (no instrumented)
./gradlew testDebugUnitTest --tests "*UserListViewModelTest*"
./gradlew testDebugUnitTest --tests "*UserDetailViewModelTest*"
```

No lint or typecheck commands configured.

## Architecture

```
ui/ (Compose + ViewModel)
  └─> domain/usecase/ (GetUsersUseCase, GetUserUseCase)
       └─> domain/repository/ (UserRepository interface)
            └─> data/repository/ (UserRepositoryImpl — retrofit)
```

- MVI in each ViewModel: `State` (data class), `Intent` (sealed class), `Effect` (sealed class).
- ViewModels inject UseCases, not repositories directly.
- `di/RepositoryModule.kt` binds `UserRepository` interface → `UserRepositoryImpl` via `@Binds`.

## Key dependencies (libs.versions.toml)

| Component | Version |
|---|---|
| AGP | 9.2.1 |
| Kotlin | 2.2.10 (built-in AGP 9) |
| Hilt | 2.59.2 via KSP (no kapt) |
| Compose BOM | 2024.12.01 |
| Retrofit | 2.9.0 |
| Navigation Compose | 2.7.7 |
| Mockito + mockito-kotlin | (tests) |

**AGP 9 quirks:** No `kotlinOptions` block, no `kotlin-android` plugin, no `kotlin-kapt` — use `ksp` instead.

## API

JSONPlaceholder: `https://jsonplaceholder.typicode.com/`
- `GET /users` → `List<User>`
- `GET /users/{id}` → `User`

No auth required.

## Testing

- `StandardTestDispatcher` + `Dispatchers.setMain()` in `@Before`
- Mock UseCases with Mockito (`whenever(mock())`)
- Use `advanceUntilIdle()` to flush coroutines
- Use `viewModel.effect.first { it is SomeEffect }` for one-shot effect assertions
- 10 tests across 2 ViewModels (list + detail)

## Iteración 2 (bugs activos)

Ver `BUGS.md` para el reporte detallado. 4 bugs introducidos en repository, ViewModel y UI.

```sh
./gradlew testDebugUnitTest     # 2 tests fallan (refresh + selectUser)
```

## Modules

Single `:app` module. No multi-module yet — intended for the interview challenge.

## Second iteration

After the candidate works on the clean project, focused bugs will be introduced for evaluation.
