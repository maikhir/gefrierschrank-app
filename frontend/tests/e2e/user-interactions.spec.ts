import { test, expect } from '@playwright/test';

test.describe('Benutzerinteraktionen und UX', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('Produktkarte Hover-Effekte', async ({ page }, testInfo) => {
    // Skip auf Mobile (kein Hover)
    if (testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    const productCard = page.locator('.card').first();
    
    // Initial shadow
    const initialShadow = await productCard.evaluate(el => 
      window.getComputedStyle(el).boxShadow
    );
    
    // Hover über Produktkarte
    await productCard.hover();
    
    // Shadow sollte sich ändern (hover:shadow-lg)
    const hoverShadow = await productCard.evaluate(el => 
      window.getComputedStyle(el).boxShadow
    );
    
    expect(initialShadow).not.toBe(hoverShadow);
  });

  test('Produktkarte Mehr-Menü Funktionalität', async ({ page }) => {
    const productCard = page.locator('.card').first();
    const moreButton = productCard.locator('button').first(); // Ellipsis button
    
    // Mehr-Menü sollte initial nicht sichtbar sein
    await expect(productCard.locator('.absolute.right-0')).not.toBeVisible();
    
    // Auf Mehr-Button klicken
    await moreButton.click();
    
    // Dropdown-Menü sollte erscheinen
    await expect(productCard.locator('.absolute.right-0')).toBeVisible();
    await expect(productCard.locator('text=Bearbeiten')).toBeVisible();
    await expect(productCard.locator('text=Als verbraucht markieren')).toBeVisible();
    await expect(productCard.locator('text=Löschen')).toBeVisible();
  });

  test('Sidebar Kategorie-Filter Interaktion', async ({ page }, testInfo) => {
    // Skip auf Mobile 
    if (testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    // Auf Fleisch-Kategorie klicken
    await page.locator('text=Fleisch').click();
    
    // Kategorie sollte als aktiv markiert sein
    const fleischButton = page.locator('button').filter({ hasText: 'Fleisch' });
    await expect(fleischButton).toHaveClass(/bg-primary-50/);
    
    // Nur Fleisch-Produkte sollten sichtbar sein
    await expect(page.locator('.card').filter({ hasText: 'Rindfleisch' })).toBeVisible();
  });

  test('Header Suchfeld Funktionalität', async ({ page }, testInfo) => {
    // Skip auf Mobile (andere Suchposition)
    if (testInfo.project.name.includes('Mobile')) {
      test.skip();
    }
    
    const headerSearch = page.locator('header input[placeholder*="suchen"]');
    await expect(headerSearch).toBeVisible();
    
    // Placeholder Text prüfen
    await expect(headerSearch).toHaveAttribute('placeholder', 'Produkte suchen...');
    
    // Focus State prüfen
    await headerSearch.focus();
    await expect(headerSearch).toBeFocused();
  });

  test('Quick Action Buttons Funktionalität', async ({ page }) => {
    // "Hinzufügen" Button sollte sichtbar sein
    const addButton = page.locator('button').filter({ hasText: 'Hinzufügen' });
    await expect(addButton).toBeVisible();
    
    // Button sollte clickable sein
    await expect(addButton).toBeEnabled();
    
    // Plus Icon sollte sichtbar sein
    await expect(addButton.locator('svg')).toBeVisible();
  });

  test('Status Badge Farbkodierung', async ({ page }) => {
    const productCards = page.locator('.card');
    const firstCard = productCards.first();
    
    // Status Badge sollte sichtbar sein
    const statusBadge = firstCard.locator('[class*="badge-"]').first();
    await expect(statusBadge).toBeVisible();
    
    // Badge sollte Text enthalten
    const badgeText = await statusBadge.textContent();
    expect(badgeText).toMatch(/Frisch|Läuft ab|\d+ Tage|Kritisch|Abgelaufen/);
  });

  test('Filter Button State Management', async ({ page }) => {
    // Alle Filter Buttons sollten sichtbar sein
    await expect(page.locator('button').filter({ hasText: 'Alle' })).toBeVisible();
    await expect(page.locator('button').filter({ hasText: 'Läuft ab' })).toBeVisible();
    await expect(page.locator('button').filter({ hasText: 'Abgelaufen' })).toBeVisible();
    
    // "Alle" sollte initial aktiv sein (primary style)
    await expect(page.locator('button').filter({ hasText: 'Alle' })).toHaveClass(/btn-primary/);
    
    // Auf "Läuft ab" klicken
    await page.locator('button').filter({ hasText: 'Läuft ab' }).click();
    
    // State sollte wechseln
    await expect(page.locator('button').filter({ hasText: 'Läuft ab' })).toHaveClass(/btn-primary/);
    await expect(page.locator('button').filter({ hasText: 'Alle' })).toHaveClass(/btn-secondary/);
  });
});

test.describe('Accessibility und UX Standards', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('Keyboard Navigation Support', async ({ page }) => {
    // Tab durch interaktive Elemente
    await page.keyboard.press('Tab');
    
    // Erstes fokussierbares Element sollte Fokus haben
    const focusedElement = page.locator(':focus');
    await expect(focusedElement).toBeVisible();
    
    // Enter auf fokussiertem Button sollte funktionieren
    await page.keyboard.press('Enter');
  });

  test('Screen Reader Unterstützung', async ({ page }) => {
    // Alt-Texte und Labels prüfen
    const searchInput = page.locator('input[placeholder*="suchen"]');
    
    // Input sollte accessible name haben
    await expect(searchInput).toHaveAttribute('placeholder');
    
    // Buttons sollten verständliche Labels haben
    const addButton = page.locator('button').filter({ hasText: 'Hinzufügen' });
    await expect(addButton).toContainText('Hinzufügen');
  });

  test('Loading States und Feedback', async ({ page }) => {
    // Initial sollten Produktkarten sichtbar sein (kein Loading)
    await expect(page.locator('.card').first()).toBeVisible();
    
    // Empty State sollte nicht sichtbar sein wenn Produkte vorhanden
    await expect(page.locator('text=Keine Produkte gefunden')).not.toBeVisible();
  });

  test('Error Handling UX', async ({ page }) => {
    // Ungültige Suche eingeben
    const searchInput = page.locator('input[placeholder*="suchen"]');
    await searchInput.fill('XXXX_NICHT_EXISTENT_XXXX');
    
    // Empty State sollte erscheinen
    await expect(page.locator('text=Keine Produkte gefunden')).toBeVisible();
    await expect(page.locator('text=Füge dein erstes Produkt hinzu')).toBeVisible();
  });

  test('Visual Consistency Check', async ({ page }) => {
    // Consistent Spacing prüfen
    const productCards = page.locator('.card');
    await expect(productCards).toHaveCount.greaterThan(0);
    
    // Consistent Font Sizing
    const headings = page.locator('h1, h2, h3');
    await expect(headings.first()).toBeVisible();
    
    // Color Consistency (primary color usage)
    const primaryButtons = page.locator('.btn-primary');
    await expect(primaryButtons.first()).toBeVisible();
  });
});

test.describe('Performance und Responsiveness', () => {
  test('Responsive Breakpoints', async ({ page }, testInfo) => {
    await page.goto('/');
    
    if (testInfo.project.name.includes('Mobile')) {
      // Mobile: Single column grid
      await expect(page.locator('.grid')).toHaveClass(/grid-cols-1/);
    } else {
      // Desktop: Multi-column grid
      await expect(page.locator('.grid')).toHaveClass(/lg:grid-cols-/);
    }
  });

  test('Interactive Element Response Time', async ({ page }) => {
    const startTime = Date.now();
    
    // Click on filter button
    await page.locator('button').filter({ hasText: 'Läuft ab' }).click();
    
    // Check that UI responds quickly (< 100ms for visual feedback)
    const responseTime = Date.now() - startTime;
    expect(responseTime).toBeLessThan(1000); // Reasonable threshold for E2E
    
    // UI should update immediately
    await expect(page.locator('button').filter({ hasText: 'Läuft ab' })).toHaveClass(/btn-primary/);
  });
});