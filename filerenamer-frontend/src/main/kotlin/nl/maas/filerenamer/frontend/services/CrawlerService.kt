package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.frontend.objects.data.Show
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.tools.SubsPleaseCrawler
import nl.maas.wicket.framework.objects.Tuple
import org.openqa.selenium.InvalidArgumentException
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
        if (modelCache.series.isEmpty()) {
            crawlForShows()
        }
        return modelCache.series.map {
            Tuple(
                listOf(
                    "Series" to it.name,
                    "Number of episodes" to "${it.episodes.size}"
                ).toMap()
            )
        }
    }

    fun getDownloadables(name: String): Show {
        if (modelCache.series.none { it.name.equals(name) }) {
            throw InvalidArgumentException("No shows found with name: $name")
        } else {
            val show = modelCache.series.first { it.name.equals(name) }
            return subsPleaseCrawler.getAvailableDownloadables(show)
        }
    }

    fun crawlForShows() {
        val start = LocalTime.now()
        modelCache.series.addAll(subsPleaseCrawler.getAvailableAnime())
        val end = LocalTime.now()

        println("Crawl completed in ${Duration.between(start, end)}")
    }
}