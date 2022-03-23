package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import org.apache.commons.lang3.StringUtils

class Filter() : java.io.Serializable {
    var path: String = StringUtils.EMPTY
    var sequence: SequenceExtrapolator.Companion.SEQUENCE = SequenceExtrapolator.Companion.SEQUENCE.NUMBER
}