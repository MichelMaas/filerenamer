package nl.maas.filerenamer.io

import nl.maas.filerenamer.errors.FilerenamerBackendError
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class FileHandler {

    fun searchFilesIn(path: String): Map<File, List<File>> {
        val headDir = Paths.get(path).toFile()
        if (!headDir.exists())
            throw FilerenamerBackendError("File ${path} not found!", IllegalStateException("File ${path} not found!"))
        var files = HashMap<File, List<File>>()
        if (headDir.isDirectory)
            files.putAll(searchFilesIn(headDir))

        return files;
    }

    fun renameFiles(vararg files: RenameOrder) {
        files.filter { it.newName.isNotBlank() }.forEach { file -> moveFile(file) }
    }

    private fun moveFile(renameOrder: RenameOrder) {
        Files.move(
            renameOrder.file.toPath(),
            renameOrder.file.toPath().resolveSibling(renameOrder.newName),
            StandardCopyOption.REPLACE_EXISTING
        )
        println("Renamed ${renameOrder.file.name} to ${renameOrder.newName} in ${renameOrder.file.parent}")
    }

    private fun searchFilesIn(path: File): Map<File, List<File>> {
        var files = path.listFiles(FileFilter { file -> file.isFile }).asList()
        var dirs = path.listFiles(FileFilter { file -> file.isDirectory }).asList()

        var map = HashMap<File, List<File>>()
        if (files.isNotEmpty()) {
            map.put(path, files.filter { file -> !".file-renamer-settings.xml".equals(file.name) });
        }

        dirs.map { file -> searchFilesIn(file.absolutePath) }.forEach { dirMap -> map.putAll(dirMap) }
        return map
    }
}