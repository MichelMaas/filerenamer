package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapButton
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.components.DynamicTableComponent
import nl.maas.filerenamer.frontend.wicket.objects.Tuple
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.form.Button
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import javax.inject.Inject

class OverviewPageBCK(parameters: PageParameters) : BasePage(parameters) {

    @Inject
    lateinit var fileService: FileService

    override fun onBeforeRender() {
        super.onBeforeRender()
        setUpTable()
        setUpApply()
    }

    private fun setUpTable() {
        addOrReplace(object : DynamicTableComponent("folders", modelCache.files.keys.map {
            Tuple(mapOf(Pair("Name", it.name), Pair("path", it.path)))
        }.toMutableList()) {
            override fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {
                super.onTupleClick(target, tuple)
                modelCache.selectedFolder =
                    modelCache.files.keys.firstOrNull { tuple.columns["path"]!!.equals(it.path) }
                setResponsePage(DetailPage::class.java)
                target.add(this@OverviewPageBCK)
            }
        })
    }

    private fun setUpApply(): Button {
        val button = object : BootstrapButton("apply", Model.of("Apply"), Buttons.Type.Primary) {}
        button.add(object : AjaxEventBehavior("click") {
            override fun onEvent(target: AjaxRequestTarget) {
                val filtered = modelCache.files.filter { it.value.failures.isEmpty() }
                filtered.keys.forEach {
                    val extrapolationResult = modelCache.files[it]!!
                    fileService.process(extrapolationResult.files)
                }
                modelCache.files = filtered
                target.add(this@OverviewPageBCK)
            }
        })
        return button
    }
}