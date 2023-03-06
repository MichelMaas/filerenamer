package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.domain.enums.SEQUENCE
import org.apache.commons.lang3.StringUtils

class Filter() : java.io.Serializable {
    var path: String = StringUtils.EMPTY
    var sequence: SEQUENCE = SEQUENCE.NUMBER
}