package nl.maas.filerenamer.frontend.wicket.pages

import nl.maas.filerenamer.frontend.wicket.panels.FolderPanel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters

class FilesPage(parameters: PageParameters) : BasePage(parameters) {

    override fun onInitialize() {
        super.onInitialize()
        add(FolderPanel("filesPanel", Model.of(modelCache.searchResult)))
    }
}