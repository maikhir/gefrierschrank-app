# Gefrierschrank-App - Feature Roadmap

## Projektziel
Eine moderne Web-Anwendung zur Verwaltung von Gefrierschrank-Inhalten mit responsivem Design für Laptop, Tablet und Smartphone. Deployment über Docker Container oder k3s Cluster.

## Feature-Status Legende
- ❌ **Geplant** - Feature ist definiert, aber noch nicht implementiert
- 🔄 **In Entwicklung** - Feature wird aktuell entwickelt
- ✅ **Implementiert** - Feature ist vollständig umgesetzt und getestet
- 🚀 **Pre-Release** - Feature ist implementiert und in Beta-Version verfügbar
- 📦 **Released** - Feature ist in einer stabilen Version veröffentlicht

---

## Core Features (Version 1.0)

### Basis-Inventar Management
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Produkt hinzufügen | 🚀 | 1.0 | Neue Artikel zum Gefrierschrank hinzufügen |
| Produkt bearbeiten | 🚀 | 1.0 | Bestehende Artikel editieren |
| Produkt löschen | 🚀 | 1.0 | Artikel aus dem Inventar entfernen |
| Inventar anzeigen | 🚀 | 1.0 | Übersicht aller Gefrierschrank-Inhalte |

### Kategorisierung & Organisation
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Kategorien verwalten | 🚀 | 1.0 | Fleisch, Gemüse, Fertiggerichte, etc. |
| Produkte kategorisieren | 🚀 | 1.0 | Artikel zu Kategorien zuordnen |
| Kategorie-Filter | 🚀 | 1.0 | Inventar nach Kategorien filtern |

### Datums-Management
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Einfrierdatum erfassen | 🚀 | 1.0 | Datum des Einfrierens speichern |
| Haltbarkeitsdatum berechnen | 🚀 | 1.0 | Automatische Berechnung basierend auf Produkttyp |
| Ablaufwarnung | 🚀 | 1.0 | Visuelle Warnung bei ablaufenden Produkten |

### Mengen & Standort
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Mengenangaben | 🚀 | 1.0 | Stückzahl, Gewicht (g/kg), Volumen |
| Standort-Verwaltung | 🚀 | 1.0 | Fächer, Schubladen, Bereiche definieren |
| Standort zuweisen | 🚀 | 1.0 | Produkte zu Standorten zuordnen |

### Responsive Design
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Desktop UI | 🚀 | 1.0 | Optimiert für Laptop/Desktop |
| Tablet UI | 🚀 | 1.0 | Touch-optimiert für Tablets |
| Mobile UI | 🚀 | 1.0 | Smartphone-freundliche Oberfläche |

---

## Advanced Features (Version 1.1+)

### Benachrichtigungen & Warnungen
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Browser-Benachrichtigungen | ❌ | 1.1 | Web Push API für Ablaufwarnungen |
| E-Mail-Benachrichtigungen | ❌ | 1.2 | Optional: E-Mail bei kritischen Abläufen |
| Warnstufen konfigurieren | ❌ | 1.1 | 3/7/14 Tage vor Ablauf |

### Daten-Management
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Daten Backup | ❌ | 1.1 | Vollständige Datensicherung |
| Daten Restore | ❌ | 1.1 | Wiederherstellung aus Backup |
| CSV Export | ❌ | 1.2 | Inventar als CSV exportieren |
| CSV Import | ❌ | 1.2 | Massenimport via CSV |

---

## Optional Features (Version 2.0+)

### Erweiterte Funktionen
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Produkt-Fotos | ❌ | 2.0 | Bilder zu Artikeln hinzufügen |
| Multi-User Support | ❌ | 2.0 | Mehrere Benutzer pro Haushalt |
| Benutzer-Rollen | ❌ | 2.0 | Admin/User Berechtigungen |

### Progressive Web App (PWA)
| Feature | Status | Version | Beschreibung |
|---------|--------|---------|--------------|
| Offline-Modus | ❌ | 2.1 | App funktioniert ohne Internet |
| App-Installation | ❌ | 2.1 | Installation wie native App |
| Service Worker | ❌ | 2.1 | Caching und Background-Updates |

---

## Technische Architektur

### Backend
- **Framework:** Spring Boot 4.0.0-SNAPSHOT
- **Database:** PostgreSQL (Production), H2 (Development)
- **API:** RESTful JSON API
- **Security:** Spring Security
- **Deployment:** Docker Container / k3s Cluster

### Frontend
- **Framework:** Vue.js 3 + TypeScript
- **Styling:** Tailwind CSS + Heroicons
- **State Management:** Pinia
- **Build Tool:** Vite

---

## Current Release Status

### 🚀 v1.0.0-beta.1 (Released)
**Release Date:** 30.05.2025  
**Status:** Pre-Release Beta

**Neue Features:**
- Complete Vue.js 3 frontend implementation
- Real backend-frontend integration
- Advanced development scripts and tooling
- Comprehensive E2E testing with Playwright
- Cross-platform startup/stop scripts

**Verbesserungen:**
- Enhanced UX with better placeholder colors
- Real-time data updates from backend
- Proper loading states and error handling
- Automatic security credential extraction

**Technische Details:**
- Vue 3 + TypeScript + Tailwind CSS + Pinia
- Spring Boot 4.0.0-SNAPSHOT backend
- 36 backend tests + E2E frontend tests
- Docker-ready deployment

**Zugriff:**
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console

---

## Release Notes Template

### Version X.X.X - [Datum]
**Neue Features:**
- Feature 1
- Feature 2

**Verbesserungen:**
- Verbesserung 1
- Verbesserung 2

**Bugfixes:**
- Fix 1
- Fix 2

**Breaking Changes:**
- Änderung 1

---

## Feature Request Process
1. Issue in GitHub Repository erstellen
2. Feature in diese Liste aufnehmen
3. Technische Bewertung durchführen
4. Priorisierung und Versionszuweisung
5. Implementation und Testing
6. Release Notes aktualisieren