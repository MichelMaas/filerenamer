package nl.maas.filerenamer.frontend

import nl.maas.filerenamer.frontend.wicket.pages.MainPage
import org.apache.wicket.Page
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.resource.FileSystemResourceReference
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import java.nio.file.Path

@SpringBootApplication
class WicketApplication : WebApplication() {
    override fun internalInit() {
        super.internalInit()
        val favicon = FileSystemResourceReference(
            "favicon",
            Path.of(this.javaClass.getResource("/open/images/icon.png").getPath())
        )
        mountResource("/images/icon.png", favicon)
        val ctx = arrayOf<ConfigurableApplicationContext?>(ContextProvider.ctx)
    }

    override fun getHomePage(): Class<out Page?> {
        return MainPage::class.java
    }

    companion object {
        private fun runApplication(args: Array<out String>): ConfigurableApplicationContext? {
            val application =
                SpringApplication(WicketApplication::class.java)

            application.setHeadless(false)

            return application.run(*args)
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            Companion.runApplication(args)
        }

        fun restart() {
            val ctx = arrayOf<ConfigurableApplicationContext?>(ContextProvider.ctx)
            val args = ctx[0]!!.getBean<ApplicationArguments>(ApplicationArguments::class.java)
            val manager = ctx[0]!!.getBean<BrowserManager>(BrowserManager::class.java)
            manager.close()
            val thread = Thread(Runnable {
                ctx[0]!!.close()
                ctx[0] = runApplication(arrayOf(*args.getSourceArgs()))
            })

            thread.setDaemon(false)
            thread.start()
        }
    }
}
