package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.tools.SubsPleaseCrawler
import nl.maas.wicket.framework.objects.Tuple
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject


@Component
class CrawlerService {

    @Inject
    protected lateinit var propertiesCache: PropertiesCache

    @Inject
    protected lateinit var modelCache: ModelCache

    @Inject
    protected lateinit var subsPleaseCrawler: SubsPleaseCrawler

    init {

    }

    fun fetchSeries(): List<Tuple> {
        val start = LocalTime.now()
        val availableAnime = subsPleaseCrawler.getAvailableAnime()
        val end = LocalTime.now()
        println("Crawl completed in ${Duration.between(start, end)}")
        return availableAnime.map {
            Tuple(
                listOf(
                    "Series" to it.key,
                    "Number of episodes" to it.value
                ).toMap()
            )
        }
    }

    fun getDownloadables(name: String): Map<String, List<String>> {
        return mapOf(
            "Batches" to subsPleaseCrawler.getBatchesForAnime(name),
            "Episodes" to subsPleaseCrawler.getEpisodesForAnime(name)
        )
    }
}