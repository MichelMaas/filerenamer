package nl.maas.filerenamer.frontend.wicket.panels

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.frontend.services.TransmissionService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicPanel.Companion.ROW_CONTENT_ID
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.spring.injection.annot.SpringBean

class SeriesPanel : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache

    @SpringBean
    private lateinit var translator: Translator

    @SpringBean
    private lateinit var transmission: TransmissionService

    override fun onBeforeRender() {
        super.onBeforeRender()
        createPanel()
    }

    private fun createPanel() {
        val panel = DynamicPanel("panel")

        val foundEpisodes =
            mapOf("Batches" to modelCache.chosenAnime.batches, "Episodes" to modelCache.chosenAnime.episodes)
        foundEpisodes.keys.forEach { panel.addRow(it, 12) }
        foundEpisodes.forEach {
            panel.addOrReplaceComponentToColumn(it.key, 0, createTable(it.key, it.value.map { it.name }))
        }
        panel.addRow("buttons", 4, 4, 4)
        panel.addOrReplaceComponentToColumn("buttons", 0, createBatchesButton())
        panel.addOrReplaceComponentToColumn("buttons", 1, createEpisodesButton())
        addOrReplace(panel)
    }

    private fun createEpisodesButton(): Component {
        return object : SimpleAjaxButton(
            ROW_CONTENT_ID,
            "Download Episodes",
            Buttons.Type.Primary,
            SimpleAjaxButton.Size.NORMAL,
            translator
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                modelCache.chosenAnime.episodes.forEach { transmission.addTorrent(it.magnetLink) }
            }
        }
    }

    private fun createBatchesButton(): Component {
        return object : SimpleAjaxButton(
            ROW_CONTENT_ID,
            "Download Batches",
            Buttons.Type.Primary,
            SimpleAjaxButton.Size.NORMAL,
            translator
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                modelCache.chosenAnime.batches.forEach { transmission.addTorrent(it.magnetLink) }
            }
        }
    }

    private fun createTable(key: String, value: List<String>): Component {
        return DynamicDataTable.get(
            ROW_CONTENT_ID,
            value.map { Tuple(key to it) },
            24
        )
    }
}