package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.frontend.objects.data.Show
import nl.maas.framework.torrent.transmission.io.TransmissionTorrent
import nl.maas.wicket.framework.objects.Tuple
import org.springframework.stereotype.Component

@Component
class TupleConvertorService {

    fun convert(show: Show): Tuple {
        return Tuple("Series" to show.name)
    }

    fun convert(it: TransmissionTorrent): Tuple {
        return Tuple(
            listOf(
                "Name" to it.name,
                "Status" to it.getStatusEnum(),
                "Progress" to it.percentComplete
            ).toMap()
        )
    }
}