package nl.maas.filerenamer.frontend.wicket.components

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
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import java.io.Serializable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

open class DynamicFormComponent<T>(id: String, val formTitle: String, model: IComponentInheritedModel<T>) :
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

    fun addPlainText(id: String, label: String, value: IComponentInheritedModel<String>): DynamicFormComponent<T> {
        formComponents.add(TextFragment(id, label, value))
        return this
    }

    fun addTextBox(id: String, label: String): DynamicFormComponent<T> {
        formComponents.add(TextBoxFragment<Serializable>(id, label))
        return this
    }

    fun <M : Serializable> addSelect(
        id: String,
        label: String,
        options: List<M>
    ): DynamicFormComponent<T> {
        formComponents.addOrReplace(SelectFragment(id, label, Model.ofList(options)))
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

    private inner class TextBoxFragment<M : Serializable>(val propertyName: String, val label: String) :
        Fragment("propertyName", "textBoxFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m: M = readInstanceProperty(form.modelObject, propertyName)
            addOrReplace(object : TextField<M>("textBox", CompoundPropertyModel.of(m)) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.declaredMemberProperties.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(form.modelObject, modelObject)
                        }
                }
            })
            addOrReplace(Label("textBoxLabel", label))
        }
    }

    private inner class SelectFragment<M : Serializable>(
        val propertyName: String,
        val label: String,
        val options: IModel<List<M>>
    ) :
        Fragment("${propertyName}-${formComponents.newChildId()}", "selectFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m: Serializable = readInstanceProperty(form.modelObject, propertyName)
            addOrReplace(object : DropDownChoice<Serializable>("select", CompoundPropertyModel.of(m), options) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.declaredMemberProperties.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(form.modelObject, modelObject)
                        }
                }
            })
            addOrReplace(Label("selectLabel", label))
        }
    }

    private inner class InnerForm(model: IComponentInheritedModel<T>) : Form<T>("dynamicForm", model) {
        init {
            add(object : AjaxSubmitLink("submit", this) {
                override fun onSubmit(target: AjaxRequestTarget) {
                    super.onSubmit(target)
                    this@DynamicFormComponent.onSubmit(target)
                    target.add(this@DynamicFormComponent)
                }

                override fun onAfterSubmit(target: AjaxRequestTarget) {
                    super.onAfterSubmit(target)
                    this@DynamicFormComponent.onAfterSubmit(target)
                    target.add(this@DynamicFormComponent)
                }
            })
            add(object : AjaxLink<String>("reset", Model.of("Reset")) {
                override fun onClick(target: AjaxRequestTarget) {
                    this@DynamicFormComponent.onBeforeCancel(target)
                    target.add(this@InnerForm)
                    clearInput()
                    this@DynamicFormComponent.onAfterCancel(target)
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