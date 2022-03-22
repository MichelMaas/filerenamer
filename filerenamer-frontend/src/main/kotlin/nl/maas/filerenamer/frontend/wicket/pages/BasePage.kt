package nl.maas.filerenamer.frontend.wicket.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.components.FilerenamerNavbarButton
import nl.maas.filerenamer.frontend.wicket.objects.enums.ButtonTypes
import org.apache.wicket.AttributeModifier
import org.apache.wicket.markup.html.GenericWebPage
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

open class BasePage(parameters: PageParameters?) : GenericWebPage<Void?>(parameters) {
    lateinit var navbar: Navbar

    @Inject
    lateinit var modelCache: ModelCache

    protected fun newNavbar(markupId: String?): Navbar {
        navbar = Navbar(markupId)
        navbar!!.position = Navbar.Position.TOP
        navbar!!.add(NavbarProvider())
        navbar!!.setBrandName(Model.of("Filerenamer"))
        val navbarButtons: Array<NavbarButton<*>> = Arrays.stream(ButtonTypes.values()).map { button: ButtonTypes? ->
            FilerenamerNavbarButton(
                button!!
            )
        }.collect(Collectors.toList()).toTypedArray()
        navbar!!.addComponents(NavbarComponents.transform(Navbar.ComponentPosition.LEFT, *navbarButtons))
        navbar.setOutputMarkupId(true)
        return navbar as Navbar
    }

    fun findNavButton(type: ButtonTypes): FilerenamerNavbarButton? {
        return navbar?.filter { component -> component.javaClass.isAssignableFrom(FilerenamerNavbarButton::class.java) }
            ?.map { it as FilerenamerNavbarButton }?.firstOrNull { type.equals(it.buttonType) }
    }


    init {
        add(newNavbar("navbar"))
        setOutputMarkupId(true)
    }

    fun isButtonActive(type: ButtonTypes): Boolean {
        var filled = when (type) {
            ButtonTypes.FILES, ButtonTypes.CONFIRM -> return !modelCache.searchResult.isEmpty()
            ButtonTypes.FAILURES -> return !modelCache.searchResult.isEmpty() && modelCache.searchResult.fileData.none { it.value.failures == null }
            ButtonTypes.TEST -> return true
            else -> false
        }
        return filled
    }

    private inner class NavbarProvider:AttributeModifier(
    "class",
    "navbar navbar-expand navbar-dark flex-column flex-md-row bd-navbar sticky-top bg-dark"){
        val childClass=AttributeModifier("class","nav-item")
    }

    private inner class NavtabsProvider:AttributeModifier("class","nav nav-tabs nav-tabs-dark")
}