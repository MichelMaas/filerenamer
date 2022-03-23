package nl.maas.filerenamer.frontend.wicket.pages

import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.request.mapper.parameter.PageParameters

class DetailPage(parameters: PageParameters?) : BasePage(parameters) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(Label("panel", modelCache.selectedFolder!!.name))
    }
}