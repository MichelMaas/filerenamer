package nl.maas.filerenamer.frontend.wicket.caches

import me.bush.translator.Language
import nl.maas.wicket.framework.services.Translator
import org.springframework.stereotype.Component
import java.util.*

@Component
class GoogleTranslator() : Translator {

    override val language: String
        get() = Locale.getDefault().language

    override fun translate(word: String): String {
        var retry = true
        var counter = 0
        var translation = word
        do {
            try {
                translation = me.bush.translator.Translator().translateBlocking(
                    word,
                    Language.valueOf(Locale.getDefault().getDisplayLanguage(Locale.ENGLISH).uppercase()),
                    Language.ENGLISH
                ).translatedText.lowercase().replaceFirstChar { it.uppercase() }
                retry = false
            } catch (e: Exception) {
                if (!retry) {
                    println("Exception caught in GoogleTranslator:${e.message}")
                } else {
                    counter++
                    if (counter > 3) {
                        retry = false
                    }
                }
            }
        } while (retry)
        return translation
    }

    override fun unTranslate(word: String): String {
        var retry = true
        var counter = 0
        var translation = word
        do {
            try {
                translation = me.bush.translator.Translator().translateBlocking(
                    word,
                    Language.ENGLISH,
                    Language.valueOf(Locale.getDefault().getDisplayLanguage(Locale.ENGLISH).uppercase())
                ).translatedText
                retry = false
            } catch (e: Exception) {
                if (!retry) {
                    println("Exception caught in GoogleTranslator:${e.message}")
                } else {
                    counter++
                    if (counter > 3) {
                        retry = false
                    }
                }
            }
        } while (retry)
        return translation
    }

}