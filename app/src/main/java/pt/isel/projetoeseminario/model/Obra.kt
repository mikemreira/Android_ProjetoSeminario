package pt.isel.projetoeseminario.model

import java.time.LocalDate

data class Obra(
    val oid: Int,
    val name: String,
    val location: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    //val foto: Byte? = null,
    val status: String
)

data class ObrasOutputModel(
    val obras: List<Obra>
)
