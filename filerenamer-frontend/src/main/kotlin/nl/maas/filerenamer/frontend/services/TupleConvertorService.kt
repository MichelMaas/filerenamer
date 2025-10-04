package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.frontend.objects.data.Show
import nl.maas.wicket.framework.objects.Tuple
import org.springframework.stereotype.Component

@Component
class TupleConvertorService {

    fun convert(show: Show): Tuple {
        return Tuple("Series" to show.name)
    }

}