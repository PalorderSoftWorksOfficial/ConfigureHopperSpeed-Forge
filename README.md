# HopperMod — Fabric 1.21.10

Ändert Hopper-Geschwindigkeit und Item-Menge pro Transfer per Befehl.
Kein OP-Level nötig — funktioniert direkt in Singleplayer!

---

## Befehle

| Befehl | Beschreibung |
|---|---|
| `/hopperspeed` | Zeigt aktuelle Einstellung |
| `/hopperspeed <1–1200>` | Ticks zwischen Transfers (Vanilla = 8, niedriger = schneller) |
| `/hopperamount` | Zeigt aktuelle Einstellung |
| `/hopperamount <1–64>` | Items pro Transfer (Vanilla = 1) |

### Beispiele

```
/hopperspeed 1          → Hopper transferiert jeden Tick (max. Speed)
/hopperspeed 4          → Doppelt so schnell wie Vanilla
/hopperspeed 20         → Einmal pro Sekunde
/hopperamount 8         → 8 Items pro Transfer statt 1
/hopperspeed 1 + /hopperamount 64  → Ultra-schnelle Hopper
```

---

## Build-Anleitung

**Voraussetzungen:** Java 21+, Internetverbindung

```bash
# Linux / Mac:
chmod +x gradlew
./gradlew build

# Windows:
gradlew.bat build
```

Die fertige JAR liegt danach in: `build/libs/hoppermod-1.0.0.jar`

→ Diese JAR in den `.minecraft/mods/` Ordner kopieren.
→ Außerdem Fabric API für 1.21.10 in den mods-Ordner!

---

## Versions-Info

| Komponente | Version |
|---|---|
| Minecraft | 1.21.10 |
| Fabric Loader | 0.17.3 |
| Fabric API | 0.138.0+1.21.10 |
| Yarn Mappings | 1.21.10+build.2 |
| Loom | 1.11-SNAPSHOT |
| Java | 21 |

---

## Standardwerte anpassen

Wenn du die Standardwerte beim Start ändern willst, editiere in `HopperMod.java`:

```java
public static int hopperSpeed = 8;   // Vanilla-Standard: 8 Ticks
public static int hopperAmount = 1;  // Vanilla-Standard: 1 Item
```

Die Einstellungen werden beim Neustart zurückgesetzt (nicht persistent gespeichert).
