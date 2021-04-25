package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.extrapolation.FileMetaData
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.Model

class MemberPanel(id: String, val data: FileMetaData) : Panel(id) {


    init {
        this.add(Label("memberName", data.name))
        add(TextField<String>("newMemberName", Model.of(data.newName)))
    }
}