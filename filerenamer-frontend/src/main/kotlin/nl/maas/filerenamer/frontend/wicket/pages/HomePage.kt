package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import nl.maas.filerenamer.frontend.wicket.panels.FilesPanel
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.request.cycle.RequestCycle
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.wicketstuff.annotation.mount.MountPath

/**
 * Homepage
 */
@WicketHomePage
@MountPath("home")
class HomePage(parameters: PageParameters?) : WebPage() {
    companion object {
        private const val serialVersionUID = 1L
    }
    // TODO Add any page properties or variables here
    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    init {

        // Add the simplest type of label
        add(Label("message", "If you see this message wicket is properly configured and running"))
        addOrReplace(FilesPanel("panel"))
        // TODO Add your page's components here
    }

    override fun renderHead(response: IHeaderResponse?) {
        val webApplication = WebApplication.get()
        println("Nonce is${if (webApplication.cspSettings.isNonceEnabled) " niet" else ""} actief")
        webApplication.cspSettings.blocking().disabled()
        val nonce = webApplication.cspSettings.getNonce(RequestCycle.get())
//        add(WebMarkupContainer("cssLink").add(AttributeModifier.replace("nonce", nonce)))
        println(if (webApplication != null) "Webapplication gevonden!" else "Webapplication niet gevonden!")
        super.renderHead(response)
//        val forReference =
//            CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "css/main.css")).setNonce(nonce)
//        response?.render(forReference)
    }

}