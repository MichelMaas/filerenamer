package nl.maas.filerenamer.frontend.wicket.objects.enums

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.GoogleTranslator
import nl.maas.filerenamer.frontend.wicket.pages.*
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.objects.enums.ButtonType
import nl.maas.wicket.framework.pages.BasePage
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
enum class ButtonTypes(override val pageClass: KClass<out BasePage<*>>, override val iconType: IconType) : ButtonType {

    SEARCH(SearchPage::class, FontAwesome5IconType.search_s),
    OVERVIEW(OverviewPage::class, FontAwesome5IconType.list_s);

    override val label get() = ContextProvider.ctx.getBean(GoogleTranslator::class.java).translate(name)

    fun toNavBarButton(): BaseNavbarButton = BaseNavbarButton(this)
}