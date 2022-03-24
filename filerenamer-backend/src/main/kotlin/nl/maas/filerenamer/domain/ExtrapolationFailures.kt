package nl.maas.filerenamer.domain

import nl.maas.filerenamer.extrapolation.FileMetaData
import java.io.Serializable

data class ExtrapolationFailures(val failures: MutableMap<String, MutableSet<ExtrapolationFailure<out Serializable>>>) :
    Serializable {

    constructor() : this(HashMap())

    enum class FailureType {
        TooMany,
        None,
        NoNumberForFile;
    }

    fun <T : Serializable> add(folder: String, vararg failures: ExtrapolationFailure<T>) {
        this.failures.putIfAbsent(folder, HashSet())
        val presentFiles = this.failures[folder]!!.flatMap { it.failed.map { fileMetaData -> fileMetaData.name } }
        failures.forEach { it.failed.removeIf { presentFiles.contains(it.name) } }
        this.failures[folder]!!.addAll(failures.filterNot { it.failed.isEmpty() })
    }


    fun <T : Serializable> addAll(failures: Map<String, MutableSet<ExtrapolationFailure<T>>>) {
        failures.keys.forEach { add(it, *failures[it]!!.toTypedArray()) }
    }

    fun <T : Serializable> getTypedFailures(): MutableMap<String, MutableSet<ExtrapolationFailure<T>>> {
        return failures.map { entry ->
            entry.key to entry.value.map { extrapolationFailure -> extrapolationFailure as ExtrapolationFailure<T> }
                .toMutableSet()
        }.toMap().toMutableMap()
    }

    fun isEmpty() = failures.isEmpty()

    fun hasFailures(fileMetaData: FileMetaData): Boolean {
        return failures.values.any { it.any { flr -> flr.failed.contains(fileMetaData) } }
    }
}