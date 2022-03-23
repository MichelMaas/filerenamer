package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.frontend.wicket.objects.Filter
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDate

@Component
class ModelCache {

    var selectedFolder: File? = null
    var filter = Filter()
    var localDate: LocalDate = LocalDate.now()
    var files: Map<File, ExtrapolationResult> = mapOf()

    fun isEmpty(): Boolean {
        return files.isNullOrEmpty()
    }

    fun isFileSelected(): Boolean {
        return selectedFolder != null
    }

}