package com.casadeoso

sealed trait AppErrors
case class FileSystemError()  extends AppErrors
case class CacheMiss()        extends AppErrors
case class JsonParsingError() extends AppErrors
case class NoApiKey()         extends AppErrors
case class ApiKeyError()      extends AppErrors
case class ApiError()         extends AppErrors
