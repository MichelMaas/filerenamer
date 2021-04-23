package nl.maas.filerenamer.domain

data class ExtrapolationFailures(val failures: MutableMap<String, MutableList<ExtrapolationFailure<*>>>) {

    constructor() : this(HashMap())

    enum class FailureType {
        TooMany,
        None,
        NoNumberForFile;
    }

    fun <T : Comparable<T>> add(folder: String, vararg failures: ExtrapolationFailure<T>) {
        this.failures.putIfAbsent(folder, ArrayList())
        this.failures[folder]!!.addAll(failures)
    }

    fun <T : Comparable<T>> addAll(failures: Map<String, MutableList<ExtrapolationFailure<T>>>) {
        failures.keys.forEach { this.failures.putIfAbsent(it, ArrayList()) }
        failures.keys.forEach { this.failures[it]?.addAll(failures[it]!!) }
    }

    fun <T : Comparable<T>> getTypedFailures(): MutableMap<String, MutableList<ExtrapolationFailure<T>>> {
        return failures.map { entry ->
            entry.key to entry.value.map { extrapolationFailure -> extrapolationFailure as ExtrapolationFailure<T> }
                .toMutableList()
        }.toMap().toMutableMap()
    }

    fun isEmpty() = failures.isEmpty()
}