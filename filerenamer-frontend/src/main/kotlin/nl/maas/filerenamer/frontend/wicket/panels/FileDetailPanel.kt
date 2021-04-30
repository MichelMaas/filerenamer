package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.Model

class FileDetailPanel(
    id: String,
    model: IComponentInheritedModel<FileMetaData>, panelToHide: Component
) : Panel(id, model) {
    init {
        var form = Form<FileMetaData>("fileDetailForm", model)
        form.add(
            TextField<String>(
                "dirName",
                Model.of("${typedModelObject().dirName}: ${typedModelObject().sequence}")
            )
        )
        form.add(TextField<String>("name"))
        form.add(TextField<String>("newName"))
        form.add(
            DropDownChoice<String>(
                "sequence",
                typedModelObject().potentialSequenceNumbers?.map { it.toString() })
        )

        form.add(object : AjaxSubmitLink("submit", form) {
            override fun onAfterSubmit(target: AjaxRequestTarget) {
                super.onAfterSubmit(target)
                panelToHide.setVisible(false)
                target.add(panelToHide.parent)
            }
        })

        form.add(object : AjaxLink<String>("cancel", Model.of("Cancel")) {
            override fun onClick(target: AjaxRequestTarget) {
                panelToHide.setVisible(false)
                target.add(panelToHide.parent)
            }

        })
        add(form)
    }

    private fun typedModelObject(): FileMetaData = defaultModelObject as FileMetaData
}