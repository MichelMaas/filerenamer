package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import org.springframework.stereotype.Component

@Component
class ModelCache {
    var searchCriteria = SearchCriteria("", SearchCriteria.SequenceType.NUMERIC)
    var searchResult = SearchResult(SearchCriteria.SequenceType.NUMERIC, HashMap())
}