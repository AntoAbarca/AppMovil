# TecMundo Connect (Ayanad)

Proyecto Semestral - Desarrollo de Aplicaciones Móviles - Fecha de inicio: 21-10-2026

## ▪ ¿Qué es TecMundo Connect?

TecMundo Connect es una app móvil Android, actualmente en desarrollo, hecha con Kotlin como lenguaje base. Su objetivo es conectar a **productores agrícolas** con **aplicadores** de producto (drones o fumigación). Por ahora es una maqueta con datos ficticios.

## ▪ ¿Cómo funciona?

La app tiene tres tipos de cuenta:

- **Productor:** publica solicitudes de aplicación, revisa las ofertas que recibe, acepta una y califica al aplicador.

- **Aplicador:** ve las solicitudes cercanas (con filtro por región) y envía ofertas con su precio por hectárea.

- **Administrador:** ve la lista de usuarios registrados y puede eliminarlos.

Los datos se guardan en el teléfono con Room y la sesión se mantiene al cerrar la app. El perfil permite tomar una foto con la cámara.

## ▪ Futuro del Proyecto

Este proyecto se desarrolla para la asignatura "Desarrollo de Aplicaciones Móviles". Se trabaja por iteraciones y aún quedan por realizar, por lo que la app y este README seguirán cambiando. Este repositorio funciona como registro del trabajo y sus versiones.

---

## Instrucciones de ejecución

### 1) Requisitos

- Android Studio

- Un celular Android (Android 7.0 o superior) o un emulador

- Conexión a internet la primera vez, para descargar las dependencias de Gradle

### 2) Base de datos

No hay que configurar nada: la app usa una base de datos local en el teléfono con Room.

### 3) Ejecución del Proyecto

- Abrir la carpeta raíz del proyecto con Android Studio.

- Esperar a que termine la sincronización de Gradle.

- Conectar el celular (o iniciar un emulador) y presionar Run.

### 4) Cuentas de prueba

- Administrador: `admin@admin.cl` / `admin`

- Productor y Aplicador: se crean desde la pantalla de registro con datos ficticios.

---

## Contribuidores

- Antonia Abarca (AntoAbarca)

- Brian Henríquez (BrianHenriquezRojas)

- Carlos Sepúlveda (carlossepulvedadiaz)

---

## Commit Log (Registro de los Commits + autores, más descriptivo)

<details>
<summary>Haga clic aquí para desplegar Log</summary>

<br>

AntoAbarca - 21/10/2026 - Se agrega el nuevo proyecto y la versión 1 del registro.

carlossepulvedadiaz - 23/10/2026 - Primera versión del login y del menú inicial de usuarios.

AntoAbarca - 23/10/2026 - Versión 2 y 3 del registro, solicitudes del cliente y ofertas del agricultor.

BrianHenriquezRojas - 23/10/2026 - Se agregan la solicitud, `SolicitudRepository`, `SolicitudViewModel` y las pantallas NuevaSolicitud, SolicitudesCercanas y DetalleSolicitudProductor.

AntoAbarca - 25/10/2026 - Se agrega la configuración de la base de datos y la base de datos.

AntoAbarca - 26/10/2026 - Se actualiza la base de datos (v2).

AntoAbarca - 28/10/2026 - Se actualiza el registro.

carlossepulvedadiaz - 29/10/2026 - Se agrega la maqueta de usuario, y se agrega KSP en otra versión con sus librerías.

BrianHenriquezRojas - 29/10/2026 - Se agregan las páginas faltantes y la navegación entre ellas, el flujo completo de la app (sesión, Room y calificaciones), y la administración, la cámara y la barra de home en todas las pantallas.

</details>
