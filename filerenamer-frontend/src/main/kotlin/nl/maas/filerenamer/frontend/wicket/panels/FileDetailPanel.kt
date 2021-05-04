package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.services.FileFinderService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.components.DynamicFormComponent
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IComponentInheritedModel
import javax.inject.Inject

class FileDetailPanel(
    id: String,
    val model: IComponentInheritedModel<FileMetaData>, val panelToHide: Component
) : Panel(id, model) {

    @Inject
    private lateinit var modelCache: ModelCache

    @Inject
    private lateinit var finderService: FileFinderService

    override fun onBeforeRender() {
        super.onBeforeRender()
        val form = object :
            DynamicFormComponent<FileMetaData>("form", "${model.`object`.dirName}: ${model.`object`.sequence}", model) {

            override fun onAfterSubmit(target: AjaxRequestTarget) {
                super.onAfterSubmit(target)
                panelToHide.setVisible(false)
                modelCache.searchResult = finderService.updateFiles(modelCache.searchResult)
                target.add(panelToHide.parent)
            }

            override fun onAfterCancel(target: AjaxRequestTarget) {
                super.onAfterCancel(target)
                panelToHide.setVisible(false)
                target.add(panelToHide.parent)
            }
        }
        form.addPlainText("name", "Name: ", CompoundPropertyModel.of(model.`object`.name))
            .addSelect("sequence", "Sequence: ", model.`object`.potentialSequenceNumbers!!.map { it.toString() })
            .addTextBox("newName", "New name: ")
        addOrReplace(form)
    }
}