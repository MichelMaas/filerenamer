package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.FileRenamerRIABasePageProperties
import nl.maas.filerenamer.frontend.wicket.panels.OverviewPanel
import nl.maas.filerenamer.frontend.wicket.panels.SearchPanel
import nl.maas.wicket.framework.pages.RIAPage

@WicketHomePage
class MainPage : RIAPage<ModelCache>(FileRenamerRIABasePageProperties()) {
    init {
        registerPanels(
            "Search" to SearchPanel(),
            "Overview" to OverviewPanel()
//            "Details" to DetailPanel()
        )
    }
}