package nl.maas.filerenamer.frontend.wicket.tools

import nl.maas.wicket.framework.viewer.ScrapeViewer
import org.springframework.stereotype.Component

private const val MAIN_URL = "https://subsplease.org/shows"

@Component
class SubsPleaseCrawler {

    private final val HEADLESS = true

    fun getAvailableAnime(): Map<String, String> {
        val viewer = ScrapeViewer.get(HEADLESS)
        viewer.startBrowser(MAIN_URL)
        val showLinks = getShowLinks(viewer)
        val mutableMap: MutableMap<String, String> = mutableMapOf()
        showLinks.forEach { link ->
            val numberOfEpisodes = getNumberOfEpisodes(viewer, link.toPair())
            mutableMap.put(link.key, "$numberOfEpisodes")
        }
        viewer.close()
        return mutableMap
    }

    private fun getNumberOfEpisodes(viewer: ScrapeViewer, link: Pair<String, String>): Int {
        viewer.navigateTo(link.second)

        val episodes =
            viewer.findElementsByClass("episode-title").map { it.text }

        val size = episodes.size
        return size
    }

    fun getEpisodesForAnime(name: String): List<String> {
        val viewer = ScrapeViewer.get(HEADLESS)
        viewer.startBrowser(MAIN_URL)
        val link = viewer.findElementsByTagName("a").first { it.text.equals(name) }.getDomAttribute("href").toString()
        viewer.navigateTo(link)
        val episodes =
            viewer.findElementsByClass("episode-title").filterNot { it.text.contains("batch", true) }.map { it.text }
        viewer.close()
        return episodes
    }

    fun getBatchesForAnime(name: String): List<String> {
        val viewer = ScrapeViewer.get(HEADLESS)
        viewer.startBrowser(MAIN_URL)
        val link = "${MAIN_URL.removeSuffix("/shows")}/${
            viewer.findElementsByTagName("a").first { it.text.equals(name) }.getDomAttribute("href").toString()
        }"
        viewer.navigateTo(link)
        val batches =
            viewer.findElementsByClass("episode-title").filter { it.text.contains("batch", true) }.map { it.text }
        viewer.close()
        return batches
    }

    private fun getShowLinks(viewer: ScrapeViewer): Map<String, String> {
        viewer.navigateTo(MAIN_URL)
        val anime =
            viewer.findElementsByTagName("a").filter { it.getDomAttribute("href")?.startsWith("/shows") ?: false }
        val associate = anime.associate { it.text to "https://subsplease.org/${it.getDomAttribute("href")!!}" }
        return associate
    }
}