package nl.maas.filerenamer.frontend

import nl.maas.filerenamer.frontend.wicket.objects.I10N
import nl.maas.filerenamer.frontend.wicket.objects.Language
import nl.maas.graal.base.support.ReflectionRegistrator
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@ImportRuntimeHints(ReflectionRuntimeHints::class)
@Configuration
class ReflectionRuntimeHints : ReflectionRegistrator(I10N::class, Language::class) {

}