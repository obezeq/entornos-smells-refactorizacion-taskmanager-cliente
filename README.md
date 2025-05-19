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
1. **Extract Method**:
   - **Ejemplo:** Para crear `esHoy()` desde el bloque `"hoy"` en `obtenerPorFecha`.

2. **Change Signature**:
   - **Ejemplo:** Cambiar `crearEvento(String, String, String)` a `crearEvento(DatosEvento)`.

3. **Safe Delete**:
   - **Ejemplo:** Eliminar el método `verificarSubtareas` sin romper nada.

---

## 4. Pruebas Unitarias por Refactorización

| **Patrón**               | **Clase de Test**              | **Métodos de Test**                                      |  
|--------------------------|--------------------------------|---------------------------------------------------------|  
| Extraer Método           | `ActividadServiceTest`         | `obtenerEventosProgramados cuenta eventos hoy y mañana` |  
| Introducir Objeto        | `ActividadServiceTest`         | `crearEvento con DatosEvento válidos`                   |  
| Simplificar Condicional  | `ActividadServiceTest`         | `cambiarEstadoTarea lanza excepción con subtareas`      |  

---

