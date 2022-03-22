package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.domain.ExtrapolationFailure
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.Model
import java.io.Serializable

class FailurePanel(id: String, val model: IComponentInheritedModel<ExtrapolationFailure<out Serializable>>) :
    Panel(id, model) {

    private val panelToHide =
        WebMarkupContainer("panelToHide")

    init {
        outputMarkupId = true
        val repeatingView = RepeatingView("modalPanel")
        model.`object`.failed.forEach {
            repeatingView.addOrReplace(
                FileDetailPanel(
                    repeatingView.newChildId(),
                    CompoundPropertyModel.of(it),
                    panelToHide
                )
            )
        }

        panelToHide.add(repeatingView)
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
        button.addOrReplace(Label("type"))
        button.addOrReplace(Label("folder", Model.of(model.`object`.failed[0].dirName)))
        addOrReplace(button)
    }
}