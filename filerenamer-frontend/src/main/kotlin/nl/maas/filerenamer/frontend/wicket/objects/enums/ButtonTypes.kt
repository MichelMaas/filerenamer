package nl.maas.filerenamer.frontend.wicket.objects.enums

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.pages.*
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.objects.enums.ButtonType
import nl.maas.wicket.framework.pages.BasePage
import nl.maas.wicket.framework.services.Translator
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
enum class ButtonTypes(override val pageClass: KClass<out BasePage<*>>, override val iconType: IconType) : ButtonType {

    SEARCH(SearchPage::class, FontAwesome5IconType.search_s),
    OVERVIEW(OverviewPage::class, FontAwesome5IconType.list_s);

//    //    OPTIONS(OptionsPage::class, FontAwesome5IconType.cogs_s),
//    OVERVIEW(OverviewPage::class, FontAwesome5IconType.folder_r),
//
//    //    Transactions(TransactionsPage::class, FontAwesome5IconType.coins_s),
//    TEST(TestPage::class, FontAwesome5IconType.cogs_s),
//    DETAIL(DetailPage::class, FontAwesome5IconType.file_video_r);

    override fun label(translator: Translator): String {
        return translator.translate(name)
    }

    fun label(): String =
        label(ContextProvider.ctx.getBean(PropertiesCache::class.java).translator.forPageClass(pageClass))

    fun toNavBarButton(): BaseNavbarButton = BaseNavbarButton(this)
}