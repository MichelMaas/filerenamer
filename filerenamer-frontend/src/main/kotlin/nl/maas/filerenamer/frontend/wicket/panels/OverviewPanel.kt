package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.Component
import org.apache.wicket.spring.injection.annot.SpringBean

class OverviewPanel : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache

    @SpringBean
    private lateinit var translator: Translator

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
                switchToPanel(DetailPanel(), target)
            }).hover().sm()
    }

    private fun validate(value: ExtrapolationResult): String {
        return if (value.files.any { value.failures.hasFailures(it) }) "NOK" else "OK"
    }

    override fun isAvailable(): Boolean {
        return !modelCache.isEmpty()
    }
}