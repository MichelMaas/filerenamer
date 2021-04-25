package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.IModel

class SearchPanel : Panel {
    constructor(id: String, model: IComponentInheritedModel<SearchCriteria>) : super(id, model)

    override fun onInitialize() {
        super.onInitialize()
        val form = SearchForm("searchForm", typedModel())
        form.add(TextField<String>("path"))
        val select =
            DropDownChoice<SearchCriteria.SequenceType>("sequenceType", SearchCriteria.SequenceType.values().asList())
        form.add(select)
        addOrReplace(form)
    }

    fun typedModel() = defaultModel as IModel<SearchCriteria>

    private class SearchForm(id: String, model: IModel<SearchCriteria>) : Form<SearchCriteria>(id, model) {
        override fun onSubmit() {
            super.onSubmit()
            println(defaultModel.`object`.toString())
        }
    }
}