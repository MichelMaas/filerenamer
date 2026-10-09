package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.wicket.framework.pages.BasePage
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class Translator(
) : nl.maas.wicket.framework.services.Translator {

    val propertiesCache: PropertiesCache = ContextProvider.ctx.getBean(PropertiesCache::class.java)
    val supportedLanguages: List<String> = propertiesCache.i10N.languages.map { it.name }
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

    override fun translate(word: String): String = propertiesCache.i10N.translate(page, word, language)

    override fun unTranslate(word: String): String = propertiesCache.i10N.untranslate(page, word, language)
}