package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator.Companion.SEQUENCE
import java.io.Serializable

data class SearchResult(
    val sequenceType: SEQUENCE,
    val fileData: MutableMap<String, ExtrapolationResult>
) : Serializable {

    fun isEmpty() = fileData.isEmpty()
}