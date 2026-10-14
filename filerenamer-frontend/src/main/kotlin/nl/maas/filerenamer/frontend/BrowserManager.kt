package nl.maas.filerenamer.frontend

import jakarta.inject.Inject
import jakarta.inject.Singleton
import nl.maas.framework.io.utils.FileUtils
import nl.maas.wicket.framework.viewer.Viewers
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import kotlin.io.path.absolutePathString


@Singleton
@Component
class BrowserManager private constructor() : ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private lateinit var appContext: ApplicationContext
    val viewer = Viewers.viewer
    private val appName = "FileRenamer"

    private fun startBrowser() {
        val url = "http://localhost:8080"
        val icon = FileUtils.findFileOrFolderInAnyAppFolder(
            "brand.png", appName
        )

        icon?.let { viewer.icon = it.absolutePathString() }
        viewer.startBrowser(url, appName)
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