package nl.maas.filerenamer.extrapolation

interface SequenceExtrapolator {


    companion object {

        enum class SEQUENCE {
            DATE,
            NUMBER;

            companion object {

                fun forValue(value: String): SEQUENCE {
                    return when (value) {
                        "N" -> NUMBER
                        "n" -> NUMBER
                        "D" -> DATE
                        "d" -> DATE
                        else -> DATE
                    }
                }
            }
        }

        fun instance(sequenceType: SEQUENCE): SequenceExtrapolator {
            return when (sequenceType) {
                SEQUENCE.NUMBER -> SequenceNumberExtrapolator()
                SEQUENCE.DATE -> SequenceDateExtrapolator()
            }
        }
    }

    fun findSequenceForFiles(fileData: List<FileMetaData>)

}