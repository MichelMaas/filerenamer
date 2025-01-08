package nl.maas.filerenamer.errors

class FilerenamerBackendError(override val message: String, override val cause: Throwable) :
    FilerenamerError(message, cause) {
}