package nl.maas.filerenamer.errors

abstract class FilerenamerError(message: String, cause: Throwable) : Throwable(message, cause) {
}