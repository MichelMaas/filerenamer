package nl.maas.filerenamer.frontend.wicket.pages

import com.giffing.wicket.spring.boot.context.scan.WicketHomePage
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.domain.enums.SEQUENCE
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.caches.GoogleTranslator
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.wicket.framework.components.base.DynamicFormComponent
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.pages.BasePage
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.spring.injection.annot.SpringBean

@WicketHomePage
open class SearchPage() :
    BasePage<ModelCache>(
        ContextProvider.ctx.getBean(ModelCache::class.java),
        ContextProvider.ctx.getBean(GoogleTranslator::class.java)
    ) {

    private var searchCriteria: SearchCriteria = SearchCriteria()

    @SpringBean
    private lateinit var fileService: FileService

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createSearchForm())
    }

    private fun createSearchForm(): DynamicFormComponent<SearchCriteria> {
        return object : DynamicFormComponent<SearchCriteria>(
            "panel",
            "Search",
            CompoundPropertyModel.of(searchCriteria),
            translator,
            this
        ) {
            override fun onSubmit(target: AjaxRequestTarget, typedModelObject: SearchCriteria) {
                super.onSubmit(target, typedModelObject)
                modelCache.files = fileService.findAndProcessFrom(
                    searchCriteria.folderPath,
                    searchCriteria.sequence
                )
                setResponsePage(OverviewPage::class.java)
            }
        }.addTextBox("folderPath", "Path")
            .addSelect("sequence", "Sequence", SEQUENCE.values().toList(), searchCriteria.sequence)
    }

    override fun createNavBarButtons(): Array<NavbarButton<*>> {
        return ButtonTypes.values().map { it.toNavBarButton() }.toTypedArray()
    }

    override fun isButtonActive(button: BaseNavbarButton): Boolean {
        return !button.buttonType.equals(ButtonTypes.SEARCH)
    }
}