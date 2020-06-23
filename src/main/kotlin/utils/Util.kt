package utils

/**
 * File path
 * Directory and files will be created with script,
 * edit and modify the script before starting up the service
 **/
/** Linux **/
const val LINUX_FILE_CONFIG = "/root/.fileServer/config"
const val LINUX_FILE_SHA256 = "/root/.fileServer/sha256"
const val LINUX_FILE_LIST = "/root/.fileServer/list"
/** Windows **/
const val WIN_FILE_CONFIG = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\config"
const val WIN_FILE_SHA256 = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\sha256"
const val WIN_FILE_LIST = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\list"

const val RESPONSE_INTERNAL_ERROR = "internal_error"

fun isWindows() = System.getProperty("os.name").toLowerCase().startsWith("win")
