package nl.maas.filerenamer.domain.enums

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