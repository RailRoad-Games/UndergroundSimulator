# 🛠️ DeepslateSMP – Developer's Guide (Community Module)

Dieser Leitfaden dient als technische Dokumentation und Entwicklungsrichtlinie für das Modul community/. Er beschreibt die Architektur, die Datenflüsse und die Sicherheitsstandards für Entwickler.
## 🏗️ Architektur & Datenfluss

Das Modul trennt strikt zwischen Web-Inhalten (Registrierungen, Forendaten) und Spieldaten (Minecraft-Server-Stats, Berechtigungen).
```html
                       +-------------------+
                       |   Web-Browser     |
                       +---------+---------+
                                 |
                        HTTP(S)  |  (Session Auth)
                                 v
                       +---------+---------+
                       |  community/       |
                       +----+----+----+----+
                            |    |    |
       +--------------------+    |    +--------------------+
       |                         |                         |
       v                         v                         v
+------+---------+        +------+---------+        +------+---------+
|  Web-Datenbank |        | Spieler-Modul  |        |  Team-Modul    |
|  (db.php)      |        | (player/)      |        |  (team/)       |
+----------------+        +------+---------+        +----------------+
                                 |
                                 +--------> MC-Datenbank (db_mc.php)
                                            (LuckPerms / Folia Stats)

```
## 1. Datenquellen und Zuständigkeiten

 *  community/db.php (Web-DB): Enthält Tabellen für Web-User, Sessions, Hashes und Passwörter.

 *  community/player/db_mc.php (MC-DB): Read-Only-Verbindung zu den Live-Tabellen des Minecraft-Netzwerks. Hierüber werden UUIDs, LuckPerms-Gruppen und Spielzeiten gezogen.

### 💻 Code-Konventionen & Dateistruktur

Um den Code wartbar zu halten, folgen alle Skripte einer klaren Struktur:
#### Trennung von Logik und UI

Formulare (<pre>register_form.php</pre>) enthalten reines HTML/CSS und senden ihre Daten via <pre>POST</pre> an die dedizierte Verarbeitungslogik (<pre>register_logic.php</pre>).
#### Einbindung von Komponenten

Wiederkehrende Elemente werden global inkludiert. Ein typisches Skript ist wie folgt aufgebaut:
```PHP

<?php
session_start();
require_once __DIR__ . '/db.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}
?>
<!DOCTYPE html>
<html lang="de">
<head>
    <link rel="stylesheet" href="style.css">
</head>
<body>

<?php include_once __DIR__ . '/footer.php'; ?>
</body>
</html>
```
### 🔒 Sicherheitsstandards (Strict Guidelines)

Für alle Entwickler gelten beim Arbeiten an diesem Verzeichnis folgende Sicherheitsvorgaben:
#### 1. SQL-Injections verhindern

Es dürfen keine Variablen direkt in SQL-Strings injiziert werden. Jede Datenbankabfrage muss über Prepared Statements mit PDO oder MySQLi realisiert werden.
```PHP

$stmt = $pdo->prepare('SELECT id, password_hash FROM users WHERE username = ?');
$stmt->execute([$username]);
$user = $stmt->fetch();
```
#### 2. Passwort-Hashing

Passwörter dürfen niemals im Klartext oder mit veralteten Algorithmen (MD5, SHA-1) verarbeitet werden. Es ist zwingend password_hash() mit PASSWORD_DEFAULT oder PASSWORD_ARGON2ID zu nutzen.
#### 3. XSS (Cross-Site Scripting) Schutz

Sämtliche Benutzereingaben oder Daten aus der Minecraft-Datenbank (z. B. Spielernamen, Prefixes) müssen vor der Ausgabe im HTML-Kontext mit htmlspecialchars() maskiert werden.
```PHP

echo '<h3>Willkommen, ' . htmlspecialchars($user['username'], ENT_QUOTES, 'UTF-8') . '</h3>';
```
#### 4. Direct Access Prevention

Dateien, die reine Logik enthalten oder sensible Konfigurationen laden (<pre>db.php</pre>, <pre>register_logic.php</pre>), müssen vor direktem URL-Aufruf geschützt werden, falls sie Struktur- oder Fehlermeldungen preisgeben könnten. Dies geschieht über die Prüfung einer im Root-Verzeichnis definierten Konstante oder die Platzierung außerhalb des public_html-Ordners.
## 📈 Deployment & Testen

 *  Lokale Entwicklung: Änderungen sind vor dem Push auf einem lokalen Webserver (z. B. XAMPP oder Docker-Umgebung mit Debian/Ubuntu) zu testen.

 *  Datenbank-Mocks: Für das Testen des player/-Moduls ist ein lokaler Abzug der LuckPerms-Struktur zu nutzen, um keine unvollständigen Queries auf die Live-Datenbank loszulassen.
felix@deepslate:~/webiste$ 
