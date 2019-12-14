package nl.maas.filerenamer.extrapolation

interface SequenceExtrapolator {


    companion object {

        enum class SEQUENCE{
            DATE,
            NUMBER;
            companion object {

                fun forValue(value: String): SEQUENCE {
                    return when (value) {
                        "N" -> NUMBER
                        "D" -> DATE
                        else -> DATE
                    }
                }
            }
        }

        fun instance(sequenceType: SEQUENCE): SequenceExtrapolator{
            return when(sequenceType){
                SEQUENCE.DATE -> SequenceDateExtrapolator()
                SEQUENCE.NUMBER -> SequenceNumberExtrapolator()
            }
        }
    }

    fun findSequenceForFiles(fileData: List<FileMetaData>)

}