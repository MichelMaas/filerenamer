package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapButton
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.components.DynamicFormComponent
import nl.maas.filerenamer.frontend.wicket.components.DynamicTableComponent
import nl.maas.filerenamer.frontend.wicket.components.SingleDataViewPanel
import nl.maas.filerenamer.frontend.wicket.objects.Tuple
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.CheckBox
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import javax.inject.Inject

class DetailPage(parameters: PageParameters?) : BasePage(parameters) {

    @Inject
    lateinit var fileService: FileService

    private var formContainer = FormContainer()
    private var useParentName: Boolean = false

    override fun onInitialize() {
        super.onInitialize()
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        setUpDataOverview()
        setUpFileOverview()
        setUpForm()
        setUpApply()
    }

    private fun setUpApply() {
        val button = object : BootstrapButton("apply", Model.of("Apply"), Buttons.Type.Primary) {}
        button.add(object : AjaxEventBehavior("click") {
            override fun onEvent(target: AjaxRequestTarget) {
                fileService.process(modelCache.files[modelCache.selectedFolder]!!.files)
                modelCache.files = modelCache.files.filter { !it.key.equals(modelCache.selectedFolder) }
                modelCache.selectedFolder == null
                setResponsePage(OverviewPage::class.java)
                target.add(this@DetailPage)
            }
        })
        addOrReplace(button)
    }

    private fun setUpForm() {
        addOrReplace(formContainer)
    }

    private fun setUpFileOverview() {
        addOrReplace(object : DynamicTableComponent("table", createFileTuples()) {
            override fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {
                super.onTupleClick(target, tuple)
//                formContainer.fileMetaData =
                formContainer =
                    FormContainer(modelCache.files[modelCache.selectedFolder]!!.files.firstOrNull { it.name.equals(tuple.columns["Name"]) })
                target.add(this@DetailPage)
            }
        })
    }

    private fun createFileTuples(): MutableList<Tuple> {
        val result = modelCache.files[modelCache.selectedFolder]!!
        val files = result.files
        return files.map {
            Tuple(
                mapOf(
                    Pair("Name", it.name),
                    Pair("Proposed name", it.newName ?: StringUtils.EMPTY),
                    Pair("Successful", if (result.failures.hasFailures(it)) "NO" else "YES")
                )
            )
        }.toMutableList()
    }

    private fun setUpDataOverview() {
        useParentName =
            modelCache.files[modelCache.selectedFolder]!!.files.firstOrNull()?.includeParentMapInName ?: false
        val checkBox = CheckBox("includeParent", Model.of(useParentName))
        checkBox.add(object : OnChangeAjaxBehavior() {
            override fun onUpdate(target: AjaxRequestTarget) {
                modelCache.files[modelCache.selectedFolder]!!.files.forEach {
                    it.includeParentMapInName = checkBox.modelObject
                    target.add(this@DetailPage)
                }
            }

        })
        addOrReplace(checkBox)
        addOrReplace(Label("includeParentLabel", "Use parent folder in name"))
        addOrReplace(object : ListView<Pair<String, String>>("overview", createOverviewTuples()) {
            override fun populateItem(item: ListItem<Pair<String, String>>) {
                item.addOrReplace(SingleDataViewPanel("data", item.modelObject))
            }
        })
    }

    private fun createOverviewTuples(): List<Pair<String, String>> {
        val result = modelCache.files[modelCache.selectedFolder]!!
        return listOf(
            Pair("Folder", modelCache.selectedFolder!!.name),
            Pair("Number of files", result.files.size.toString()),
            Pair("Number of failures", result.failures.failures.values.flatMap { it }.size.toString())
        )
    }

    private inner class FormContainer(var fileMetaData: FileMetaData? = null) : WebMarkupContainer("formContainer") {
        override fun onInitialize() {
            super.onInitialize()
            outputMarkupId = true
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            if (fileMetaData == null) {
                addOrReplace(WebMarkupContainer("form"))
            } else {
                addOrReplace(
                    object : DynamicFormComponent<FileMetaData>(
                        "form",
                        fileMetaData!!.name,
                        CompoundPropertyModel.of(fileMetaData!!)
                    ) {
                        override fun onSubmit(target: AjaxRequestTarget) {
                            super.onSubmit(target)
                            formContainer = FormContainer()
                            target.add(this@DetailPage)
                        }
                    }.addPlainText("name", "Name", CompoundPropertyModel.of(fileMetaData!!.name))
                        .addPlainText("newName", "Proposed name", CompoundPropertyModel.of(fileMetaData!!.newName))
                        .addTextBox("newName", "New name")
                )
            }
        }
    }

}