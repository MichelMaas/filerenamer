package nl.maas.filerenamer.frontend.services

import nl.maas.framework.torrent.domain.TorrentStatus
import nl.maas.framework.torrent.rest.client.TransmissionClient
import nl.maas.framework.torrent.transmission.io.TransmissionTorrent
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

    fun getAllTorrents(): List<TransmissionTorrent> {
        val torrents = transmissionClient.fetchAll()
        return torrents
    }

    fun addTorrent(torrentUrl: String) {
        transmissionClient.addTorrent(torrentUrl)
    }

    fun cleanUp() {
        val torrents = transmissionClient.fetchAll().filter { it.getStatusEnum().equals(TorrentStatus.DONE) }
        transmissionClient.removeTorrent(*torrents.toTypedArray())
    }


}