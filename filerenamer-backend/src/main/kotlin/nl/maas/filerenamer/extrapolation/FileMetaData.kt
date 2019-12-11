package nl.maas.filerenamer.extrapolation

import java.io.File

data class FileMetaData(val file: File) {

    val delimiters: List<String> = listOf()
    lateinit var dirName: String
    lateinit var name: String
    var potentialSequenceNumberIndexes: List<Int>? = null
    var sequence: Int? = null
    var newName: String? = null
    lateinit var extrapolatedName: List<String>

    init {
        name = file.nameWithoutExtension
        extrapolatedName = name.split("-", " ", "_", ".", "[", "]")
        dirName = file.parentFile.name
    }

    fun hasPotentialSequenceIndexFor(number: Int): Boolean {
        return potentialSequenceNumberIndexes?.any { i -> number.equals(extrapolatedName.get(i).toIntOrNull()) }
                ?: false
    }
}