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
- **Base de datos**: Firebase Realtime Database

## Primeros Pasos 🚀

### Requisitos Previos
- Android Studio Flamingo o superior.
- Android SDK 33 (Compile SDK).
- Versión mínima de Android: API 21 (Lollipop).

## Configuración de Firebase 🚀

1.  Crea proyecto en Firebase y añade `google-services.json` en `/app/`.
2.  Configura las Reglas de Indexación en Firebase:
    ```json
    {
      "rules": {
        "matches": {
          ".indexOn": ["latitude", "longitude"]
        }
      }
    }

## Instalación
1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/PachangasApp.git
   ```
2. Abre el proyecto en **Android Studio**.
3. Sincroniza el proyecto con los archivos de Gradle.
4. Ejecuta la aplicación en un emulador o dispositivo físico.
