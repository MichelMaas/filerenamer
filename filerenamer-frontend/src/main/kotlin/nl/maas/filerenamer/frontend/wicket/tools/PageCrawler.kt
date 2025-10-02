package nl.maas.filerenamer.frontend.wicket.tools

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL

class PageCrawler : WebCrawler() {

    private final val MAGNET_PREFIX = "magnet:"

    override fun shouldVisit(referringPage: Page, url: WebURL): Boolean {
        return url.toString().startsWith("https://subsplease.org/shows/")
    }

    override fun visit(page: Page) {
        val parseData = page.parseData as HtmlParseData
        if (parseData.outgoingUrls.any { it.toString().startsWith(MAGNET_PREFIX) }) {
            val title = parseData.title
//        magnet:?xt=urn:btih:5ZNBFEOW7RT6AJF5246JHJ6XPTQCY2E2&dn=%5BSubsPlease%5D%20100-man%20no%20Inochi%20no%20Ue%20ni%20Ore%20wa%20Tatte%20Iru%20-%2024%20%28720p%29%20%5BBAAD157A%5D.mkv&xl=743070557&tr=http%3A%2F%2Fnyaa.tracker.wf%3A7777%2Fannounce&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.opentrackr.org%3A1337%2Fannounce&tr=udp%3A%2F%2F9.rarbg.to%3A2710%2Fannounce&tr=udp%3A%2F%2F9.rarbg.me%3A2710%2Fannounce&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker.internetwarriors.net%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker.cyberia.is%3A6969%2Fannounce&tr=udp%3A%2F%2Fexodus.desync.com%3A6969%2Fannounce&tr=udp%3A%2F%2Ftracker3.itzmx.com%3A6961%2Fannounce&tr=udp%3A%2F%2Ftracker.torrent.eu.org%3A451%2Fannounce&tr=udp%3A%2F%2Ftracker.tiny-vps.com%3A6969%2Fannounce&tr=udp%3A%2F%2Fretracker.lanta-net.ru%3A2710%2Fannounce&tr=http%3A%2F%2Fopen.acgnxtracker.com%3A80%2Fannounce&tr=wss%3A%2F%2Ftracker.openwebtorrent.com
            val magnets =
                parseData.outgoingUrls.filter {
                    it.toString().startsWith(MAGNET_PREFIX) && it.toString().contains("720p")
                }

        }
    }
}