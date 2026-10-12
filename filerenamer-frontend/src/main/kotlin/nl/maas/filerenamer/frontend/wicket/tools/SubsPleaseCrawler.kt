package nl.maas.filerenamer.frontend.wicket.tools

import jakarta.inject.Inject
import nl.maas.filerenamer.frontend.objects.data.Batch
import nl.maas.filerenamer.frontend.objects.data.Episode
import nl.maas.filerenamer.frontend.objects.data.Show
import nl.maas.framework.io.utils.AppDirectories
import nl.maas.wicket.framework.domain.scrapers.ScrapeElement
import nl.maas.wicket.framework.viewer.ScrapeViewer
import nl.maas.wicket.framework.viewer.Viewers
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

private const val MAIN_URL = "https://subsplease.org/shows"

@Component
class SubsPleaseCrawler {

    @Inject
    private lateinit var appContext: ApplicationContext
    private final val HEADLESS = true
    private val appName = "FileRenamer"


    fun getAvailableAnime(): List<Show> {
        val viewer = Viewers.scraper
        viewer.setBrowserLocation(AppDirectories.cacheDir(appName))
        viewer.startBrowser(MAIN_URL)
        val shows = getShows(viewer)
        viewer.close()
        return shows
    }

    fun getAvailableDownloadables(show: Show): Show {
        val viewer = Viewers.scraper
        viewer.setBrowserLocation(AppDirectories.cacheDir(appName))
        viewer.startBrowser(show.showHome)
        show.batches = getBatchesForAnime(viewer)
        show.episodes = getEpisodesForAnime(viewer)
        return show
    }

    private fun getNumberOfEpisodes(viewer: ScrapeViewer, link: Pair<String, String>): Int {
        viewer.navigateTo(link.second)

        val episodes =
            viewer.findElementsByClass("episode-title").map { it.text }

        val size = episodes.size
        return size
    }

    private fun getEpisodesForAnime(viewer: ScrapeViewer): List<Episode> {
        val episodes =
            viewer.findElementsByClass("show-release-item").filterNot { it.text.contains("batch", true) }
                .map {
                    Episode(
                        it.findElementsByClass("episode-title").first().text,
                        it.findElementsByTagName("a").firstOrNull { is720pMagnetLink(it) }?.getAttribute("href")
                            ?: it.findElementsByTagName("a").first().getAttribute("href") ?: ""
                    )
                }
        return episodes
    }

    private fun getBatchesForAnime(viewer: ScrapeViewer): List<Batch> {
        val batches =
            viewer.findElementsByClass("show-release-item").filter { it.text.contains("batch", true) }
                .map {
                    Batch(
                        it.findElementsByClass("episode-title").first().text,
                        it.findElementsByTagName("a").firstOrNull { is720pMagnetLink(it) }?.getAttribute("href")
                            ?: it.findElementsByTagName("a").first().getAttribute("href") ?: ""
                    )
                }
        return batches
    }

    private fun is720pMagnetLink(element: ScrapeElement): Boolean =
        element.getAttribute("href")?.startsWith("magnet:") ?: false && element.getAttribute("href")
            ?.contains("720p") ?: false

    private fun getShows(viewer: ScrapeViewer): List<Show> {
        viewer.navigateTo(MAIN_URL)
        val anime =
            viewer.findElementsByTagName("a").filter { it.getAttribute("href")?.startsWith("/shows") ?: false }
        return anime.map { Show(it.text, "https://subsplease.org/${it.getAttribute("href")!!}") }
    }
}