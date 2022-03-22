package nl.maas.filerenamer.frontend.wicket.objects.enums

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType
import nl.maas.filerenamer.frontend.wicket.pages.*
import org.apache.wicket.Page

enum class ButtonTypes(val pageClass: Class<out Page>, val iconType: IconType) {
    FIND(SearchPage::class.java, FontAwesome5IconType.search_s), FILES(
        FilesPage::class.java,
        FontAwesome5IconType.file_s
    ),
    FAILURES(FailsPage::class.java, FontAwesome5IconType.cross_s),
    CONFIRM(ConfirmationPage::class.java, FontAwesome5IconType.list_s),
    TEST(TestPage::class.java, FontAwesome5IconType.cogs_s);
}