# Registro de equipos

## Diseño

La entidad `TeamEntity` contiene un identificador autogenerado, el nombre visible
y un nombre normalizado con índice único. Se eliminan espacios sobrantes y se
normalizan las mayúsculas para impedir registros duplicados. El nombre admite
entre 1 y 60 caracteres después de normalizar los espacios.

El equipo admite una imagen opcional mediante el selector de fotos de Android.
El formulario permite previsualizarla, cambiarla y quitarla antes de guardar.
`LocalTeamImageStore` decodifica la imagen en segundo plano, limita su tamaño a
512 píxeles y conserva una copia PNG en el almacenamiento privado. Room guarda
solo el nombre relativo del archivo. Las inserciones rechazadas limpian la copia.
La lista muestra la imagen junto al nombre, o un icono predeterminado.

La base de datos está en versión 2. `MIGRATION_1_2` añade `imageFileName` nullable
sin eliminar equipos. Las pruebas instrumentadas comprueban esta migración y que
la copia reducida sigue disponible después de eliminar la imagen original.

Flujo de uso: Equipos → Agregar equipo → escribir nombre → Guardar → lista
actualizada. Cancelar descarta el formulario. Los errores mantienen el nombre
para que el usuario pueda corregirlo o volver a guardar.

## Capas y flujo de datos

- `data/local/entity`: modelo persistente de Room.
- `data/local/dao`: inserción y consulta observable ordenada por nombre normalizado.
- `data/local/ChampionshipDatabase`: base de datos y esquema versionado.
- `data/repository`: validación, normalización y acceso a los datos.
- `ui/screens/teams/TeamsViewModel`: estado del formulario y operaciones asíncronas.
- `ui/screens/teams/TeamsScreen`: lista, formulario y estados de carga o error.

Escritura: Compose → ViewModel → Repository → DAO → Room.
Lectura: Room → Flow → ViewModel → LiveData → Compose.

`ChampionshipApplication` proporciona una única instancia de la base de datos
y del repositorio mediante inicialización diferida. El ViewModel recibe el
repositorio mediante su fábrica. `SavedStateHandle` conserva el formulario
ante recreación de la actividad; Room conserva los equipos entre sesiones.

La barra inferior mantiene la navegación existente. Esta entrega implementa
creación y lectura de equipos. Edición, eliminación, relaciones con partidos y
tabla de posiciones se podrán incorporar cuando se desarrollen esas funciones.
DataStore se reserva para preferencias; los equipos se almacenan en Room.

## Verificación

`TeamRepositoryTest` comprueba validación, normalización y duplicados.
`TeamDatabaseTest` comprueba persistencia al reabrir Room y el índice único;
requiere un dispositivo o emulador Android.

El esquema exportado se guarda en `app/schemas`. Los cambios futuros en la
estructura de datos deberán incrementar la versión y definir migraciones.
