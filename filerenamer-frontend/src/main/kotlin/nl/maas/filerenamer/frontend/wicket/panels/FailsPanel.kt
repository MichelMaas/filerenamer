package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.domain.ExtrapolationFailure
import nl.maas.filerenamer.domain.ExtrapolationFailures
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IComponentInheritedModel
import java.io.Serializable

class FailsPanel(id: String, val model: IComponentInheritedModel<ExtrapolationFailures>) : Panel(id, model) {

    override fun onBeforeRender() {
        super.onBeforeRender()
        addMembers()
    }

    fun addMembers() {
        val fileData = (defaultModelObject as ExtrapolationFailures).failures
        val folders =
            FolderMapView(
                "folders",
                fileData
            )
        add(folders)
    }


    private inner class FolderMapView(
        id: String,
        val fileData: MutableMap<String, MutableList<ExtrapolationFailure<out Serializable>>>
    ) :
        ListView<String>(id, fileData.keys.toMutableList()) {

        override fun populateItem(item: ListItem<String>) {
            val key = item.modelObject

            var hidingContainer = WebMarkupContainer("hidingContainer")
            hidingContainer.add(FailureListView("failures", fileData[key]!!)).setOutputMarkupPlaceholderTag(true)
            hidingContainer.setVisible(false)
            val detailLink: AjaxLink<Void> = object : AjaxLink<Void>("hidingButton") {
                init {
                    add(Label("folderTitle", key.substringAfterLast("/")))
                }

                override fun onClick(target: AjaxRequestTarget) {
                    hidingContainer.setVisible(!hidingContainer.isVisible())
                    target.add(hidingContainer)
                }
            }
            item.add(detailLink)
            item.add(hidingContainer)
        }

        private inner class FailureListView(id: String, data: MutableList<ExtrapolationFailure<out Serializable>>) :
            ListView<ExtrapolationFailure<out Serializable>>(id, data) {
            override fun populateItem(item: ListItem<ExtrapolationFailure<out Serializable>>) {
                item.add(FailurePanel("failure", CompoundPropertyModel.of(item.modelObject)))
            }

        }
    }
}