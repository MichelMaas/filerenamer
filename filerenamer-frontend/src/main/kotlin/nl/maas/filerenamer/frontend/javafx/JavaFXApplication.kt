package nl.maas.filerenamer.frontend.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.web.WebView
import javafx.stage.Stage
import nl.maas.filerenamer.frontend.ContextProvider

class JavaFXApplication() : Application() {


    override fun start(stage: Stage) {
        stage.setTitle("Filerenamer")
        stage.isMaximized = true
        val webView = WebView()
        webView.engine.load("http://localhost:8080")
        val stackPane = StackPane(webView)
        val scene = Scene(stackPane)
        stage.setScene(scene)
        stage.show()
    }

    override fun stop() {
        super.stop()
        val ctx = ContextProvider.ctx
        ctx.close()
    }

}