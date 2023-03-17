package nl.maas.filerenamer.domain

import nl.maas.filerenamer.extrapolation.FileMetaData
import java.io.Serializable

data class ExtrapolationResult(
    private val _files: List<FileMetaData>,
    var warnings: List<Warning>,
    val failures: ExtrapolationFailures
) : Serializable {
    constructor(_files: List<FileMetaData>) : this(_files, ArrayList(), ExtrapolationFailures(HashMap()))


    var sequence: List<Int> = listOf()
    val files = _files.sortedBy { it.name }

    fun addWarnings(vararg warnings: Warning) {
        val list = ArrayList(this.warnings)
        list.addAll(warnings)
        this.warnings = list;
    }

    fun renameRequired(): Boolean {
        return files.any { !it.proposedNames.contains("${it.name}.${it.file.extension}") }
    }
}