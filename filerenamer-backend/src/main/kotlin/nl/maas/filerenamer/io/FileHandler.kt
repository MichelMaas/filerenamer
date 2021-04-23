package nl.maas.filerenamer.io

import java.io.File
import java.io.FileFilter
import java.nio.file.*

class FileHandler {

    fun searchFilesIn(path: String): Map<File, List<File>> {
        val headDir = Paths.get(path).toFile()
        if (!headDir.exists())
            throw IllegalStateException("File ${path} not found!")
        var files = HashMap<File, List<File>>()
        if (headDir.isDirectory)
            files.putAll(searchFilesIn(headDir))

        return files;
    }

    fun renameFiles(vararg files:RenameOrder ){
        files.forEach { file -> moveFile(file) }
    }

    private fun moveFile(renameOrder: RenameOrder) {
        if(Paths.get("${renameOrder.file.parent}/${renameOrder.newName}").toFile().exists()){
            println("File ${renameOrder.newName} already exists in '${renameOrder.file.parent}'")
            print("Replace existing file? (Y/N): ")
            if("N".equals(readLine()!!.toUpperCase())) return
        }
        Files.move(renameOrder.file.toPath(), renameOrder.file.toPath().resolveSibling(renameOrder.newName), StandardCopyOption.REPLACE_EXISTING)
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