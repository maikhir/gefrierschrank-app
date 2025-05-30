# Test info

- Name: Dashboard - Inventar anzeigen >> zeigt Dashboard mit App-Title und Logo
- Location: /Users/maik.hirthe/Documents/Dev/gefrierschrank-app/frontend/tests/e2e/dashboard.spec.ts:8:3

# Error details

```
Error: expect.toContainText: Error: strict mode violation: locator('h1') resolved to 2 elements:
    1) <h1 class="text-xl font-bold text-secondary-900 hidden sm:block"> Gefrierschrank-App </h1> aka getByRole('heading', { name: 'Gefrierschrank-App' })
    2) <h1 class="text-2xl font-bold text-secondary-900 mb-2"> Gefrierschrank Übersicht </h1> aka getByRole('heading', { name: 'Gefrierschrank Übersicht' })

Call log:
  - expect.toContainText with timeout 5000ms
  - waiting for locator('h1')

    at /Users/maik.hirthe/Documents/Dev/gefrierschrank-app/frontend/tests/e2e/dashboard.spec.ts:11:38
```

# Page snapshot

```yaml
- banner:
  - text: 🧊
  - heading "Gefrierschrank-App" [level=1]
  - navigation:
    - link "Dashboard":
      - /url: /
    - link "Kategorien":
      - /url: /categories
    - link "Standorte":
      - /url: /locations
  - textbox "Produkte suchen..."
  - button "Hinzufügen"
  - button
- complementary:
  - heading "Kategorien" [level=2]
  - navigation:
    - button "Fleisch 5"
    - button "Gemüse 8"
    - button "Fertiggerichte 3"
    - button "Brot 2"
  - heading "Standorte" [level=2]
  - navigation:
    - button "Fach 1 7"
    - button "Fach 2 6"
    - button "Schublade 5"
  - heading "Schnellfilter" [level=2]
  - navigation:
    - button "Läuft bald ab 3"
    - button "Abgelaufen 1"
- main:
  - heading "Gefrierschrank Übersicht" [level=1]
  - paragraph: 3 Produkte insgesamt • 1 laufen bald ab
  - paragraph: Frische Produkte
  - paragraph: "2"
  - paragraph: Läuft bald ab
  - paragraph: "1"
  - paragraph: Abgelaufen
  - paragraph: "0"
  - button "Alle"
  - button "Läuft ab"
  - button "Abgelaufen"
  - textbox "Produkte suchen..."
  - combobox:
    - option "Name" [selected]
    - option "Ablaufdatum"
    - option "Kategorie"
    - option "Standort"
  - text: Frisch
  - button
  - heading "Brokkoli" [level=3]
  - paragraph: Gemüse
  - text: "Eingefroren: 28.05.2025 Haltbar bis: 28.12.2025 500 g Fach 2 6 Tage"
  - button
  - heading "Pizza Margherita" [level=3]
  - paragraph: Fertiggerichte
  - text: "Eingefroren: 20.05.2025 Haltbar bis: 05.06.2025 2 stück Fach 1 TK-Pizza Frisch"
  - button
  - heading "Rindfleisch" [level=3]
  - paragraph: Fleisch
  - text: "Eingefroren: 25.05.2025 Haltbar bis: 25.05.2026 2.5 kg Fach 1 Bio-Qualität"
- img
- img
```

# Test source

```ts
   1 | import { test, expect } from '@playwright/test';
   2 |
   3 | test.describe('Dashboard - Inventar anzeigen', () => {
   4 |   test.beforeEach(async ({ page }) => {
   5 |     await page.goto('/');
   6 |   });
   7 |
   8 |   test('zeigt Dashboard mit App-Title und Logo', async ({ page }) => {
   9 |     // App Title und Logo prüfen
   10 |     await expect(page).toHaveTitle(/Gefrierschrank/);
>  11 |     await expect(page.locator('h1')).toContainText('Gefrierschrank-App');
      |                                      ^ Error: expect.toContainText: Error: strict mode violation: locator('h1') resolved to 2 elements:
   12 |     await expect(page.locator('text=🧊')).toBeVisible();
   13 |   });
   14 |
   15 |   test('zeigt Produktstatistiken an', async ({ page }) => {
   16 |     // Statistikkarten prüfen
   17 |     await expect(page.locator('text=Frische Produkte')).toBeVisible();
   18 |     await expect(page.locator('text=Läuft bald ab')).toBeVisible();
   19 |     await expect(page.locator('text=Abgelaufen')).toBeVisible();
   20 |     
   21 |     // Zahlen sollten sichtbar sein (mindestens 0)
   22 |     const freshCount = page.locator('text=Frische Produkte').locator('..').locator('.text-2xl');
   23 |     await expect(freshCount).toBeVisible();
   24 |   });
   25 |
   26 |   test('zeigt Produktkarten mit korrekten Informationen', async ({ page }) => {
   27 |     // Mindestens eine Produktkarte sollte sichtbar sein (Mockdaten)
   28 |     const productCards = page.locator('.card').filter({ hasText: 'Rindfleisch' });
   29 |     await expect(productCards.first()).toBeVisible();
   30 |     
   31 |     // Produktkarte sollte wichtige Informationen enthalten
   32 |     await expect(productCards.first()).toContainText('Fleisch'); // Kategorie
   33 |     await expect(productCards.first()).toContainText('2.5 kg'); // Menge
   34 |     await expect(productCards.first()).toContainText('Fach 1'); // Standort
   35 |   });
   36 |
   37 |   test('zeigt Status-Badges für Produkte', async ({ page }) => {
   38 |     // Status Badge sollte sichtbar sein
   39 |     const statusBadges = page.locator('.badge-fresh, .badge-expiring, .badge-critical, .badge-expired');
   40 |     await expect(statusBadges.first()).toBeVisible();
   41 |   });
   42 |
   43 |   test('zeigt Sidebar mit Kategorien und Standorten', async ({ page }, testInfo) => {
   44 |     // Skip auf Mobile da Sidebar versteckt ist
   45 |     if (testInfo.project.name.includes('Mobile')) {
   46 |       test.skip();
   47 |     }
   48 |     
   49 |     // Sidebar sollte sichtbar sein
   50 |     await expect(page.locator('aside')).toBeVisible();
   51 |     
   52 |     // Kategorien Section
   53 |     await expect(page.locator('text=Kategorien')).toBeVisible();
   54 |     await expect(page.locator('text=Fleisch')).toBeVisible();
   55 |     await expect(page.locator('text=Gemüse')).toBeVisible();
   56 |     
   57 |     // Standorte Section  
   58 |     await expect(page.locator('text=Standorte')).toBeVisible();
   59 |     await expect(page.locator('text=Fach 1')).toBeVisible();
   60 |     await expect(page.locator('text=Fach 2')).toBeVisible();
   61 |   });
   62 |
   63 |   test('zeigt Mobile Menu auf kleinen Bildschirmen', async ({ page }, testInfo) => {
   64 |     // Nur auf Mobile testen
   65 |     if (!testInfo.project.name.includes('Mobile')) {
   66 |       test.skip();
   67 |     }
   68 |     
   69 |     // Mobile Menu sollte am Bottom sichtbar sein
   70 |     await expect(page.locator('.fixed.bottom-0')).toBeVisible();
   71 |     await expect(page.locator('text=Home')).toBeVisible();
   72 |     await expect(page.locator('text=Filter')).toBeVisible();
   73 |     await expect(page.locator('text=Hinzufügen')).toBeVisible();
   74 |   });
   75 | });
   76 |
   77 | test.describe('Filter und Suche Funktionalität', () => {
   78 |   test.beforeEach(async ({ page }) => {
   79 |     await page.goto('/');
   80 |   });
   81 |
   82 |   test('kann Produkte nach Status filtern', async ({ page }) => {
   83 |     // Filter Bar sollte sichtbar sein
   84 |     await expect(page.locator('.card').filter({ hasText: 'Alle' })).toBeVisible();
   85 |     
   86 |     // Auf "Läuft ab" Filter klicken
   87 |     await page.locator('button').filter({ hasText: 'Läuft ab' }).click();
   88 |     
   89 |     // Button sollte als aktiv markiert sein (primary style)
   90 |     await expect(page.locator('button').filter({ hasText: 'Läuft ab' })).toHaveClass(/btn-primary/);
   91 |   });
   92 |
   93 |   test('kann Produkte durchsuchen', async ({ page }) => {
   94 |     // Suchfeld sollte sichtbar sein
   95 |     const searchInput = page.locator('input[placeholder*="suchen"]');
   96 |     await expect(searchInput).toBeVisible();
   97 |     
   98 |     // Nach "Rindfleisch" suchen
   99 |     await searchInput.fill('Rindfleisch');
  100 |     
  101 |     // Nur Rindfleisch-Karte sollte sichtbar sein
  102 |     await expect(page.locator('.card').filter({ hasText: 'Rindfleisch' })).toBeVisible();
  103 |     
  104 |     // Andere Produkte sollten gefiltert werden
  105 |     const allCards = page.locator('.card').filter({ hasText: /Brokkoli|Pizza/ });
  106 |     await expect(allCards).toHaveCount(0);
  107 |   });
  108 |
  109 |   test('kann Sortierung ändern', async ({ page }) => {
  110 |     // Sortier-Dropdown sollte sichtbar sein
  111 |     const sortSelect = page.locator('select').last(); // Letztes select Element
```