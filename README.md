# NutriExpert · Registro y Estimación Nutricional Agrícola en Terreno

App Android para agricultores y asesores agrónomos que digitaliza el registro en terreno: datos del lote, estimación nutricional referencial y foto foliar, **todo sin conexión a internet**.

> ⚠️ **Datos de ejemplo.** Los valores de los cultivos y la fórmula del cálculo son **ficticios** y solo sirven para la demo. No son recomendaciones agronómicas: las tablas y fórmulas reales las debe entregar y validar el cliente.

---

## Funcionalidades del MVP

| Criterio de evaluación | Cómo se cumple |
|---|---|
| **Autenticación** (SQLite + estados) | Registro e inicio de sesión con Room. Contraseñas con hash PBKDF2-SHA256 y sal, nunca en texto plano. Estados de carga y error en `AuthViewModel`. |
| **Navegación** | Splash → Bienvenida → Login / Registro → Home → Registro de terreno / Historial. Después del login, “atrás” no vuelve al formulario. Cierre de sesión. |
| **Home del caso** | Saludo con nombre y perfil (agricultor o asesor), indicador “Sin conexión · N registros pendientes” y últimos registros. |
| **Formularios con validaciones** | Registro de usuario, login y registro de terreno (cultivo, lote, meta de rendimiento t/há, eficiencia de riego %). Acepta coma decimal (`6,5`). |
| **Animaciones** | Botón que cambia de color, loader mientras calcula o guarda, tarjeta de resultado con `AnimatedVisibility`, scroll automático al resultado y animación de la bienvenida. |
| **Recurso nativo** | Cámara del teléfono para la foto foliar, con permiso en tiempo de ejecución y `FileProvider`. |

Además:
- **Funciona offline**: todo se guarda en el teléfono (Room/SQLite). Cada registro queda marcado como *pendiente* de sincronizar.
- **Trazabilidad**: cada registro guarda usuario, cultivo, lote, meta, eficiencia, resultado, foto y fecha.
- **Historial** por usuario, con opción de borrar.

## Flujo de uso

1. Abrir la app → **Registrarme** → nombre, correo, perfil, contraseña y aceptar el consentimiento.
2. En el **Home**, tocar **Nuevo registro de terreno**.
3. Elegir cultivo, ingresar lote, meta de rendimiento y eficiencia de riego. Opcional: **Tomar foto foliar**.
4. **Calcular** → aparece la demanda estimada de N, P₂O₅, K₂O, CaO y MgO (kg/há).
5. **Guardar registro** → vuelve al Home con el registro como *pendiente*.
6. **Ver historial** para revisar o borrar registros.

## Arquitectura

Patrón **MVVM** con Jetpack Compose:

```
UI (Compose)  ──observa StateFlow──▶  ViewModel  ──▶  Room (DAO)  ──▶  SQLite
     │                                    ▲
     └────────── eventos (clics) ─────────┘
```

- **UI**: pantallas Compose que solo dibujan el estado y avisan las acciones.
- **ViewModel**: guarda el estado en un `MutableStateFlow` privado y lo expone como `StateFlow` de solo lectura. Valida formularios y llama a la base de datos.
- **Datos**: Room con dos tablas, `usuarios` y `registros_terreno`.
- **Dominio**: cálculo nutricional en Kotlin puro, con pruebas unitarias.
- **Navegación**: `NavHost` con rutas en `Screen` (sealed class) y eventos emitidos desde `MainViewModel`.

### Estructura del código

```
app/src/main/java/cl/duoc/agronutri/
├── MainActivity.kt          NavHost y conexión entre pantallas
├── data/                    Room: entidades, DAOs, base de datos, hash de contraseñas
├── domain/                  Cálculo nutricional y datos de ejemplo (ficticios)
├── model/                   Estados de las pantallas (UiState)
├── navigation/              Rutas y eventos de navegación
├── ui/                      Pantallas Compose y componentes de marca
└── viewmodels/              ViewModels de auth, cálculo, registros y navegación
```

## Tecnologías

- Kotlin 2.2.10 · Jetpack Compose (BOM 2026.02.01) · Material 3
- Navigation Compose 2.9.0 · Lifecycle ViewModel 2.9.0 · Coroutines 1.10.2
- Room 2.8.5 con KSP
- Android Gradle Plugin 8.12.0 · Gradle 8.13
- `compileSdk` 36 · `minSdk` 26 (Android 8.0) · `targetSdk` 36

## Cómo ejecutar

Requisitos: Android Studio con soporte para AGP 8.12 y un teléfono con Android 8.0 o superior (o un emulador).

```bash
git clone https://github.com/Anys-VS/agronutri-android.git
cd agronutri-android
./gradlew :app:assembleDebug
```

El APK de prueba queda en `app/build/outputs/apk/debug/app-debug.apk`. También se puede abrir el proyecto en Android Studio y presionar **Run**.

Pruebas unitarias del cálculo:

```bash
./gradlew :app:testDebugUnitTest
```

## Privacidad (Ley 19.628 y Ley 21.719)

- Se piden solo los datos mínimos: nombre, correo y perfil.
- La cuenta requiere aceptar explícitamente el consentimiento.
- Los datos quedan **solo en el teléfono**; no se envían a terceros.
- Las contraseñas se guardan con hash y sal (PBKDF2-SHA256).
- Para pruebas y demos se usan **únicamente datos ficticios**.

## Limitaciones y próximos pasos

- **Fórmulas y tablas reales**: reemplazar los datos de ejemplo por los del cliente y validar la fórmula de cálculo.
- **Sincronización**: enviar los registros pendientes a un backend (Retrofit + Spring Boot) cuando haya conexión.
- **Validación del asesor**: que el perfil asesor revise y apruebe registros.
- **Sesión recordada**: mantener la sesión al cerrar la app (DataStore).
- **Módulo 2 de NutriExpert**: dosis sugeridas, distribución fenológica y fertirriego.
- La base de datos usa migración destructiva al cambiar de versión (aceptable en el MVP con datos ficticios). Antes de producción hay que escribir migraciones reales.

## Equipo

- Carolina Flores Flores
- Dennis Alan Fraser Milling
- Ana María Vidal Salva
