package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import java.io.Serializable

data class SearchCriteria(val path: String, val sequenceType: SequenceExtrapolator.Companion.SEQUENCE):Serializable {

}