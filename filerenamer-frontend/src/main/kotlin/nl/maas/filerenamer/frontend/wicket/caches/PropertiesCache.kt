package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.frontend.wicket.objects.I10N
import nl.maas.filerenamer.io.FileUtils
import nl.maas.filerenamer.io.JsonUtils
import nl.maas.wicket.framework.pages.BasePage
import nl.maas.wicket.framework.services.Translator
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
    ) : nl.maas.wicket.framework.services.Translator {

        var page: KClass<out BasePage<*>> = BasePage::class

        override val language: String
            get() = propertiesCache.i10N.languages.firstOrNull { "nl".equals(it.code) }?.code
                ?: propertiesCache.i10N.languages.first { "en".equals(it.code) }.code

        fun <T : BasePage<*>> forPage(page: T): Translator {
            this.page = page::class
            return this
        }

        fun forPageClass(page: KClass<out BasePage<*>>): Translator {
            this.page = page
            return this
        }

        override fun translate(word: String): String = i10N.translate(page, word, language)

        override fun unTranslate(word: String): String = i10N.untranslate(page, word, language)
    }

}
