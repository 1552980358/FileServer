package utils

import com.google.gson.JsonObject
import lib.github1552980358.ktExtension.jvm.io.writeAndClose
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse

/**
 * [BaseHttpServlet]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/6/23
 * @time    : 20:45
 **/

open class BaseHttpServlet: HttpServlet() {
    
    companion object {
    
        /** Response heads **/
        const val RESPONSE_HEAD = "response"
        const val RESPONSE_INTERNAL_ERROR = "internal_error"
    
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
        
    }
    
    /**
     * [isWindows]
     * @return [Boolean]
     **/
    fun isWindows() = System.getProperty("os.name").toLowerCase().startsWith("win")
    
    /**
     * [responseSingle]
     * @param resp [HttpServletResponse]
     * @param content [String]
     **/
    fun responseSingle(resp: HttpServletResponse, content: String) = tryCatch {
        resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, content) }.toString())
    }
    
    /**
     * [response]
     * @param resp [HttpServletResponse]
     * @param content [String]
     **/
    fun response(resp: HttpServletResponse, content: String) = tryCatch {
        resp.outputStream.writeAndClose(content)
    }
    
}