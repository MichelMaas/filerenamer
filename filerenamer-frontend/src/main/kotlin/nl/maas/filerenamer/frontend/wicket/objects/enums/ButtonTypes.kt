package nl.maas.filerenamer.frontend.wicket.objects.enums

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType
import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.pages.*
import kotlin.reflect.KClass

@OptIn(ExperimentalStdlibApi::class)
enum class ButtonTypes(val pageClass: KClass<out BasePage>, val iconType: IconType) {


    SEARCH(SearchPage::class, FontAwesome5IconType.search_s),

    //    OPTIONS(OptionsPage::class, FontAwesome5IconType.cogs_s),
    OVERVIEW(OverviewPage::class, FontAwesome5IconType.folder_r),

    //    Transactions(TransactionsPage::class, FontAwesome5IconType.coins_s),
    TEST(TestPage::class, FontAwesome5IconType.cogs_s),
    DETAIL(DetailPage::class, FontAwesome5IconType.file_video_r);

    fun label(): String {
        return ContextProvider.ctx.getBean(PropertiesCache::class.java).translator.translate(pageClass, "title")
    }
}