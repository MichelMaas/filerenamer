package nl.maas.filerenamer.extrapolation

import java.io.File
import java.io.Serializable

data class FileMetaData(val file: File) : Serializable {

    val delimiters: List<String> = listOf()
    lateinit var dirName: String
    lateinit var name: String
    var potentialSequenceNumbers = emptySet<String>().toMutableSet()
    var sequence: String? = null
    var newName: String? = null
    lateinit var extrapolatedName: List<String>

    init {
        name = file.nameWithoutExtension
        extrapolatedName = name.split("-", " ", "_", ".", "[", "]")
        dirName = file.parentFile.name
    }

    fun hasPotentialSequenceFor(number: Int): Boolean {
        return potentialSequenceNumbers.map { it.toIntOrNull() }.filterNotNull().contains(number)
    }
}