package utils

/**
 * File path
 * Directory and files will be created with script,
 * edit and modify the script before starting up the service
 **/
/** Linux **/
const val LINUX_FILE_CONFIG = "/root/.fileServer/config"
const val LINUX_FILE_SHA256 = "/root/.fileServer/sha256"
/** Windows **/
const val WIN_FILE_CONFIG = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\config"
const val WIN_FILE_SHA256 = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\sha256"

fun isWindows() = System.getProperty("os.name").toLowerCase().startsWith("win")
