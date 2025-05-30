import { test, expect } from '@playwright/test';

test.describe('Dashboard - Inventar anzeigen', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('zeigt Dashboard mit App-Title und Logo', async ({ page }) => {
    // App Title und Logo prüfen
    await expect(page).toHaveTitle(/Gefrierschrank/);
    await expect(page.locator('h1')).toContainText('Gefrierschrank-App');
    await expect(page.locator('text=🧊')).toBeVisible();
  });

  test('zeigt Produktstatistiken an', async ({ page }) => {
    // Statistikkarten prüfen
    await expect(page.locator('text=Frische Produkte')).toBeVisible();
    await expect(page.locator('text=Läuft bald ab')).toBeVisible();
    await expect(page.locator('text=Abgelaufen')).toBeVisible();
    
    // Zahlen sollten sichtbar sein (mindestens 0)
    const freshCount = page.locator('text=Frische Produkte').locator('..').locator('.text-2xl');
    await expect(freshCount).toBeVisible();
  });

  test('zeigt Produktkarten mit korrekten Informationen', async ({ page }) => {
    // Mindestens eine Produktkarte sollte sichtbar sein (Mockdaten)
    const productCards = page.locator('.card').filter({ hasText: 'Rindfleisch' });
    await expect(productCards.first()).toBeVisible();
    
    // Produktkarte sollte wichtige Informationen enthalten
    await expect(productCards.first()).toContainText('Fleisch'); // Kategorie
    await expect(productCards.first()).toContainText('2.5 kg'); // Menge
    await expect(productCards.first()).toContainText('Fach 1'); // Standort
  });

  test('zeigt Status-Badges für Produkte', async ({ page }) => {
    // Status Badge sollte sichtbar sein
    const statusBadges = page.locator('.badge-fresh, .badge-expiring, .badge-critical, .badge-expired');
    await expect(statusBadges.first()).toBeVisible();
  });

  test('zeigt Sidebar mit Kategorien und Standorten', async ({ page }, testInfo) => {
    // Skip auf Mobile da Sidebar versteckt ist
    if (testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    // Sidebar sollte sichtbar sein
    await expect(page.locator('aside')).toBeVisible();
    
    // Kategorien Section
    await expect(page.locator('text=Kategorien')).toBeVisible();
    await expect(page.locator('text=Fleisch')).toBeVisible();
    await expect(page.locator('text=Gemüse')).toBeVisible();
    
    // Standorte Section  
    await expect(page.locator('text=Standorte')).toBeVisible();
    await expect(page.locator('text=Fach 1')).toBeVisible();
    await expect(page.locator('text=Fach 2')).toBeVisible();
  });

  test('zeigt Mobile Menu auf kleinen Bildschirmen', async ({ page }, testInfo) => {
    // Nur auf Mobile testen
    if (!testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    // Mobile Menu sollte am Bottom sichtbar sein
    await expect(page.locator('.fixed.bottom-0')).toBeVisible();
    await expect(page.locator('text=Home')).toBeVisible();
    await expect(page.locator('text=Filter')).toBeVisible();
    await expect(page.locator('text=Hinzufügen')).toBeVisible();
  });
});

test.describe('Filter und Suche Funktionalität', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('kann Produkte nach Status filtern', async ({ page }) => {
    // Filter Bar sollte sichtbar sein
    await expect(page.locator('.card').filter({ hasText: 'Alle' })).toBeVisible();
    
    // Auf "Läuft ab" Filter klicken
    await page.locator('button').filter({ hasText: 'Läuft ab' }).click();
    
    // Button sollte als aktiv markiert sein (primary style)
    await expect(page.locator('button').filter({ hasText: 'Läuft ab' })).toHaveClass(/btn-primary/);
  });

  test('kann Produkte durchsuchen', async ({ page }) => {
    // Suchfeld sollte sichtbar sein
    const searchInput = page.locator('input[placeholder*="suchen"]');
    await expect(searchInput).toBeVisible();
    
    // Nach "Rindfleisch" suchen
    await searchInput.fill('Rindfleisch');
    
    // Nur Rindfleisch-Karte sollte sichtbar sein
    await expect(page.locator('.card').filter({ hasText: 'Rindfleisch' })).toBeVisible();
    
    // Andere Produkte sollten gefiltert werden
    const allCards = page.locator('.card').filter({ hasText: /Brokkoli|Pizza/ });
    await expect(allCards).toHaveCount(0);
  });

  test('kann Sortierung ändern', async ({ page }) => {
    // Sortier-Dropdown sollte sichtbar sein
    const sortSelect = page.locator('select').last(); // Letztes select Element
    await expect(sortSelect).toBeVisible();
    
    // Nach Ablaufdatum sortieren
    await sortSelect.selectOption('expiration');
    await expect(sortSelect).toHaveValue('expiration');
  });
});

test.describe('Responsive Design Validierung', () => {
  test('Desktop Layout (≥1024px)', async ({ page }, testInfo) => {
    if (testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    await page.goto('/');
    
    // Desktop spezifische Elemente
    await expect(page.locator('nav').filter({ hasText: 'Dashboard' })).toBeVisible(); // Header Navigation
    await expect(page.locator('aside')).toBeVisible(); // Sidebar
    await expect(page.locator('.grid')).toHaveClass(/lg:grid-cols-3/); // Grid Layout
  });

  test('Mobile Layout (≤767px)', async ({ page }, testInfo) => {
    if (!testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    await page.goto('/');
    
    // Mobile spezifische Elemente
    await expect(page.locator('aside')).not.toBeVisible(); // Sidebar versteckt
    await expect(page.locator('.fixed.bottom-0')).toBeVisible(); // Mobile Menu
    await expect(page.locator('.grid')).toHaveClass(/grid-cols-1/); // Single Column
  });
});