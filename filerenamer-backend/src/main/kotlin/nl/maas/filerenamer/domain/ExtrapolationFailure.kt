package nl.maas.filerenamer.domain

import nl.maas.filerenamer.domain.ExtrapolationFailures.FailureType
import nl.maas.filerenamer.extrapolation.FileMetaData

class ExtrapolationFailure<T : Comparable<T>>(val type: FailureType, val sequence: T, val failed: List<FileMetaData>) {
    constructor(type: FailureType, sequence: T, vararg failed: FileMetaData) : this(type, sequence, failed.asList())
}