package nl.maas.filerenamer.frontend.services

import dev.katsute.mal4j.MyAnimeList
import nl.maas.filerenamer.frontend.objects.data.Show
import org.springframework.stereotype.Component
import java.time.LocalTime

private const val MY_PRIVATE_ANIME_INFO = "ff51527cf1668a0e6e10e723f32a1c35"

@Component
class MyAnimeListService {
    val myAnimeList = MyAnimeList.withClientID(MY_PRIVATE_ANIME_INFO)
    private var lastCall: Long = 0

    fun addAnimeInformation(show: Show) {
        val now = LocalTime.now().toNanoOfDay() / 1000
        val wait = 1000 - (now - lastCall)
        if (wait > 0) {
            println("Waiting $wait milliseconds.")
            Thread.sleep(wait)
        }
        val query = if (show.name.length > 60) show.name.substring(0, 60) else show.name
        val search = myAnimeList.getAnime().withQuery(query).withLimit(1).withAllFields().search()
        show.information = search.firstOrNull()?.synopsis ?: ""
        show.pictureURL = search.firstOrNull()?.mainPicture?.mediumURL ?: ""
        show.status = search.firstOrNull()?.status.toString() ?: ""
        lastCall = LocalTime.now().toNanoOfDay() / 1000
    }
}