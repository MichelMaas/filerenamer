package nl.maas.filerenamer.frontend.wicket.tools

import nl.maas.filerenamer.frontend.wicket.caches.PropertiesCache
import nl.maas.wicket.framework.pages.BasePage
import java.time.DayOfWeek
import kotlin.reflect.KClass

class TranslatedDayOfWeek private constructor(val propertiesCache: PropertiesCache) {

    companion object {
        fun translatedDays(cls: KClass<out BasePage<*>>, propertiesCache: PropertiesCache) =
            TranslatedDayOfWeek(propertiesCache).getDays(cls)
    }

    fun getDays(cls: KClass<out BasePage<*>>): Array<String> {
        return DayOfWeek.values().map { propertiesCache.translator.forPageClass(cls).translate(it.name) }.toTypedArray()
    }
}