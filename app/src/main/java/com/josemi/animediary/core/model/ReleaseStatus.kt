package com.josemi.animediary.core.model

enum class ReleaseStatus(val label: String) {
    Unknown("Desconocido"),
    NotYetAired("No emitido"),
    Airing("En emision"),
    Finished("Finalizado"),
    WaitingNewSeason("Esperando nueva temporada"),
    Cancelled("Cancelado")
}

