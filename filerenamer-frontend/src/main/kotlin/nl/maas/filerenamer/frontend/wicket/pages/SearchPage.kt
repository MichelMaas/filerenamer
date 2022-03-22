package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import nl.maas.filerenamer.frontend.wicket.panels.SearchPanel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters

@WicketHomePage
open class SearchPage(parameters: PageParameters) : BasePage(parameters) {

    override fun onInitialize() {
        super.onInitialize()
        addOrReplace(
            SearchPanel(
                "panel",
                Model.of(modelCache.searchCriteria)
            )
        )
    }

}