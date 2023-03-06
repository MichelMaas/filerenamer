package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicPanel.Companion.ROW_CONTENT_ID
import nl.maas.wicket.framework.components.base.KeyValueView
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.pages.BasePage
import org.apache.wicket.Component

class DetailPage : BasePage<ModelCache>(
    ContextProvider.ctx.getBean(ModelCache::class.java),
    ContextProvider.ctx.getBean(PropertiesCache::class.java).translator.forPageClass(SearchPage::class)
) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createPanel())
    }

    private fun createPanel(): Component {
        return DynamicPanel("panel").addRow("Summary", 4, 8)
            .addOrReplaceComponentToColumn("Summary", 0, createOverview())
    }

    private fun createOverview(): Component {
        return KeyValueView(
            ROW_CONTENT_ID,
            translator,
            "Folder" to modelCache.selectedFolder!!.path,
            "Errors" to modelCache.files.get(modelCache.selectedFolder)!!.failures.failures.size
        )
    }

    override fun createNavBarButtons(): Array<NavbarButton<*>> {
        return ButtonTypes.values().map { it.toNavBarButton() }.toTypedArray()
    }

    override fun isButtonActive(button: BaseNavbarButton): Boolean {
        return true
    }
}