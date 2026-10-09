package nl.maas.filerenamer.frontend.wicket.tools

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.filerenamer.frontend.wicket.caches.Translator
import nl.maas.wicket.framework.pages.BasePage
import java.time.DayOfWeek
import kotlin.reflect.KClass

class TranslatedDayOfWeek private constructor() {

    val translator = ContextProvider.ctx.getBean(Translator::class.java) as Translator

    companion object {
        fun translatedDays(cls: KClass<out BasePage<*>>, propertiesCache: PropertiesCache) =
            TranslatedDayOfWeek().getDays(cls)
    }

    fun getDays(cls: KClass<out BasePage<*>>): Array<String> {
        return DayOfWeek.values().map { translator.forPageClass(cls).translate(it.name) }.toTypedArray()
    }
}