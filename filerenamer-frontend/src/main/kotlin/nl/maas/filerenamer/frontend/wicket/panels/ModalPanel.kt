package nl.maas.filerenamer.frontend.wicket.panels

import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog
import org.apache.wicket.model.IModel
import java.io.Serializable

open class ModalPanel<T : Serializable> internal constructor(
    id: String,
    model: IModel<T>
) :
    ModalDialog(id) {



    init {
        setOutputMarkupId(true)
        setDefaultModel(model)
        setOutputMarkupPlaceholderTag(true)
    }

    override fun open(target: AjaxRequestTarget): ModalDialog {
        val open = super.open(target)
        target.add(this)
        return open
    }

    override fun close(target: AjaxRequestTarget): ModalDialog {
        val close = super.close(target)
        target.add(this)
        return close
    }

}