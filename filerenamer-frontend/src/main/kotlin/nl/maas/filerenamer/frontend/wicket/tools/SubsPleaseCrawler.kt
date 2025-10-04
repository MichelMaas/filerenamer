package nl.maas.filerenamer.frontend.wicket.tools

import nl.maas.filerenamer.frontend.objects.data.Batch
import nl.maas.filerenamer.frontend.objects.data.Episode
import nl.maas.filerenamer.frontend.objects.data.Show
import nl.maas.wicket.framework.viewer.ScrapeViewer
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Component

private const val MAIN_URL = "https://subsplease.org/shows"

@Component
class SubsPleaseCrawler {

    private final val HEADLESS = true

    fun getAvailableAnime(): List<Show> {
        val viewer = ScrapeViewer.get(HEADLESS)
        viewer.startBrowser(MAIN_URL)
        val shows = getShows(viewer)
        viewer.close()
        return shows
    }

    fun getAvailableDownloadables(show: Show): Show {
        val viewer = ScrapeViewer.get(HEADLESS)
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
                        it.findElement(By.className("episode-title")).text,
                        it.findElements(By.tagName("a")).firstOrNull { is720pMagnetLink(it) }?.getDomAttribute("href")
                            ?: it.findElement(By.tagName("a")).getDomAttribute("href") ?: ""
                    )
                }
        return episodes
    }

    private fun getBatchesForAnime(viewer: ScrapeViewer): List<Batch> {
        val batches =
            viewer.findElementsByClass("show-release-item").filter { it.text.contains("batch", true) }
                .map {
                    Batch(
                        it.findElement(By.className("episode-title")).text,
                        it.findElements(By.tagName("a")).firstOrNull { is720pMagnetLink(it) }?.getDomAttribute("href")
                            ?: it.findElement(By.tagName("a")).getDomAttribute("href") ?: ""
                    )
                }
        return batches
    }

    private fun is720pMagnetLink(element: WebElement): Boolean =
        element.getDomAttribute("href")?.startsWith("magnet:") ?: false && element.getDomAttribute("href")
            ?.contains("720p") ?: false

    private fun getShows(viewer: ScrapeViewer): List<Show> {
        viewer.navigateTo(MAIN_URL)
        val anime =
            viewer.findElementsByTagName("a").filter { it.getDomAttribute("href")?.startsWith("/shows") ?: false }
        return anime.map { Show(it.text, "https://subsplease.org/${it.getDomAttribute("href")!!}") }
    }
}