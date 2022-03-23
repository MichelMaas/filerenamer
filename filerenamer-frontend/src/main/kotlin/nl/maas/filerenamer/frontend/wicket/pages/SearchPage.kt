package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator.Companion.SEQUENCE
import nl.maas.filerenamer.frontend.services.FileFinderService
import nl.maas.filerenamer.frontend.wicket.components.DynamicFormComponent
import nl.maas.filerenamer.frontend.wicket.objects.Filter
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import javax.inject.Inject

@WicketHomePage
class SearchPage(parameters: PageParameters) : BasePage(parameters) {

    @Inject
    lateinit var fileFinderService: FileFinderService

    override fun onBeforeRender() {
        super.onBeforeRender()
        setForm()
    }

    private fun setForm() {
        val form =
            object : DynamicFormComponent<Filter>("panel", "Search", CompoundPropertyModel.of(modelCache.filter)) {
                override fun onSubmit(target: AjaxRequestTarget) {
                    super.onSubmit(target)
                    modelCache.files =
                        fileFinderService.findAndProcessFrom(modelCache.filter.path, modelCache.filter.sequence)
                    target.add(this@SearchPage)
                }
            }.addTextBox("path", "Path").addSelect("sequence", "Sequence", SEQUENCE.values().asList())
        addOrReplace(form)
    }

}