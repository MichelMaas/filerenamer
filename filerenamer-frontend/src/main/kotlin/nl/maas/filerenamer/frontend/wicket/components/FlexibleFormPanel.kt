package nl.maas.filerenamer.frontend.wicket.components

import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Fragment
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import java.io.Serializable
import kotlin.reflect.KProperty1

open class FlexibleFormPanel<T>(id: String, val formTitle: String, model: IComponentInheritedModel<T>) :
    Panel(id, model) {

    private val formComponents = RepeatingView("fragments")
    private val form = InnerForm(model)

    init {
        setOutputMarkupPlaceholderTag(true)
        add(form)
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        form.addOrReplace(Label("formTitle", Model.of(formTitle)))
        form.addOrReplace(formComponents)
    }

    fun addPlainText(id: String, label: String, value: IComponentInheritedModel<String>): FlexibleFormPanel<T> {
        formComponents.add(TextFragment(id, label, value))
        return this
    }

    fun <M> addTextBox(id: String, label: String): FlexibleFormPanel<T> {
        formComponents.add(TextBoxFragment<M>(id, label))
        return this
    }

    fun <M> addSelect(
        id: String,
        label: String,
        model: IComponentInheritedModel<M>,
        options: List<M>
    ): FlexibleFormPanel<T> {
        formComponents.addOrReplace(SelectFragment(id, label, model, Model.ofList(options)))
        return this
    }

    open fun onSubmit(target: AjaxRequestTarget) {
    }

    open fun onAfterSubmit(target: AjaxRequestTarget) {
    }

    open fun onBeforeCancel(target: AjaxRequestTarget) {
    }

    open fun onAfterCancel(target: AjaxRequestTarget) {
    }

    private inner class TextFragment(
        val propertyName: String,
        val label: String,
        val value: IComponentInheritedModel<String>
    ) :
        Fragment("${propertyName}-${formComponents.newChildId()}", "textFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(Label("text", value))
            addOrReplace(Label("textLabel", Model.of(label)))
        }
    }

    private inner class TextBoxFragment<M>(val propertyName: String, val label: String) :
        Fragment("${propertyName}-${formComponents.newChildId()}", "textBoxFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m: Serializable = readInstanceProperty(form.modelObject, propertyName)
            addOrReplace(
                TextField("textBox", Model.of(m)).setLabel(
                    Model.of(label)
                ).add(AttributeModifier("placeholder", label))
            )
        }
    }

    private inner class SelectFragment<M>(
        propertyName: String,
        val label: String,
        val model: IComponentInheritedModel<M>,
        val options: IModel<List<M>>
    ) :
        Fragment("${propertyName}-${formComponents.newChildId()}", "selectFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(
                DropDownChoice<M>(
                    "select", model,
                    options
                ).setLabel(Model.of(label))
            )
        }
    }

    private inner class InnerForm(model: IComponentInheritedModel<T>) : Form<T>("flexibleForm", model) {
        init {
            add(object : AjaxSubmitLink("submit", this) {
                override fun onSubmit(target: AjaxRequestTarget) {
                    super.onSubmit(target)
                    this@FlexibleFormPanel.onSubmit(target)
                    target.add(this@FlexibleFormPanel)
                }

                override fun onAfterSubmit(target: AjaxRequestTarget) {
                    super.onAfterSubmit(target)
                    this@FlexibleFormPanel.onAfterSubmit(target)
                    target.add(this@FlexibleFormPanel)
                }
            })
            add(object : AjaxLink<String>("reset", Model.of("Reset")) {
                override fun onClick(target: AjaxRequestTarget) {
                    this@FlexibleFormPanel.onBeforeCancel(target)
                    target.add(this@InnerForm)
                    clearInput()
                    this@FlexibleFormPanel.onAfterCancel(target)
                }

            })
        }

        override fun onSubmit() {
            super.onSubmit()
        }

        override fun onValidate() {
            super.onValidate()
        }

    }


    @Suppress("UNCHECKED_CAST")
    private fun <R : Serializable, I> readInstanceProperty(instance: I, propertyName: String): R {
        val property = instance!!::class.members
            // don't cast here to <Any, R>, it would succeed silently
            .first { it.name == propertyName } as KProperty1<Any, *>
        // force a invalid cast exception if incorrect type here
        return property.get(instance) as R
    }
}