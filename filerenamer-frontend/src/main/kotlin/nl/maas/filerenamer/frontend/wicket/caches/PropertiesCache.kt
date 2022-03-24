package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.frontend.wicket.objects.I10N
import nl.maas.filerenamer.frontend.wicket.pages.BasePage
import nl.maas.filerenamer.io.FileUtils
import nl.maas.filerenamer.io.JsonUtils
import org.apache.wicket.resource.FileSystemResourceReference
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.reflect.KClass

@Component
class PropertiesCache {
    protected val i10N: I10N
    val translator: Translator
    val iconReference =
        FileSystemResourceReference("favicon", Path.of(FileUtils.findFile("icon.png")))
    val brandReference =
        FileSystemResourceReference("brand", Path.of(FileUtils.findFile("brand.png")))

    init {
        i10N = JsonUtils.load(FileUtils.findFile("I10N.json").toString(), I10N::class.java)!!
    }

    constructor() {
        translator = Translator(this)
    }

    inner class Translator(
        val propertiesCache: PropertiesCache,
        val supportedLanguages: List<String> = propertiesCache.i10N.languages.map { it.name }
    ) {
        fun <T : KClass<out BasePage>> translate(page: T, key: String) =
            i10N.translate(page, key, currentLanguage)

        fun <T : KClass<out BasePage>> untranslate(page: T, key: String) =
            i10N.untranslate(page, key, currentLanguage)

        val currentLanguage
            get() = propertiesCache.i10N.languages.firstOrNull { "nl".equals(it.code) }?.code
                ?: propertiesCache.i10N.languages.first { "en".equals(it.code) }.code
    }

}
