package nl.maas.filerenamer.domain

import nl.maas.filerenamer.extrapolation.FileMetaData

data class ExtrapolationResult(
    val files: List<FileMetaData>,
    var warnings: List<Warning>,
    val failures: ExtrapolationFailures
) {
    constructor(files: List<FileMetaData>) : this(files, ArrayList(), ExtrapolationFailures(HashMap()))

    fun addWarnings(vararg warnings: Warning) {
        val list = ArrayList(this.warnings)
        list.addAll(warnings)
        this.warnings = list;
    }
}