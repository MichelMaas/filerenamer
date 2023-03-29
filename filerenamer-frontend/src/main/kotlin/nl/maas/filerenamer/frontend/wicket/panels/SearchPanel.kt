package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.domain.enums.SEQUENCE
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.wicket.framework.components.base.DynamicFormComponent
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.Translator
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.spring.injection.annot.SpringBean

class SearchPanel : RIAPanel() {
    @SpringBean
    private lateinit var modelCache: ModelCache

    @SpringBean
    private lateinit var translator: Translator

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createSearchForm())
    }

    private fun createSearchForm(): DynamicFormComponent<SearchCriteria> {
        return object : DynamicFormComponent<SearchCriteria>(
            "panel",
            "Search",
            CompoundPropertyModel.of(modelCache.searchCriteria),
            translator,
            this
        ) {
            override fun onAfterSubmit(target: AjaxRequestTarget, typedModelObject: SearchCriteria) {
                super.onAfterSubmit(target, typedModelObject)
            }

            override fun onSubmitCompleted(target: AjaxRequestTarget, typedModelObject: SearchCriteria) {
                super.onSubmitCompleted(target, typedModelObject)
                switchToPanel(OverviewPanel(), target)
            }
        }.addTextBox("folderPath", "Path")
            .addSelect("sequence", "Sequence", SEQUENCE.values().toList(), modelCache.searchCriteria.sequence)
    }

}