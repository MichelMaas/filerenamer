package nl.maas.filerenamer.frontend.services

import nl.maas.framework.torrent.rest.client.TransmissionClient
import nl.maas.framework.torrent.transmission.io.TransmissionTorrent
import nl.maas.wicket.framework.objects.Tuple
import org.springframework.stereotype.Component
import java.net.URI
import java.util.*

@Component
class TransmissionService {

    val transmissionClient = client()

    private fun client(): TransmissionClient = TransmissionClient(
        URI.create("http://10.0.0.6:8181/transmission/rpc"),
        "michel",
        Base64.getEncoder().encodeToString("llae2215".toByteArray()),
        true
    )

    fun getAllTorrents(): List<Tuple> {
        val torrents = transmissionClient.fetchAll()
        return torrents.map { toTuple(it) }
    }

    fun addTorrent(torrentUrl: String) {
        transmissionClient.addTorrent(torrentUrl)
    }

    private fun toTuple(it: TransmissionTorrent): Tuple {
        return Tuple(
            listOf(
                "Name" to it.name,
                "Status" to it.getStatusEnum(),
                "Progress" to it.percentComplete
            ).toMap()
        )
    }
}