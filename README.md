# 🏃‍♂️ ParkourSpeedrun Plugin (Paper 1.13+) JAVA 8

Un plugin completo de parkour y speedrun para servidores PaperMC. Los jugadores inician y finalizan carreras interactuando con NPCs (usando **Citizens**), con cronómetro de bossbar, sistema de checkpoints, récords personales y globales, ítems interactivos, y soporte para PlaceholderAPI.

---

## 🚀 Características

- 🎯 Inicio y fin de parkours mediante **NPCs configurables por ID**.
- ⏱️ Cronómetro en **BossBar** con colores y formato personalizado.
- 🧠 Guarda récords personales por jugador y por parkour.
- 🥇 Sistema de **Top 10 globales** con broadcast y sonido.
- 🎮 Ítems de hotbar interactivos:
  - **Tinte verde/gris** para mostrar/ocultar jugadores.
  - **Cama** para salir del parkour (ejecuta `/salir`).
  - **Placa de oro** para volver al último checkpoint.
- 💾 Configuración YAML para definir múltiples parkours.
- 🧱 **Checkpoint system** basado en placas de presión de oro con cooldown y efectos visuales.
- 📊 Integración con **PlaceholderAPI** para mostrar top global:


---

## 🔁 Flujo del sistema

1. El jugador hace clic derecho sobre un NPC de inicio.
2. Es teletransportado a la posición de inicio (definida en `config.yml`).
3. Se reproduce un conteo de 3 segundos con titles y sonidos.
4. Inicia el cronómetro (BossBar) y recibe los ítems de control.
5. Al pisar una **placa de oro**, se guarda un **checkpoint**.
6. Al llegar al **NPC final**, se:
 - Detiene el tiempo.
 - Guarda récord si es mejor.
 - Teletransporta al final.
 - Ejecuta comandos configurados (`say`, `give`, etc).
 - Muestra broadcast si entra al top 10 o rompe el récord mundial.
7. Si usa la **cama**, el jugador se sale del parkour y vuelve al final sin guardar tiempo.

---

## 📁 Estructura de clases

| Clase                          | Función principal                                 |
|-------------------------------|--------------------------------------------------|
| `SpeedrunPlugin`              | Clase principal del plugin                       |
| `ParkourManager`              | Lógica del sistema de parkours                   |
| `CheckpointManager`           | Gestión de checkpoints por jugador              |
| `PlayerDataManager`           | Manejo de datos YAML por jugador                |
| `NPCListener`                 | Interacción con NPCs Citizens                   |
| `CheckpointListener`          | Detección de placas de presión doradas          |
| `PlayerInteractListener`      | Manejo de ítems: ocultar, salir, checkpoint      |
| `ParkourTopExpansion`         | PlaceholderAPI: top global por parkour          |

---

## 🧩 Dependencias requeridas

- [PaperMC 1.13+](https://papermc.io/)
- [Citizens](https://www.spigotmc.org/resources/citizens.13811/)
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

---

## ⚙️ Ejemplo de configuración

```yaml
parkours:
desierto:
  startNPC: 10
  endNPC: 11
  allowedWorld: world
  startLocation:
    world: world
    x: 100
    y: 65
    z: 100
    yaw: 0
    pitch: 0
  finishLocation:
    world: world
    x: 150
    y: 65
    z: 150
    yaw: 0
    pitch: 0
  endCommands:
    - "say %player% completó el parkour del desierto"
```
