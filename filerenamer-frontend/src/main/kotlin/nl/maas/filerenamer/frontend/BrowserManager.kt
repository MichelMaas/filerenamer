package nl.maas.filerenamer.frontend

import jakarta.inject.Inject
import jakarta.inject.Singleton
import nl.maas.filerenamer.io.FileUtils
import nl.maas.wicket.framework.viewer.Viewers
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component


@Singleton
@Component
class BrowserManager private constructor() : ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private lateinit var appContext: ApplicationContext
    val viewer = Viewers.viewer
    private fun startBrowser() {
        val url = "http://localhost:8080"
        val icon = FileUtils.findFile("brand.png")
        viewer.icon = icon
        viewer.startBrowser(url, "FileRenamer")
        viewer.onCloseRequest {
            System.exit(0)
        }
    }

    fun close() {
        viewer.close()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        startBrowser()
    }
}