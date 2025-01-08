package nl.maas.filerenamer.frontend

import nl.maas.filerenamer.frontend.wicket.pages.MainPage
import org.apache.wicket.Page
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.resource.FileSystemResourceReference
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.nio.file.Path

@SpringBootApplication
class WicketApplication : WebApplication() {
    override fun internalInit() {
        super.internalInit()
        val favicon =
            FileSystemResourceReference("favicon", Path.of(this.javaClass.getResource("/open/images/icon.png").path))
        mountResource("/images/icon.png", favicon)
        val ctx = arrayOf(ContextProvider.ctx)
    }

    override fun getHomePage(): Class<out Page?> {
        return MainPage::class.java
    }

//    companion object {
//
//        fun restart() {
//            val ctx = arrayOf(ContextProvider.ctx)
//            val args = ctx[0].getBean(ApplicationArguments::class.java)
//            val manager: BrowserManager = ctx[0].getBean(BrowserManager::class.java)
//            manager.close()
//            val thread = Thread {
//                ctx[0].close()
//                ctx[0] = SpringApplication.run(WicketApplication::class.java, *args.sourceArgs)
//            }
//
//            thread.isDaemon = false
//            thread.start()
//        }
//    }
}

fun main(args: Array<String>) {
    SpringApplication.run(WicketApplication::class.java, *args)
}
