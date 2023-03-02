package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.components.DynamicTableComponent
import nl.maas.filerenamer.frontend.wicket.objects.Tuple
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicPanel.Companion.ROW_CONTENT_ID
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.request.mapper.parameter.PageParameters
import javax.inject.Inject

class OverviewPage(parameters: PageParameters) : BasePage(parameters) {

    @Inject
    lateinit var fileService: FileService

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createDynamicPanel())
    }

    private fun createDynamicPanel(): DynamicPanel {
        return DynamicPanel("panel").addRows("table" to intArrayOf(12), "button" to intArrayOf(12))
            .addOrReplaceComponentToColumn("table", 0, setUpTable())
            .addOrReplaceComponentToColumn("button", 0, setUpApply())
    }

    private fun setUpTable(): DynamicTableComponent {
        return object : DynamicTableComponent(ROW_CONTENT_ID, modelCache.files.keys.map {
            Tuple(mapOf(Pair("Name", it.name), Pair("path", it.path)))
        }.toMutableList()) {
            override fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {
                super.onTupleClick(target, tuple)
                modelCache.selectedFolder =
                    modelCache.files.keys.firstOrNull { tuple.columns["path"]!!.equals(it.path) }
                setResponsePage(DetailPage::class.java)
                target.add(this@OverviewPage)
            }
        }
    }

    private fun setUpApply(): SimpleAjaxButton {
        val button =
            object : SimpleAjaxButton(ROW_CONTENT_ID, "Apply", Buttons.Type.Primary, Size.LARGE, block = true) {
                override fun onClick(target: AjaxRequestTarget) {
                    val filtered = modelCache.files.filter { it.value.failures.isEmpty() }
                    filtered.keys.forEach {
                        val extrapolationResult = modelCache.files[it]!!
                        fileService.process(extrapolationResult.files)
                    }
                    modelCache.files = filtered
                    target.add(this@OverviewPage)
                }
            }
        return button
    }
}