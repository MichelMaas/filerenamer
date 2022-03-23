package nl.maas.filerenamer.frontend.wicket.pages

import nl.maas.filerenamer.frontend.wicket.components.DynamicTableComponent
import nl.maas.filerenamer.frontend.wicket.objects.Tuple
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.request.mapper.parameter.PageParameters

class OverviewPage(parameters: PageParameters) : BasePage(parameters) {

    override fun onBeforeRender() {
        super.onBeforeRender()
//        addOrReplace(object : ListView<File>("folders", modelCache.files.keys.toList()) {
//            override fun populateItem(item: ListItem<File>) {
//                item.add(Label("folder", item.modelObject.path))
//            }
//        })

        addOrReplace(object : DynamicTableComponent("folders", modelCache.files.keys.map {
            Tuple(mapOf(Pair("Name", it.name), Pair("path", it.path)))
        }.toMutableList()) {
            override fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {
                super.onTupleClick(target, tuple)
                modelCache.selectedFolder =
                    modelCache.files.keys.firstOrNull { tuple.columns["path"]!!.equals(it.path) }
                setResponsePage(DetailPage::class.java)
                target.add(this@OverviewPage)
            }
        })
    }
}