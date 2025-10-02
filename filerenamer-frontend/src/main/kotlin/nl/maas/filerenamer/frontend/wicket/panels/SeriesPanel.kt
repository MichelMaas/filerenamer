package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import org.apache.wicket.Component
import org.apache.wicket.spring.injection.annot.SpringBean

class SeriesPanel : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache

    override fun onBeforeRender() {
        super.onBeforeRender()
        createPanel()
    }

    private fun createPanel() {
        val panel = DynamicPanel("panel")
        modelCache.foundEpisodes.keys.forEach { panel.addRow(it, 12) }
        modelCache.foundEpisodes.forEach {
            panel.addOrReplaceComponentToColumn(it.key, 0, createTable(it.key, it.value))
        }
        addOrReplace(panel)
    }

    private fun createTable(key: String, value: List<String>): Component {
        return DynamicDataTable.get(DynamicPanel.ROW_CONTENT_ID, value.map { Tuple(key to it) }, 24)
    }
}