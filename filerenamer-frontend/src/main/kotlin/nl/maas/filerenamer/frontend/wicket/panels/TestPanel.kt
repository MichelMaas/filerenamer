package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.frontend.wicket.components.DynamicFormComponent
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.CompoundPropertyModel
import java.io.Serializable

class TestPanel : Panel {
    constructor(id: String) : super(id)

    override fun onBeforeRender() {
        super.onBeforeRender()
        val model = CompoundPropertyModel.of(StringHolder("Test"))
        val flexibleForm = object : DynamicFormComponent<StringHolder>("content", "Test form", model) {
            override fun onSubmit(target: AjaxRequestTarget) {
                super.onSubmit(target)
                println("Form submitting")
            }

            override fun onAfterSubmit(target: AjaxRequestTarget) {
                super.onAfterSubmit(target)
                println("Form submited")
            }

            override fun onBeforeCancel(target: AjaxRequestTarget) {
                super.onBeforeCancel(target)
                println("Form cancelling")
            }
        }
        flexibleForm.addPlainText("string", "Stringholder: ", CompoundPropertyModel.of(model.`object`.string))
            .addTextBox("string", "String editor")
        add(flexibleForm)
    }

    data class StringHolder(var string: String) : Serializable {
    }
}