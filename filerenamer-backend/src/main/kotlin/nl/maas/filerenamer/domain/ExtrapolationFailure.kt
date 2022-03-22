package nl.maas.filerenamer.domain

import nl.maas.filerenamer.domain.ExtrapolationFailures.FailureType
import nl.maas.filerenamer.extrapolation.FileMetaData
import java.io.Serializable

class ExtrapolationFailure<T : Serializable>(
    val type: FailureType,
    val sequence: T,
    val failed: MutableList<FileMetaData>
) :
    Serializable {
    constructor(type: FailureType, folder: String, sequence: T, vararg failed: FileMetaData) : this(
        type,
        sequence,
        failed.toMutableList()
    )

    init {
        if (FailureType.TooMany.equals(type)) {
            failed.forEach { it.potentialSequenceNumbers.add("NONE") }
        }
    }
}