package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.io.FileHandler
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.RepeatingView

class TestPanel : Panel {
    constructor(id: String) : super(id)

    init {
        add(Label("testLabel", "The testpanel is working"))
        val filesIn = FileHandler().searchFilesIn("/shares/anime")
        var repeater = RepeatingView("listItems")
        filesIn.keys.forEach { file -> repeater.add(Label(repeater.newChildId(),file.name))}
        add(repeater)
    }
}