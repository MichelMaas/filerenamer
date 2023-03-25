package nl.maas.filerenamer.domain

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.commons.lang3.StringUtils
import java.io.Serializable

data class ExtrapolationResult(
    private val _files: List<FileMetaData>,
    var warnings: List<Warning>,
    val failures: ExtrapolationFailures
) : Serializable {
    constructor(_files: List<FileMetaData>) : this(_files, ArrayList(), ExtrapolationFailures(HashMap()))

    val commonalities: Set<String>;

    init {
        commonalities =
            _files.first().extrapolatedName.filter { _files.all { fl -> fl.extrapolatedName.contains(it) } }
                .filter { !it.isNullOrBlank() }.distinct()
                .toSet()
    }

    var sequence: List<Int> = listOf()
    val files: List<FileMetaData>
        get() {
            return if (commonalities.isNotEmpty()) {
                _files.sortedBy { it.name.replaceBefore(commonalities.first(), StringUtils.EMPTY) }
            } else {
                _files.sortedBy { it.name }
            }
        }

    fun addWarnings(vararg warnings: Warning) {
        val list = ArrayList(this.warnings)
        list.addAll(warnings)
        this.warnings = list;
    }

    fun renameRequired(): Boolean {
        return files.any { !it.proposedNames.contains("${it.name}.${it.file.extension}") }
    }
}