package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.caches.GoogleTranslator
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicFormComponent
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.base.DynamicPanel.Companion.ROW_CONTENT_ID
import nl.maas.wicket.framework.components.base.KeyValueView
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.pages.BasePage
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.spring.injection.annot.SpringBean

class DetailPage : BasePage<ModelCache>(
    ContextProvider.ctx.getBean(ModelCache::class.java),
    ContextProvider.ctx.getBean(GoogleTranslator::class.java),
    brandName = "File renamer"
) {

    @SpringBean
    lateinit var fileService: FileService

    var selected: Tuple? = null
    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createPanel())
    }

    private fun createPanel(): Component {
        val editorRowName = "Editor"
        val dynamicPanel = DynamicPanel("panel")
            .addRow("Summary", 8, 4)
            .addRow("ParentFolder", 4, 8)
            .addRow(editorRowName, 12)
            .addRow("ButtonPanel", 2, 2, 8)
        dynamicPanel
            .addOrReplaceComponentToColumn("Summary", 0, createOverview())
            .addOrReplaceComponentToColumn("ParentFolder", 0, createParentFolderButton())
            .addOrReplaceComponentToColumn(
                editorRowName,
                0,
                createList(modelCache.files.get(modelCache.selectedFolder)!!.files)
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
            SimpleAjaxButton(ROW_CONTENT_ID, "Return", Buttons.Type.Secondary, Size.NORMAL, translator, true) {
            override fun onClick(target: AjaxRequestTarget) {
                modelCache.selectedFolder = null
                setResponsePage(OverviewPage::class.java)
            }
        }
    }

    private fun createApplyButton(): Component {
        return object : SimpleAjaxButton(ROW_CONTENT_ID, "Apply", Buttons.Type.Primary, Size.NORMAL, translator, true) {
            override fun onClick(target: AjaxRequestTarget) {
                fileService.process(modelCache.files[modelCache.selectedFolder]!!.files)
                modelCache.refresh()
                setResponsePage(OverviewPage::class.java)
            }
        }
    }

    private fun createParentFolderButton(): Component {
        return object : SimpleAjaxButton(
            ROW_CONTENT_ID,
            "Use parent folder",
            Buttons.Type.Secondary,
            Size.SMALL,
            translator,
            true
        ) {
            override fun onClick(target: AjaxRequestTarget) {
                modelCache.files[modelCache.selectedFolder]!!.files.forEach {
                    it.includeParentMapInName = !it.includeParentMapInName
                }
                target.add(this@DetailPage)
            }

        }
    }

    private fun toFileMetadata(tuple: Tuple): FileMetaData {
        return modelCache.files.get(modelCache.selectedFolder)!!.files.first { it.name.equals(tuple.columns.values.first()) }
    }

    private fun createForm(fileMetaData: FileMetaData): Component {
        return object : DynamicFormComponent<FileMetaData>(
            ROW_CONTENT_ID,
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
                fileService.update(fileMetaData, modelCache.files[modelCache.selectedFolder]!!.sequence.size)
                target.add(this@DetailPage)
            }

            override fun onAfterCancel(target: AjaxRequestTarget, typedModelObject: FileMetaData) {
                super.onAfterCancel(target, typedModelObject)
                selected = null
                target.add(this@DetailPage)
            }

        }.addSelect(
            "sequence",
            "Sequence",
            fileMetaData.potentialSequenceNumbers.toList(),
            fileMetaData.potentialSequenceNumbers.firstOrNull() ?: StringUtils.EMPTY
        ).addTextBox("newName", "New name")

    }

    private fun createList(fileMetaData: List<FileMetaData>, maxRows: Int = 15): Component {
        return DynamicDataTable.get(
            ROW_CONTENT_ID,
            fileMetaData.map { Tuple("Name" to it.name, "New name" to it.newName) }.toMutableList(),
            maxRows,
            { target, tuple ->
                selected = tuple
                target.add(this@DetailPage)
            }).hover().light().sm()

    }

    private fun createOverview(): Component {
        return KeyValueView(
            ROW_CONTENT_ID,
            translator,
            "Folder" to modelCache.selectedFolder!!.path.substringAfter(modelCache.searchCriteria.folderPath),
            "Errors" to modelCache.files.get(modelCache.selectedFolder)!!.failures.failures.size
        )
    }

    override fun createNavBarButtons(): Array<NavbarButton<*>> {
        return ButtonTypes.values().map { it.toNavBarButton() }.toTypedArray()
    }

    override fun isButtonActive(button: BaseNavbarButton): Boolean {
        return true
    }
}