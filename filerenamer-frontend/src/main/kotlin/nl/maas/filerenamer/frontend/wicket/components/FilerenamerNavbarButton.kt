package nl.maas.filerenamer.frontend.wicket.components

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import nl.maas.filerenamer.frontend.wicket.pages.BasePage
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import java.io.Serializable

class FilerenamerNavbarButton(val buttonType: ButtonTypes) :
    NavbarButton<Void>(buttonType.pageClass, Model.of(buttonType.name)) {

    init {
        setIconType(buttonType.iconType)
    }

    override fun onInitialize() {
        super.onInitialize()
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        setEnabled(ButtonTypes.FIND.equals(buttonType) || findParent(BasePage::class.java).isModelFilled(buttonType))
    }

    fun enable(): FilerenamerNavbarButton {
        setEnabled(true)
        return this
    }

    fun disable(): FilerenamerNavbarButton {
        setEnabled(false)
        return this
    }

}