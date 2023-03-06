package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicTableComponent
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.pages.BasePage
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget

class OverviewPage : BasePage<ModelCache>(
    ContextProvider.ctx.getBean(ModelCache::class.java),
    ContextProvider.ctx.getBean(PropertiesCache::class.java).translator.forPageClass(SearchPage::class)
) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createOverview())
    }

    private fun createOverview(): DynamicPanel {
        return DynamicPanel("panel").addRow("table", 12)
            .addOrReplaceComponentToColumn("table", 0, setUpTable())
    }

    private fun setUpTable(): Component {
        return object : DynamicTableComponent(
            DynamicPanel.ROW_CONTENT_ID,
            modelCache.files.map { Tuple("Path" to it.key.path, "Succeeded" to validate(it.value)) }.toMutableList()
        ) {
            override fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {
                modelCache.selectedFolder =
                    modelCache.files.keys.first { it.path.equals(tuple.columns.values.first()) }
                val extrapolationResult =
                    modelCache.files[modelCache.selectedFolder]
                if (extrapolationResult?.files?.any { extrapolationResult.failures.hasFailures(it) } ?: false) {
                    modelCache.selectedFolder =
                        modelCache.files.keys.first { it.path.equals(tuple.columns.values.first()) }
                    setResponsePage(DetailPage::class.java)
                }
            }
        }
    }

    private fun validate(value: ExtrapolationResult): String {
        return if (value.files.any { value.failures.hasFailures(it) }) "NOK" else "OK"
    }


    override fun createNavBarButtons(): Array<NavbarButton<*>> {
        return ButtonTypes.values().map { it.toNavBarButton() }.toTypedArray()
    }

    override fun isButtonActive(button: BaseNavbarButton): Boolean {
        return !button.buttonType.equals(ButtonTypes.OVERVIEW)
    }
}