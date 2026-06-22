package nl.maas.filerenamer.io

import org.apache.commons.lang3.StringUtils
import java.io.File

class FileUtils {
    companion object {
        fun findFile(fileName: String): String {
            return File("../")
                .walkTopDown()
                .onFail { _, _ -> }
                .maxDepth(10)
                .firstOrNull { it.absolutePath.endsWith(fileName) }
                ?.absolutePath ?: StringUtils.EMPTY
        }
    }
}