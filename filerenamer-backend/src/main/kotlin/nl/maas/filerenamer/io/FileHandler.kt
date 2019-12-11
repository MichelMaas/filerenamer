package nl.maas.filerenamer.io

import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path

class FileHandler {

    fun searchFilesIn(path: String): Map<File, List<File>> {
        val headDir = Path.of(path).toFile()
        if (!headDir.exists())
            throw IllegalStateException("File ${path} not found!")
        var files = HashMap<File, List<File>>()
        if (headDir.isDirectory)
            files.putAll(searchFilesIn(headDir))

        return files;
    }

    fun saveFiles(vararg files:RenameOrder ){
        files.forEach { file -> Files.move(file.file.toPath(),file.file.toPath().resolveSibling(file.newName)) }
    }

    private fun searchFilesIn(path: File): Map<File, List<File>> {
        var files = path.listFiles(FileFilter { file -> file.isFile }).asList()
        var dirs = path.listFiles(FileFilter { file -> file.isDirectory }).asList()

        var map = HashMap<File, List<File>>()
        if (files.isNotEmpty()) {
            map.put(path, files.filter { file -> !".file-renamer-settings.xml".equals(file.name) });
        }
        dirs.map { file -> searchFilesIn(path) }.forEach { dirMap -> map.putAll(dirMap) }
        return map
    }
}