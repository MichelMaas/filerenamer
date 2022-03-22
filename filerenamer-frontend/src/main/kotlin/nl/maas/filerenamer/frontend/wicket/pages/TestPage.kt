package nl.maas.filerenamer.frontend.wicket.pages

import nl.maas.filerenamer.frontend.wicket.panels.TestPanel
import org.apache.wicket.request.mapper.parameter.PageParameters

class TestPage(parameters: PageParameters?) : BasePage(parameters) {
    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(TestPanel("panel"))
    }
}