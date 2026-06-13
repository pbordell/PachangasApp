# PachangasApp 🏀

PachangasApp es una aplicación Android diseñada para ayudar a los usuarios a crear y encontrar partidos casuales de baloncesto (pachangas) en cualquier ciudad.

La aplicación integra mapas para visualizar la ubicación de las canchas y los puntos de encuentro, proporcionando una plataforma para que los jugadores locales se conecten y organicen partidos fácilmente.

## Características ✨

- **Mapa Interactivo**: Visualiza canchas de baloncesto y ubicaciones de partidos utilizando OpenStreetMap (osmdroid).
- **Creación de Partidos**: Planifica y organiza partidos de baloncesto en tu ciudad.
- **Marcadores Personalizados**: Capas de mapa especializadas para identificar puntos de juego.

## Tecnologías Utilizadas 🛠️

- **Lenguaje**: Java
- **Framework de UI**: Android XML con Material Design
- **Mapas**: [osmdroid](https://github.com/osmdroid/osmdroid)
- **Navegación**: Componente de Navegación de Jetpack
- **Arquitectura**: ViewBinding habilitado

## Primeros Pasos 🚀

### Requisitos Previos
- Android Studio Flamingo o superior.
- Android SDK 33 (Compile SDK).
- Versión mínima de Android: API 21 (Lollipop).

### Instalación
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/PachangasApp.git
   ```
2. Abre el proyecto en **Android Studio**.
3. Sincroniza el proyecto con los archivos de Gradle.
4. Ejecuta la aplicación en un emulador o dispositivo físico.

## Configuración ⚙️

La aplicación utiliza `osmdroid`. Asegúrate de tener conexión a internet para que los mapas carguen correctamente. Dependiendo del uso, es posible que necesites configurar un "User Agent" en el `MainActivity` (actualmente utiliza la configuración por defecto).
