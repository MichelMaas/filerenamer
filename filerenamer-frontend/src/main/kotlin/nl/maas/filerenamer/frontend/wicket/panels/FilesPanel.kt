package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import org.apache.wicket.AttributeModifier
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.IModel

class FilesPanel(id: String, model: IModel<SearchResult>) : Panel(id, model) {
    init {
        addMembers()
    }

    fun addMembers() {
        val folders = RepeatingView("folders")
        val fileData = (defaultModelObject as SearchResult).fileData
        fileData.keys.forEach { key ->
            folders.add(
                Label(folders.newChildId(), key.substringAfterLast("/")).add(
                    AttributeModifier.replace(
                        "class",
                        "folderName"
                    )
                )
            )
                .add(createMemberPanels(folders.newChildId(), fileData.get(key)!!))
        }
        add(folders)
    }

    fun createMemberPanels(id: String, files: List<FileMetaData>): RepeatingView {
        val fileMembers = RepeatingView(id)
        files.forEach { file -> fileMembers.add(MemberPanel(fileMembers.newChildId(), file)) }
        return fileMembers
    }
}