package nl.maas.filerenamer.frontend.wicket.panels

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.wicket.framework.components.base.*
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model
import org.apache.wicket.spring.injection.annot.SpringBean

class DetailPanel : RIAPanel() {

    @SpringBean
    private lateinit var fileService: FileService

    @SpringBean
    private lateinit var modelCache: ModelCache

    @SpringBean
    private lateinit var translator: Translator

    private var parentFolder = false

    var selected: Tuple? = null
    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createPanel())
    }

    private fun createPanel(): Component {
        val editorRowName = "Editor"
        val dynamicPanel = DynamicPanel("panel")
            .addRow("Summary", 10, 2)
            .addRow("ParentFolder", 4, 8)
            .addRow(editorRowName, 12)
            .addRow("ButtonPanel", 2, 2, 8)
        dynamicPanel
            .addOrReplaceComponentToColumn("Summary", 0, createOverview())
            .addOrReplaceComponentToColumn("ParentFolder", 0, createParentFolderButton())
            .addOrReplaceComponentToColumn(
                editorRowName,
                0,
                createList(modelCache.getFilesForSelectedFolder())
            )
            .addOrReplaceComponentToColumn("ButtonPanel", 0, createApplyButton())
            .addOrReplaceComponentToColumn("ButtonPanel", 1, createBackButton())
        selected?.let {
            dynamicPanel.addRow(editorRowName, 6, 6)
                .addOrReplaceComponentToColumn(
                    editorRowName,
                    0,
                    createList(modelCache.files.get(modelCache.selectedFolder)!!.files, 10)
                ).addOrReplaceComponentToColumn(
                    editorRowName,
                    1,
                    createForm(toFileMetadata(it))
                )
        }
        return dynamicPanel

    }

    private fun createBackButton(): Component {
        return object :
            SimpleAjaxButton(
                DynamicPanel.ROW_CONTENT_ID,
                "Return",
                Buttons.Type.Secondary,
                Size.NORMAL,
                translator,
                true
            ) {
            override fun onClick(target: AjaxRequestTarget) {
                switchToPanel(OverviewPanel(), target)
                modelCache.selectedFolder = null
            }
        }
    }

    private fun createApplyButton(): Component {
        return object : SimpleAjaxButton(
            DynamicPanel.ROW_CONTENT_ID,
            "Apply",
            Buttons.Type.Primary,
            Size.NORMAL,
            translator,
            true
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                fileService.process(modelCache.files[modelCache.selectedFolder]!!.files)
                switchToPanel(OverviewPanel(), target)
            }
        }
    }

    private fun createParentFolderButton(): Component {
        return object :
            Switch(DynamicPanel.ROW_CONTENT_ID, Model.of(parentFolder), translator.translate("Use parent folder")) {

            override fun onUpdate(target: AjaxRequestTarget, modelObject: Boolean) {
                super.onUpdate(target, modelObject)
                parentFolder = !parentFolder
                modelCache.files[modelCache.selectedFolder]!!.files.forEach {
                    it.includeParentMapInName = parentFolder
                }
                target.add(this@DetailPanel)
            }

        }
    }

    private fun toFileMetadata(tuple: Tuple): FileMetaData {
        return modelCache.files.get(modelCache.selectedFolder)!!.files.first { it.name.equals(tuple.columns.values.first()) }
    }

    private fun createForm(fileMetaData: FileMetaData): Component {
        return object : DynamicFormComponent<FileMetaData>(
            DynamicPanel.ROW_CONTENT_ID,
            fileMetaData.name,
            CompoundPropertyModel.of(fileMetaData),
            translator
        ) {

            override fun <M> onSelectChanged(propertyName: String, value: M, target: AjaxRequestTarget) {
                super.onSelectChanged(propertyName, value, target)
                fileMetaData.sequence = value.toString()
            }

            override fun onAfterSubmit(target: AjaxRequestTarget, typedModelObject: FileMetaData) {
                super.onAfterSubmit(target, typedModelObject)
                selected = null
                fileService.update(
                    fileMetaData,
                    modelCache.files[modelCache.selectedFolder]!!.sequence.size,
                    modelCache.searchCriteria.sequence
                )
                target.add(this@DetailPanel)
            }

            override fun onAfterCancel(target: AjaxRequestTarget, typedModelObject: FileMetaData) {
                super.onAfterCancel(target, typedModelObject)
                selected = null
                target.add(this@DetailPanel)
            }

        }.addSelect(
            "sequence",
            "Sequence",
            fileMetaData.potentialSequenceNumbers.toList(),
            fileMetaData.potentialSequenceNumbers.firstOrNull() ?: StringUtils.EMPTY
        ).addTextBox("newName", "New name")

    }

    private fun createList(fileMetaData: List<FileMetaData>, maxRows: Int = 13): Component {
        val tuples = fileMetaData.map { Tuple("Name" to it.name, "New name" to it.newName) }.toMutableList()
        return DynamicDataTable.get(
            DynamicPanel.ROW_CONTENT_ID,
            tuples,
            maxRows,
            translator = translator,
            onTupleClick = { target, tuple ->
                selected = tuple
                target.add(this@DetailPanel)
            }, showClicked = true, translateContent = arrayOf("none")
        ).hover().sm().invertHeader()

    }

    private fun createOverview(): Component {
        return KeyValueView(
            DynamicPanel.ROW_CONTENT_ID,
            translator,
            "Folder" to "${modelCache.getSelectedFolderDisplayPath()}",
            "Errors" to "${modelCache.getSelectedResult().failures.failures.size}"
        )
    }

}