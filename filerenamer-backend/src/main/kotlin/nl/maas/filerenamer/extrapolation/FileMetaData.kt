package nl.maas.filerenamer.extrapolation

import java.io.File
import java.io.Serializable

data class FileMetaData(val file: File) : Serializable {

    val delimiters: List<String> = listOf()
    lateinit var dirName: String
    lateinit var name: String

    @Transient
    var potentialSequenceNumbers = emptySet<String>().toMutableSet()
    var sequence: String? = null
    var newName: String
        get() = if (includeParentMapInName) proposedNames[1] else proposedNames[0]
        set(name) = if (includeParentMapInName) proposedNames[1] = name else proposedNames[0] = name
    lateinit var extrapolatedName: List<String>
    var includeParentMapInName = false
    var proposedNames: Array<String> = arrayOf("", "")

    init {
        name = file.nameWithoutExtension
        extrapolatedName = name.split("-", " ", "_", ".", "[", "]")
        dirName = file.parentFile.name
    }

    fun hasPotentialSequenceFor(number: Int): Boolean {
        return potentialSequenceNumbers.map { it.toIntOrNull() }.filterNotNull().contains(number)
    }

    override fun toString(): String {
        return name
    }
}