package nl.maas.filerenamer.domain

import nl.maas.filerenamer.extrapolation.FileMetaData
import java.io.Serializable

data class ExtrapolationResult(
    val files: List<FileMetaData>,
    var warnings: List<Warning>,
    val failures: ExtrapolationFailures
) : Serializable {
    constructor(files: List<FileMetaData>) : this(files, ArrayList(), ExtrapolationFailures(HashMap()))


    var min = 0
    var max = 0

    fun addWarnings(vararg warnings: Warning) {
        val list = ArrayList(this.warnings)
        list.addAll(warnings)
        this.warnings = list;
    }

    fun renameRequired(): Boolean {
        return files.any { !"${it.name}.${it.file.extension}".equals(it.newName) }
    }
}