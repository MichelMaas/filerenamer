package nl.maas.filerenamer.frontend.wicket.objects

import java.io.Serializable

data class SearchCriteria(val path: String, val sequenceType: SequenceType):Serializable {

    public enum class SequenceType {
        NUMERIC,
        DATE
    }
}