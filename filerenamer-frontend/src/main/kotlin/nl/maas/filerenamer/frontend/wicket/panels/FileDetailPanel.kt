package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.wicket.Component
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IComponentInheritedModel

class FileDetailPanel(
    id: String,
    model: IComponentInheritedModel<FileMetaData>, panelToHide: Component
) : Panel(id, model) {
    init {
        var form = Form<FileMetaData>("fileDetailForm", model)
        form.add(TextField<String>("dirName"))
        form.add(TextField<String>("name"))
        form.add(TextField<String>("newName"))
        form.add(
            DropDownChoice<String>(
                "sequence",
                typedModelObject().potentialSequenceNumbers?.map { it.toString() })
        )

        form.add(object : AjaxSubmitLink("submit", form) {})
        add(form)
    }

    private fun typedModelObject(): FileMetaData = defaultModelObject as FileMetaData
}