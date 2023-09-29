package nl.maas.filerenamer.frontend

import nl.maas.filerenamer.io.FileUtils
import nl.maas.wicket.framework.viewer.Viewer
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.io.Zip
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@Component
class BrowserManager private constructor() : ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private lateinit var appContext: ApplicationContext
    val viewer = Viewer.get()
    private fun startBrowser() {
        val url = "http://localhost:8080"
        viewer.startBrowser(url)
    }

    fun close() {
        viewer.close()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        startBrowser()
    }
}