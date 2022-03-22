package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationResult

interface SequenceExtrapolator {


    companion object {

        enum class SEQUENCE {
            TIMESTAMP,
            DATE,
            NUMBER;

            companion object {

                fun forValue(value: String): SEQUENCE {
                    return when (value) {
                        "N" -> NUMBER
                        "n" -> NUMBER
                        "D" -> DATE
                        "d" -> DATE
                        "T" -> TIMESTAMP
                        "t" -> TIMESTAMP
                        else -> DATE
                    }
                }
            }
        }

        fun instance(sequenceType: SEQUENCE): SequenceExtrapolator {
            return when (sequenceType) {
                SEQUENCE.NUMBER -> SequenceNumberExtrapolator()
                SEQUENCE.DATE -> SequenceDateExtrapolator()
                SEQUENCE.TIMESTAMP -> SequenceTimeStampExtrapolator()
            }
        }
    }

    fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult

}