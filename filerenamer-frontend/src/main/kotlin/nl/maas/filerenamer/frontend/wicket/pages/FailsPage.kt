package nl.maas.filerenamer.frontend.wicket.pages

import nl.maas.filerenamer.frontend.wicket.panels.FailsPanel
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters

class FailsPage(parameters: PageParameters) : BasePage(parameters) {

    override fun onInitialize() {
        super.onInitialize()
        val repeatingView = RepeatingView("failsPanel")
        modelCache.searchResult.fileData.forEach { key, extrapolationResult ->
            repeatingView.add(
                FailsPanel(
                    repeatingView.newChildId(),
                    CompoundPropertyModel.of(extrapolationResult.failures)
                )
            )
        }
        add(repeatingView)
    }
}