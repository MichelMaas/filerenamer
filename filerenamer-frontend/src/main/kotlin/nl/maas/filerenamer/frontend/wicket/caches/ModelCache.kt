package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import org.springframework.stereotype.Component

@Component
class ModelCache {
    var searchCriteria = SearchCriteria("", SequenceExtrapolator.Companion.SEQUENCE.NUMBER)
    var searchResult = SearchResult(SequenceExtrapolator.Companion.SEQUENCE.NUMBER, HashMap())
}