package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.domain.enums.SEQUENCE

interface SequenceExtrapolator {


    companion object {


        fun instance(sequenceType: SEQUENCE): SequenceExtrapolator {
            return when (sequenceType) {
                SEQUENCE.NUMBER -> SequenceNumberExtrapolator()
                SEQUENCE.DATE -> SequenceDateExtrapolator()
                SEQUENCE.TIMESTAMP -> SequenceTimeStampExtrapolator()
                SEQUENCE.TIME -> SequenceTimeExtrapolator()
            }
        }
    }


    fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult

}