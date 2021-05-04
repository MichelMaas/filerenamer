package nl.maas.filerenamer.frontend.wicket.pages

import nl.maas.filerenamer.frontend.services.FileFinderService
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import javax.inject.Inject

class ConfirmationPage(parameters: PageParameters) : BasePage(parameters) {

    @Inject
    private lateinit var fileFinderService: FileFinderService

    override fun onBeforeRender() {
        super.onBeforeRender()
        val searchResult = modelCache.searchResult
        val changes = RepeatingView("panel")
        searchResult.fileData.values.flatMap { result -> result.files }.forEach { file ->
            changes.add(
                Label(
                    changes.newChildId(),
                    "${file.file.absolutePath} -> ${file.newName}"
                )
            )
        }
        val footer = WebMarkupContainer("footerbar").add(object : AjaxLink<String>("confirm", Model.of("Confirm")) {
            override fun onClick(target: AjaxRequestTarget?) {
                fileFinderService.commitChanges(searchResult.fileData.values.flatMap { it.files })
            }
        }).add(
            AttributeModifier.replace(
                "class",
                "navbar  navbar-dark  bd-navbar sticky-bottom bg-dark justify-content-center"
            )
        )
        add(changes, footer)
    }
}