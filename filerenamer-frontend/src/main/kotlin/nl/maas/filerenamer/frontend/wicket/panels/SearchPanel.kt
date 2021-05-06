package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.SequenceExtrapolator.Companion.SEQUENCE
import nl.maas.filerenamer.frontend.services.FileFinderService
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.filerenamer.frontend.wicket.pages.SearchPage
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IModel
import javax.inject.Inject

class SearchPanel : Panel {
    constructor(id: String, model: IModel<SearchCriteria>) : super(id, model)

    @Inject
    lateinit var fileFinderService: FileFinderService

    @Inject
    lateinit var modelCache: ModelCache

    override fun onInitialize() {
        super.onInitialize()
        val form = SearchForm("searchForm", CompoundPropertyModel.of(typedModel()))
        form.add(TextField<String>("path"))
        val select =
            DropDownChoice<SEQUENCE>("sequenceType", SEQUENCE.values().asList())
        form.add(select)
//        add(Submit(form))
        addOrReplace(form)
    }

    fun findFiles(): SearchResult {
        val foundFiles = fileFinderService.findFilesToRename(typedModel().`object`)
        var searchResult = SearchResult(typedModel().`object`.sequenceType, foundFiles)
        return searchResult
    }

    fun typedModel() = defaultModel as IModel<SearchCriteria>


    private inner class SearchForm(id: String, model: IModel<SearchCriteria>) : Form<SearchCriteria>(id, model) {

        override fun onInitialize() {
            super.onInitialize()
            add(Submit())
        }

        private inner class Submit :
            AjaxFormSubmitBehavior(this, "submit") {
            override fun onSubmit(target: AjaxRequestTarget) {
                super.onSubmit(target)
                println(defaultModel.`object`.toString())
                val searchPage = findParent(SearchPage::class.java)
                modelCache.searchResult = this@SearchPanel.findFiles()
                searchPage.findNavButton(ButtonTypes.FILES)?.setEnabled(true)
                target.add(searchPage)
            }
        }


    }

}