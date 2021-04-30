package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model

class MemberPanel(id: String, val data: FileMetaData) : Panel(id, Model.of(data)) {

    private val panelToHide =
        WebMarkupContainer("panelToHide")

    init {
        outputMarkupId = true
        val fileDetailPanel = FileDetailPanel("modalPanel", CompoundPropertyModel.of(data), panelToHide)
        panelToHide.add(fileDetailPanel)
        panelToHide.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false)
        addOrReplace(panelToHide)
    }

    override fun onInitialize() {
        super.onInitialize()

    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        val memberpanel = this
        val button = object : AjaxLink<String>("button") {
            override fun onClick(target: AjaxRequestTarget) {
                panelToHide.setVisible(!panelToHide.isVisible)
                target.add(memberpanel)
            }
        }
        button.addOrReplace(Label("memberName", data.name))
        button.addOrReplace(Label("newMemberName", Model.of(data.newName)))
        addOrReplace(button)
    }
}