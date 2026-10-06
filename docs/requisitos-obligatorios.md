# Documentación de los requisitos obligatorios

Este documento describe el código existente. «Pendiente» indica que el requisito
todavía no está implementado; un comentario no sustituye su desarrollo.
Los archivos Kotlin incluyen comentarios `//` asociados a estas secciones.

## 8. Diseño de la solución

### 8.1 Entidades

Implementado para equipos en `data/local/entity/TeamEntity.kt`.
No existen entidades de partidos ni de clasificación todavía.

### 8.2 Modelo de datos

Room contiene la tabla `teams`:

| Campo | Tipo Kotlin | Uso |
| --- | --- | --- |
| id | Long | Clave primaria autogenerada |
| name | String | Nombre mostrado al usuario |
| normalizedName | String | Nombre normalizado con índice único |
| imageFileName | String? | Nombre relativo de la imagen opcional |

Las imágenes se guardan en archivos privados, no como datos binarios en Room.
La versión 2 incorpora la imagen mediante `MIGRATION_1_2`, conservando registros.
Todavía no hay relaciones entre tablas.

### 8.3 Flujo de la aplicación

Inicio muestra el total de equipos. Equipos permite abrir el formulario,
escribir un nombre, seleccionar una imagen opcional y guardar. El repositorio
valida el nombre, guarda la imagen y solicita la inserción. Room notifica los
cambios a la lista y al contador. Los errores se muestran en el formulario.

### 8.4 Navegación

`ui/ChampionshipApp.kt` selecciona Inicio, Equipos, Partidos o Tabla según
`TopLevelDestination`. Atrás vuelve a Inicio desde las otras pestañas.
Partidos y Tabla son pantallas iniciales; Partido del día es una tarjeta vacía.

## 9. Desarrollo de la aplicación

### 9.1 Room

Implementado. `ChampionshipApplication` crea la base de datos de forma diferida
y proporciona el repositorio. Room utiliza SQLite para persistir los equipos.

### 9.2 Entity

Implementado mediante `@Entity` y `@PrimaryKey` en `TeamEntity`.

### 9.3 DAO

Implementado en `TeamDao`: define inserción, listado observable y conteo.

### 9.4 Database

Implementado en `ChampionshipDatabase`, versión 2, con esquema exportado en
`app/schemas` y migración desde la versión 1.

### 9.5 CRUD

**Parcial.** Crear y Leer están implementados. Actualizar y Eliminar siguen
pendientes tanto en el DAO/repositorio como en la interfaz. Cambiar o quitar
una imagen antes de guardar modifica el borrador, no un equipo ya registrado.

### 9.6 Consultas

Implementado con `@Query`: listado ordenado por `normalizedName` y `COUNT(*)`
para Inicio. Ambas consultas devuelven `Flow` y reaccionan a cambios en Room.

### 9.7 LiveData

Implementado: `TeamsViewModel` y `HomeViewModel` convierten los flujos del
repositorio mediante `asLiveData()`. Compose los observa con `observeAsState()`.

## 10. Arquitectura

### 10.1 MVVM

Implementado en Equipos e Inicio. La vista está en los composables; los
ViewModels coordinan estados y acciones; el modelo incluye repositorio y Room.

### 10.2 Repository

`TeamRepository` normaliza y valida nombres, coordina archivos e inserciones,
y expone las consultas. Recibe `TeamDao` y `TeamImageStore` por constructor.

### 10.3 ViewModel

`TeamsViewModel` administra el formulario, guardado y errores.
`HomeViewModel` administra carga, error y total de equipos. Sus fábricas
reciben el repositorio, evitando crear bases de datos desde los composables.

### 10.4 Flujo de datos

Escritura: interfaz → ViewModel → Repository → DAO → Room.
Lectura: Room → Flow → Repository → ViewModel → LiveData → Compose.
Imágenes: Repository → TeamImageStore → almacenamiento privado.

## 11. Jetpack Compose

### 11.1 Composables

Implementado con funciones `@Composable`: pantallas, formulario, tarjetas,
barra inferior y `TeamAvatar`, entre otras.

### 11.2 Interfaces

Las interfaces visuales usan Material 3, colores, recursos de texto e iconos.
Los componentes reciben valores y callbacks para comunicar acciones.

### 11.3 Listas

`TeamsScreen` utiliza `LazyColumn` e `items` con el identificador del equipo
como clave estable. Incluye estados de carga, error y lista vacía.

### 11.4 Formularios

`AddTeamDialog` permite introducir el nombre y seleccionar una imagen.
Incluye contador, vista previa, cancelación y mensajes de validación.

### 11.5 Gestión de estado

Se utilizan LiveData, StateFlow, `mutableStateOf`, `SavedStateHandle` y
`rememberSaveable`. El borrador y la pestaña seleccionada se conservan ante
recreaciones; los equipos guardados persisten en Room.

### 11.6 Navegación

Implementada mediante selección de estado en `ChampionshipApp`, con barra
inferior y `BackHandler`. **No se ha integrado Navigation Compose/NavHost**;
si la evaluación exige esa biblioteca concreta, esa integración está pendiente.

## 12. DataStore

**Pendiente.** No existe una implementación ni dependencia de DataStore.
Room, SavedStateHandle y rememberSaveable no cumplen este requisito por sí solos.
Un uso previsto es guardar preferencias de la aplicación, por ejemplo el tema
visual. Para cumplirlo deberá desarrollarse su almacenamiento, acceso y consumo
desde la interfaz; actualmente no se presenta como una función implementada.

## Estado de cumplimiento

La base de Equipos e Inicio cubre Room, Entity, DAO, Database, consultas,
LiveData, MVVM, Repository, ViewModel y los componentes Compose descritos.
Para completar todos los requisitos obligatorios faltan el CRUD completo y
DataStore; Navigation Compose también falta si se exige específicamente.
