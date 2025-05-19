# Actividad: Aplicación de Code Smells y Patrones de Refactorización en el Código del Task Manager

- Rama: `P4.3.2-EOB`

---

## 1. Code Smells Detectados y Patrones Aplicados  

### **1.a Code Smells y Patrones de Refactorización**  
Durante la revisión del código, identifiqué los siguientes code smells y apliqué estos patrones:  

1. **Código Duplicado** (Dispensable)  
   - **Ejemplo:** La lógica para parsear fechas de eventos estaba repetida en `obtenerPorFecha` y `obtenerEventosProgramados`.  
   - **Patrón:** *Extraer Método* => Creé `parsearFechaEvento()`.  
   - **Commit:** [07b240b](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/07b240b5490132fdf827da5e3ffecff024f157c6)  

2. **Método Largo** (Bloaters)  
   - **Ejemplo:** `obtenerPorFecha` tenía un `when` gigante con lógica compleja.  
   - **Patrón:** *Dividir en Métodos Pequeños* => Extraje `esHoy()`, `esManana()`, etc.  
   - **Commit:** [ecbd86c](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/ecbd86c9238c81d54959c11161b116fc638db7c0)  

3. **Primitive Obsession** (Acoplamiento Inapropiado)  
   - **Ejemplo:** `crearEvento()` recibía 3 strings (`descripcion`, `fecha`, `ubicacion`).  
   - **Patrón:** *Introducir Objeto Parámetro* => Clase `DatosEvento`.  
   - **Commit:** [9afd6a2](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/9afd6a2428196304f8867f82bc94883d90b8fd86)  

4. **Código Muerto** (Dispensable)  
   - **Ejemplo:** Método `verificarSubtareas` comentado y sin uso.  
   - **Patrón:** *Eliminar Código Muerto* => Lo borré.  
   - **Commit:** [c2f7e62](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/c2f7e62ef81aaacb66bee332e97f8080a0ca8093)  

5. **Condicional Complejo** (Legibilidad)  
   - **Ejemplo:** El `when` en `cambiarEstadoTarea` mezclaba lógica y validaciones.  
   - **Patrón:** *Extraer Método* => `obtenerEstadoDesdeOpcion()`.  
   - **Commit:** [8bdac3c](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/8bdac3c16a1c494f580fe533d8a141423d120f4b)  

Durante la refactorización de todo el código de esta practica use las funcionalidades que te trae el IDE IntelliJ IDEA Ultimate.

En concreto he utilizado la funcionalidad "Refactor", el cual para utilizarla hay que seleccionar una porción de código que queramos refactorizar, hacer click derecho y posteriormente hacer click en "Refactor", después si lo que queremos es refactorizar una función hacemos click en "Function".

Ahora lo que nos queda es refactorizar y darle el nombre a la función o lo que queramos refactorizar. Una vez refactorizado, podemos perfeccionar el método sin problemas.

*(Pongo ejemplos y capturas de pantalla mas adelante)*

---

### **1.b Mejora con Pruebas Unitarias**  
**Patrón Seleccionado:** *Extraer Método* en `cambiarEstadoTarea`.  

**¿Por qué mejora el código?**  
- **Antes:** El método tenía un `when` que hacía dos cosas: mapear opciones *y* validar subtareas.  
  ```kotlin
  // Código original (confuso)
  val estado = when (opcion) {
      3 -> { if (!tarea.puedeCerrarse()) throw... }
      else -> ...
  }
  ```  
- **Después:** Separé responsabilidades:
  ```kotlin
  // Código refactorizado (claro)
  val estado = obtenerEstadoDesdeOpcion(opcion) // Solo mapea
  if (estado == FINALIZADA && !tarea.puedeCerrarse()) throw... // Valida
  ```  
**Tests que lo cubren:**
- `ActividadServiceTest.kt` => `Debería lanzar IllegalStateException al cerrar tarea con subtareas`.
- **Enlace al código:** https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/blob/2a8a99e71d390d02adeb4b15e00de73dfcbebf10/src/test/kotlin/ActividadServiceTest.kt#L85-L89

---

## 2. Proceso de Verificación Post-Refactor

### **2.a Garantía de Calidad**
Para asegurarme de que la refactorización no rompía nada:

1. **Ejecuté TODAS las pruebas antes de cambiar nada** (`gradlew test`).
2. **Usé el menú Refactor de IntelliJ** para cambios seguros (ej: *Extract Method* mantiene referencias).
3. **Modifiqué pruebas existentes** donde era necesario (ej: `crearEvento` ahora usa `DatosEvento`).
4. **Añadí tests nuevos** para métodos extraídos (ej: `esHoy()`).
5. **Re-ejecuté pruebas después de cada cambio**.

---

## 3. Uso del IDE para Refactorizar

### **3.a Funcionalidades de IntelliJ Usadas**

---

1. **Extract Method**:
   - **Ejemplo:** Para crear `esHoy()` desde el bloque `"hoy"` en `obtenerPorFecha`.
   - Ejecuto la opción del IDE `Refactor > Function` del IDE como hemos mencionado previamente.
      - ![Refactor Option](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactor-option.png)
      - ![Refactor Option](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactor-function-option.png)
   - Ahora aceptamos las consecuencias, pues no habría ningún conflicto como tal que nos afecte, solamente estamos refactorizando, y clickamos en `Refactor Anyway`
      - ![Refactoring Conflicts esHoy](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactoring-conflicts-es-hoy.png)
   - Ahora refactorizamos el método según nuestras necesidades y elegimos el nombre.
      - ![Refactoring esHoy](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactoring-es-hoy.png)
   - Después, una vez refactorizado, modifico el método para afinarlo mas a mis necesidades modificando un poco la función y ya daría por terminada la refactorización de este método.
      - ![Refactoring Updated esHoy](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactoring-updated-es-hoy.png)
   - He seguido este procedimiento con todos los métodos para `obtenerPorFecha` así como: `esHoy`, `esManana`, `esEstaSemana` y `esEsteMes`.

---

2. **Change Signature**:
   - **Ejemplo:** Cambiar `crearEvento(String, String, String)` a `crearEvento(DatosEvento)`.
   - Primero selecciono los parámetros
     - ![Selecting Parameters](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/selecting-crearEvento-parameters.png)
   - Ahora ejecuto la opción **Refactor > Change Signature**
     - ![Refactor Option](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactor-option.png)
     - ![Refactor Change Signature Option](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/refactor-change-signature-option.png)
   - Y a partir de aquí elegimos la opción que nos sea mas conveniente para nuestro caso.
     - ![Refactor Changing Signature](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/crearEvento-changing-signature.png)

---

3. **Safe Delete**:
   - **Ejemplo:** Eliminar el método `verificarSubtareas` sin romper nada.
   - **Commit:** [c2f7e62](https://github.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/commit/c2f7e62ef81aaacb66bee332e97f8080a0ca8093)

---

## 4. Pruebas Unitarias por Refactorización

| **Patrón**               | **Clase de Test**              | **Métodos de Test**                                      |  
|--------------------------|--------------------------------|---------------------------------------------------------|  
| Extraer Método           | `ActividadServiceTest`         | `obtenerEventosProgramados cuenta eventos hoy y mañana` |  
| Introducir Objeto        | `ActividadServiceTest`         | `crearEvento con DatosEvento válidos`                   |  
| Simplificar Condicional  | `ActividadServiceTest`         | `cambiarEstadoTarea lanza excepción con subtareas`      |  

- **Para inicializar los tests ejecuto con `gradlew test`**
![TESTS Initializing](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/tests-initializing.png)
![TESTS Executing](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/tests-executing.png)
![TESTS Successful](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/tests-successful.png)
- **Adjunto el documento HTML generado por Gradle**, el cual se encuentra en `build/reports/tests/test/index.html` el cual muestra de una forma muy visual que todos los tests han tenido éxito *satisfactoriamente*.
![TESTS HTML](https://raw.githubusercontent.com/obezeq/entornos-smells-refactorizacion-taskmanager-cliente/refs/heads/P4.3.2-EOB/img/tests-html.png)

---

