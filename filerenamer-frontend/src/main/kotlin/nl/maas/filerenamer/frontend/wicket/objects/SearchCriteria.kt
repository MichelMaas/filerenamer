package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.domain.enums.SEQUENCE
import java.io.Serializable

data class SearchCriteria(
    var folderPath: String = System.getProperty("user.home"),
    var sequence: SEQUENCE = SEQUENCE.NUMBER
) : Serializable {

    companion object {
        fun default() = SearchCriteria()
    }
}