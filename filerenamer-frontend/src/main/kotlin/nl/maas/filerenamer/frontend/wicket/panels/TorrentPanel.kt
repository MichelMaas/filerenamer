package nl.maas.filerenamer.frontend.wicket.panels

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.frontend.services.CrawlerService
import nl.maas.filerenamer.frontend.services.TransmissionService
import nl.maas.filerenamer.frontend.services.TupleConvertorService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.Torrent
import nl.maas.wicket.framework.components.base.*
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.spring.injection.annot.SpringBean

class TorrentPanel : RIAPanel() {

    @SpringBean
    private lateinit var transmission: TransmissionService

    @SpringBean
    private lateinit var translator: Translator

    @SpringBean
    private lateinit var crawler: CrawlerService

    @SpringBean
    private lateinit var modelCache: ModelCache

    @SpringBean
    private lateinit var tupleConvertorService: TupleConvertorService

    private val torrent = Torrent()

    override fun onBeforeRender() {
        super.onBeforeRender()
        createScreen()
    }

    private fun createScreen() {
        val seriesTables = createSeriesTables()
        val dynamicPanel =
            DynamicPanel("panel").addRow("torrentList", 12).addRow("torrentURL", 12).addRow("seriesList", 2, 10)
                .addOrReplaceComponentToColumn(
                    "torrentList",
                    0,
                    createDataTable()
                )
                .addOrReplaceComponentToColumn("seriesList", 0, createFetchButton(seriesTables))
                .addOrReplaceComponentToColumn("seriesList", 1, seriesTables)
                .addOrReplaceComponentToColumn("torrentURL", 0, createForm())
        addOrReplace(dynamicPanel)
    }

    private fun createFetchButton(seriesTables: CollapsablePanelGroup): Component {
        return object : SimpleAjaxButton(
            DynamicPanel.ROW_CONTENT_ID,
            "Fetch",
            Buttons.Type.Default,
            Size.NORMAL,
            translator,
            true
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                crawler.crawlForShows()
                reload(target)
            }
        }
    }

    private fun createSeriesTables(): CollapsablePanelGroup {
        return CollapsablePanelGroup(
            DynamicPanel.ROW_CONTENT_ID,
            60,
            createSeriesTable(modelCache.series.map { tupleConvertorService.convert(it) })
        )
    }

    private fun createSeriesTable(entry: List<Tuple>): CollapsablePanel {
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Series",
            createSeriesDataTable(entry),
            _visible = modelCache.series.isNotEmpty()
        )
    }

    private fun createSeriesDataTable(value: List<Tuple>): DynamicDataTable {
        return DynamicDataTable.get(
            CollapsablePanel.CONTENT_ID, value, rowsPerPage = 10, onTupleClick =
                { target, tuple ->
                    modelCache.chosenAnime = crawler.getDownloadables(tuple.getValueForColumn("Series").toString())
                    switchToPanel(SeriesPanel(), target)
                })
    }

    private fun createDataTable(): DynamicDataTable {
        val pairs = transmission.getAllTorrents()
        return DynamicDataTable.get(DynamicPanel.ROW_CONTENT_ID, pairs, 5, 50, translator, "Status")
    }

    private fun createForm(): DynamicFormComponent<Torrent> {
        return object : DynamicFormComponent<Torrent>(
            DynamicPanel.ROW_CONTENT_ID,
            "Torrent URL",
            CompoundPropertyModel.of(torrent),
            translator
        ) {

            override fun onModelChanged() {
                super.onModelChanged()
            }

            override fun onAfterSubmit(target: AjaxRequestTarget, typedModelObject: Torrent) {
                super.onAfterSubmit(target, typedModelObject)

            }
        }.addTextBox("torrentUrl", "Torrent")
            .addTextBox("siteUrl", "Anime site")
            .addTextBox("animeName", "Anime name")
    }
}