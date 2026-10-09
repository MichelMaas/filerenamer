package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.frontend.wicket.objects.I10N
import nl.maas.filerenamer.io.FileUtils
import nl.maas.filerenamer.io.JsonUtils
import org.apache.wicket.resource.FileSystemResourceReference
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class PropertiesCache() {
    val i10N: I10N = JsonUtils.load(FileUtils.findFile("I10N.json").toString(), I10N::class.java)!!

    val iconReference =
        FileSystemResourceReference("favicon", Path.of(FileUtils.findFile("icon.png")))
    val brandReference =
        FileSystemResourceReference("brand", Path.of(FileUtils.findFile("brand.png")))

}
