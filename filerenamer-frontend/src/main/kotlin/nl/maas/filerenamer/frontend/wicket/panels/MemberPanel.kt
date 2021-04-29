package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model

class MemberPanel(id: String, val data: FileMetaData) : Panel(id) {

    private val panelToHide =
        WebMarkupContainer("panelToHide")

    init {
        this.add(Label("memberName", data.name))
        add(Label("newMemberName", Model.of(data.newName)))
        val fileDetailPanel = FileDetailPanel("modalPanel", CompoundPropertyModel.of(data), panelToHide)
        panelToHide.add(fileDetailPanel)
        panelToHide.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setVisible(false)
        addOrReplace(panelToHide)
    }

    override fun onInitialize() {
        super.onInitialize()
        val memberpanel = this
        add(object : AjaxEventBehavior("click") {
            override fun onEvent(event: AjaxRequestTarget) {
                panelToHide.setVisible(!panelToHide.isVisible)
                event.add(memberpanel)
            }

        })
    }
}