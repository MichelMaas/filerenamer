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
        URI.create("http://transmission.mnw:9091/transmission/rpc"),
        "michel",
        Base64.getEncoder().encodeToString("llae2215".toByteArray()),
        true
    )

    fun getAllTorrents(): List<TransmissionTorrent> {
        val torrents = try {
            transmissionClient.fetchAll()
        } catch (ex: Exception) {
            emptyList()
        }
        return torrents
    }

    fun addTorrent(torrentUrl: String) {
        try {
            transmissionClient.addTorrent(torrentUrl)
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    fun cleanUp() {
        try {
            val torrents = transmissionClient.fetchAll().filter { it.getStatusEnum().equals(TorrentStatus.DONE) }
            transmissionClient.removeTorrent(*torrents.toTypedArray())
        } catch (ex: Exception) {
            println(ex.message)
        }
    }


}