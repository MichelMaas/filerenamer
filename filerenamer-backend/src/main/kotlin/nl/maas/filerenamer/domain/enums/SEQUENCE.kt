package nl.maas.filerenamer.domain.enums

enum class SEQUENCE {
    TIME,
    TIMESTAMP,
    DATE,
    NUMBER;

    companion object {

        fun forValue(value: String): SEQUENCE {
            return when (value) {
                "N", "n" -> NUMBER
                "D", "d" -> DATE
                "S", "s" -> TIMESTAMP
                "T", "t" -> TIME
                else -> DATE
            }
        }
    }
}