package nl.maas.filerenamer.frontend.wicket.caches

import me.bush.translator.Language
import nl.maas.wicket.framework.services.Translator
import org.springframework.stereotype.Component
import java.util.*

@Component
class GoogleTranslator() : Translator {

    val translator: me.bush.translator.Translator = me.bush.translator.Translator()
    override val language: String
        get() = Locale.getDefault().language

    override fun translate(word: String): String {
        return translator.translateBlocking(
            word,
            Language.valueOf(Locale.getDefault().getDisplayLanguage(Locale.ENGLISH).uppercase()),
            Language.ENGLISH
        ).translatedText.lowercase().replaceFirstChar { it.uppercase() }
    }

    override fun unTranslate(word: String): String {
        return translator.translateBlocking(
            word,
            Language.ENGLISH,
            Language.valueOf(Locale.getDefault().getDisplayLanguage(Locale.ENGLISH).uppercase())
        ).translatedText
    }

}