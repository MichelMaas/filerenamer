package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.FileRenamerBasePageProperties
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.pages.BasePage
import org.apache.wicket.Component

class OverviewPage : BasePage<ModelCache>(
    FileRenamerBasePageProperties.get()
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
        return DynamicDataTable.get(
            DynamicPanel.ROW_CONTENT_ID,
            modelCache.files.map { Tuple("Path" to it.key.path, "Succeeded" to validate(it.value)) }.toMutableList(),
            20,
            translator,
            false,
            { target, tuple ->
                modelCache.selectedFolder =
                    modelCache.files.keys.first { it.path.equals(tuple.columns.values.first()) }
                modelCache.selectedFolder =
                    modelCache.files.keys.first { it.path.equals(tuple.columns.values.first()) }
                setResponsePage(DetailPage::class.java)
            }).hover().sm()
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