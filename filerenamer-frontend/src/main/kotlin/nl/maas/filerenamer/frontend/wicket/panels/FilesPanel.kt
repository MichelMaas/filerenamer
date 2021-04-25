package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.services.FileFinderService
import org.apache.wicket.AttributeModifier
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.RepeatingView
import javax.inject.Inject

class FilesPanel : Panel {
    constructor(id: String) : super(id)

    @Inject
    lateinit var fileFinder: FileFinderService

    lateinit var filesIn: Map<String, List<FileMetaData>>

    init {
        fetchFiles()
        addMembers()
    }

    fun fetchFiles() {
        filesIn = fileFinder.findFilesToRename("/shares/anime")
    }

    fun addMembers() {
        val folders = RepeatingView("folders")
        filesIn.keys.forEach { key ->
            folders.add(
                Label(folders.newChildId(), key.substringAfterLast("/")).add(
                    AttributeModifier.replace(
                        "class",
                        "folderName"
                    )
                )
            )
                .add(createMemberPanels(folders.newChildId(), filesIn.get(key)!!))
        }
        add(folders)
    }

    fun createMemberPanels(id: String, files: List<FileMetaData>): RepeatingView {
        val fileMembers = RepeatingView(id)
        files.forEach { file -> fileMembers.add(MemberPanel(fileMembers.newChildId(), file)) }
        return fileMembers
    }
}