package nl.maas.filerenamer.frontend.wicket.panels

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.frontend.services.CrawlerService
import nl.maas.filerenamer.frontend.services.TransmissionService
import nl.maas.filerenamer.frontend.services.TupleConvertorService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.Torrent
import nl.maas.wicket.framework.components.base.*
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.link.AbstractLink
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

    private var torrentTable: DynamicDataTable? = null
    private var seriesTable: DynamicDataTable? = null

    private val torrent = Torrent()

    override fun onBeforeRender() {
        super.onBeforeRender()
        createScreen()
    }

    private fun createScreen() {
        val collapsables = createSeriesTables()
        val dynamicPanel =
            DynamicPanel("panel").addRow("seriesList", 2, 1, 9)
                .addOrReplaceComponentToColumn("seriesList", 0, createButtons())
                .addOrReplaceComponentToColumn("seriesList", 2, collapsables)
        addOrReplace(dynamicPanel)
    }

    private fun createButtons(): Component {
        return ButtonGroup(
            DynamicPanel.ROW_CONTENT_ID,
            createCleanButton(),
            createFetchButton()
        )
    }

    private fun createCleanButton(): AbstractLink {
        return object : SimpleAjaxButton(
            ComponentListView.CONTENT_ID,
            "Clean up",
            Buttons.Type.Default,
            Size.NORMAL,
            translator,
            true
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                transmission.cleanUp()
                torrentTable!!.update(transmission.getAllTorrents().map { tupleConvertorService.convert(it) }, target)
            }

        }
    }

    private fun createFetchButton(): AbstractLink {
        return object : SimpleAjaxButton(
            ComponentListView.CONTENT_ID,
            "Fetch",
            Buttons.Type.Default,
            Size.NORMAL,
            translator,
            true
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                crawler.crawlForShows()
                seriesTable!!.update(modelCache.series.map { tupleConvertorService.convert(it) }, target)
            }
        }
    }

    private fun createSeriesTables(): CollapsablePanelGroup {
        return CollapsablePanelGroup(
            DynamicPanel.ROW_CONTENT_ID,
            60,
            createTorrentsTableCollapsable(), createSeriesTableCollapsable()
        )
    }

    private fun createTorrentsTableCollapsable(): CollapsablePanel {
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Torrents",
            createDataTable()
        )
    }

    private fun createSeriesTableCollapsable(): CollapsablePanel {
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Series",
            createSeriesDataTable(),
            _visible = modelCache.series.isNotEmpty()
        )
    }

    private fun createSeriesDataTable(): DynamicDataTable {
        val value = modelCache.series.map { tupleConvertorService.convert(it) }
        seriesTable = DynamicDataTable.get(
            CollapsablePanel.CONTENT_ID, value, rowsPerPage = 20, onTupleClick =
                { target, tuple ->
                    modelCache.chosenAnime = crawler.getDownloadables(tuple.getValueForColumn("Series").toString())
                    switchToPanel(SeriesPanel(), target)
                })
        return seriesTable!!
    }

    private fun createDataTable(): DynamicDataTable {
        val tuples = transmission.getAllTorrents().map { tupleConvertorService.convert(it) }
        torrentTable = DynamicDataTable.get(
            DynamicPanel.ROW_CONTENT_ID,
            tuples,
            20,
            translator = translator,
            translateContent = arrayOf("Status")
        )
        return torrentTable!!
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