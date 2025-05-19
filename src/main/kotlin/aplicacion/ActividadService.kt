package es.prog2425.taskmanager.aplicacion

import es.prog2425.taskmanager.datos.IActividadRepository
import es.prog2425.taskmanager.datos.UserRepository
import es.prog2425.taskmanager.dominio.*
import es.prog2425.taskmanager.utilidades.Logger
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActividadService(
    private val repositorio: IActividadRepository,
    private val userRepository: UserRepository
) {

    fun crearTarea(descripcion: String, etiquetas: List<String> = emptyList()) {
        Logger.debug("Intentando crear tarea: $descripcion")
        try {
            require(descripcion.isNotBlank()) {
                Logger.warn("Intento de crear tarea con descripción vacía")
                "Descripción vacía"
            }

            val tarea = Tarea.creaInstancia(descripcion, null, etiquetas)
            repositorio.agregar(tarea)
            Logger.info("Tarea creada exitosamente | ID:${tarea.id} | Desc:${descripcion.take(15)}...")
        } catch (e: IllegalArgumentException) {
            Logger.error("Error de vazlidación: ${e.message}", e)
            throw e
        } catch (e: Exception) {
            Logger.error("Error inesperado", e)
            throw IllegalStateException("Error interno al crear tarea", e)
        }
    }

    fun asignarTarea(tareaId: Long, usuarioId: Int) {
        Logger.debug("Asignando tarea $tareaId a usuario $usuarioId")
        val tarea = repositorio.buscarPorId(tareaId) as? Tarea
            ?: throw IllegalArgumentException("Tarea no encontrada")
        val usuario = userRepository.buscarPorId(usuarioId)
            ?: throw IllegalArgumentException("Usuario no encontrado")

        tarea.asignarUsuario(usuario)
        Logger.info("Tarea $tareaId asignada a usuario ${usuario.nombre}")
    }

    fun obtenerHistorialTarea(tareaId: Long): List<Tarea.RegistroHistorial> {
        val tarea = repositorio.buscarPorId(tareaId) as? Tarea
            ?: throw IllegalArgumentException("ID no válido")
        return tarea.obtenerHistorial()
    }

    fun filtrarPorTipo(tipo: String): List<Actividad> {
        return repositorio.listar().filter { it::class.simpleName!!.lowercase() == tipo }
    }

    fun filtrarPorEstado(estado: Estado): List<Tarea> {
        return repositorio.listar()
            .filterIsInstance<Tarea>()
            .filter { it.estado == estado }
    }

    fun filtrarPorEtiqueta(etiqueta: String): List<Actividad> {
        return repositorio.listar()
            .filter { it.etiquetas.contains(etiqueta) }
    }

    fun filtrarPorUsuario(usuarioId: Int): List<Tarea> {
        return tareasPorUsuario(usuarioId)
    }

    fun filtrarPorFecha(fecha: String): List<Evento>{
        val hoy = LocalDate.now()
        val eventos = repositorio.listar().filterIsInstance<Evento>()
        return obtenerPorFecha(fecha)
    }

    private fun parsearFechaEvento(evento: Evento): LocalDate? {
        return try {
            LocalDate.parse(evento.fecha, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        } catch (e: Exception) {
            null
        }
    }

    private fun esHoy(evento: Evento, hoy: LocalDate): Boolean {
        val fechaEvento = parsearFechaEvento(evento)
        return fechaEvento?.isEqual(hoy) ?: false
    }

    private fun esManana(evento: Evento, hoy: LocalDate): Boolean {
        val fechaEvento = parsearFechaEvento(evento)
        return fechaEvento?.isEqual(hoy.plusDays(1)) ?: false
    }

    private fun esEstaSemana(evento: Evento, hoy: LocalDate): Boolean {
        val fechaEvento = parsearFechaEvento(evento) ?: return false
        return !fechaEvento.isBefore(hoy) && fechaEvento.isBefore(hoy.plusWeeks(1))
    }

    private fun esEsteMes(evento: Evento, hoy: LocalDate): Boolean {
        val fechaEvento = parsearFechaEvento(evento) ?: return false
        return fechaEvento.month == hoy.month && fechaEvento.year == hoy.year
    }

    private fun obtenerPorFecha(fecha: String): List<Evento> {
        val hoy = LocalDate.now()
        val eventos = repositorio.listar().filterIsInstance<Evento>()
        val lista = mutableListOf<Evento>()

        eventos.forEach { evento ->
            when (fecha) {
                "hoy" -> if (esHoy(evento, hoy)) lista.add(evento)
                "mañana" -> if (esManana(evento, hoy)) lista.add(evento)
                "esta semana" -> if (esEstaSemana(evento, hoy)) lista.add(evento)
                "este mes" -> if (esEsteMes(evento, hoy)) lista.add(evento)
            }
        }
        return lista
    }

    fun crearEvento(datos: DatosEvento) {
        val evento = Evento.creaInstancia(datos.descripcion, datos.fecha, datos.ubicacion)
        repositorio.agregar(evento)
    }

    fun listarActividades(): List<Actividad> = repositorio.listar()

    fun crearSubtarea(parentId: Long, descripcion: String) {
        val parent = repositorio.buscarPorId(parentId) as? Tarea
            ?: throw IllegalArgumentException("ID de tarea padre no válido")
        val subtarea = Tarea.creaInstancia(descripcion, parent)
        repositorio.agregar(subtarea)
        parent.agregarSubtarea(subtarea)
    }

    private fun obtenerEstadoDesdeOpcion(opcion: Int): Estado {
        return when (opcion) {
            1 -> Estado.ABIERTA
            2 -> Estado.EN_PROGRESO
            3 -> Estado.FINALIZADA
            else -> throw IllegalArgumentException("Opción incorrecta")
        }
    }

    fun cambiarEstadoTarea(tareaId: Long, opcionElegida: Int) {
        Logger.info("Cambiando estado tarea:$tareaId | Opción:$opcionElegida")

        val tarea = repositorio.buscarPorId(tareaId) as? Tarea
            ?: throw IllegalArgumentException("ID no corresponde a tarea").also {
                Logger.warn("Intento de cambiar estado en no-tarea: ID$tareaId")
            }

        val estado = obtenerEstadoDesdeOpcion(opcionElegida)

        if (estado == Estado.FINALIZADA && !tarea.puedeCerrarse()) {
            Logger.error("Intento de cerrar tarea $tareaId con subtareas pendientes")
            throw IllegalStateException("Subtareas pendientes | Tarea:$tareaId")
        }

        tarea.cambiarEstado(estado)
    }

    fun obtenerResumenTareas(): ResumenTareas {
        val tareas = repositorio.listar().filterIsInstance<Tarea>()

        return ResumenTareas(
            totalTareas = tareas.size,
            tareasMadre = tareas
                .filter { it.parent == null }
                .associate { it.id to it.subtareas.size.toLong() },
            porEstado = tareas.groupingBy { it.estado }.eachCount()
        )
    }

    fun obtenerEventosProgramados(): ResumenEventos {
        val hoy = LocalDate.now()
        val eventos = repositorio.listar().filterIsInstance<Evento>()

        fun fechaValida(evento: Evento): LocalDate? {
            return try {
                LocalDate.parse(evento.fecha, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            } catch (e: Exception) {
                null
            }
        }

        return ResumenEventos(
            hoy = eventos.count {
                fechaValida(it)?.isEqual(hoy) == true
            },
            manana = eventos.count {
                fechaValida(it)?.isEqual(hoy.plusDays(1)) == true
            },
            estaSemana = eventos.count {
                val fecha = fechaValida(it) ?: return@count false
                !fecha.isBefore(hoy) && fecha.isBefore(hoy.plusWeeks(1))
            },
            esteMes = eventos.count {
                val fecha = fechaValida(it) ?: return@count false
                fecha.month == hoy.month && fecha.year == hoy.year
            }
        )
    }

    // Gestión de usuarios
    fun crearUsuario(nombre: String, email: String): Usuario {
        require(nombre.isNotBlank()) { "Nombre no puede estar vacío" }
        require(email.isNotBlank()) { "Email no puede estar vacío" }
        require(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$").matches(email)) { "Correo incorrecto" }

        val usuario = Usuario(0, nombre, email)
        userRepository.agregar(usuario)
        return userRepository.listar().last()
    }

    fun listarUsuarios(): List<Usuario> = userRepository.listar()


    fun tareasPorUsuario(usuarioId: Int): List<Tarea> {
        return repositorio.listar()
            .filterIsInstance<Tarea>()
            .filter { it.asignadoA?.id == usuarioId }
    }

    fun buscarActividad(id: Long): Actividad? = repositorio.buscarPorId(id)
}
