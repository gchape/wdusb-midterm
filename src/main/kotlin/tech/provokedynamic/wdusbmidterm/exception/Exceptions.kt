package tech.provokedynamic.wdusbmidterm.exception

class EntityNotFoundException(message: String) : RuntimeException(message)

class EntityAlreadyExistsException(message: String) : RuntimeException(message)

class EntityDeletedException(message: String) : RuntimeException(message)