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
    val nfcId: String,
    val status: String
) {
    init { require(name.isNotBlank() && location.isNotBlank() && description.isNotBlank()) }
}

data class ObrasOutputModel(
    val obras: List<Obra>
)
