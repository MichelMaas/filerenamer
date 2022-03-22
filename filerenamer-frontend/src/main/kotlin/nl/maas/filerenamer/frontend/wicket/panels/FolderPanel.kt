package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel


class FolderPanel(id: String, model: IModel<SearchResult>) : Panel(id, model) {
    init {
        addMembers()
    }

    fun addMembers() {
        val fileData = (defaultModelObject as SearchResult).fileData
        val folders =
            FileMapView("folders", fileData.map { it.key to it.value.files.toMutableList() }.toMap().toMutableMap())
        add(folders)
    }


    private inner class FileMapView(id: String, val fileData: MutableMap<String, MutableList<FileMetaData>>) :
        ListView<String>(id, fileData.keys.toMutableList()) {

        override fun populateItem(item: ListItem<String>) {
            val key = item.modelObject

            var hidingContainer = WebMarkupContainer("hidingContainer")
            hidingContainer.add(FileMetaDataListView("folders", fileData[key]!!)).setOutputMarkupPlaceholderTag(true)
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

        private inner class FileMetaDataListView(id: String, data: MutableList<FileMetaData>) :
            ListView<FileMetaData>(id, data) {
            override fun populateItem(item: ListItem<FileMetaData>) {
                item.add(MemberPanel("folder", item.modelObject))
            }

        }
    }
}