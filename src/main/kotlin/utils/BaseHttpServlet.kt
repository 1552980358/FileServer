package utils

import com.google.gson.JsonObject
import lib.github1552980358.ktExtension.jvm.io.writeAndClose
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import java.io.File
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
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_CONFIG", ReplaceWith("FILE_CONFIG"), DeprecationLevel.ERROR)
        const val LINUX_FILE_CONFIG = "/root/.fileServer/config"
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_TOKEN", ReplaceWith("FILE_TOKEN"), DeprecationLevel.ERROR)
        const val LINUX_FILE_SHA256 = "/root/.fileServer/sha256"
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_LIST", ReplaceWith("FILE_LIST"), DeprecationLevel.ERROR)
        const val LINUX_FILE_LIST = "/root/.fileServer/list"
        /** Windows **/
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_CONFIG", ReplaceWith("FILE_CONFIG"), DeprecationLevel.ERROR)
        const val WIN_FILE_CONFIG = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\config"
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_TOKEN", ReplaceWith("FILE_TOKEN"), DeprecationLevel.ERROR)
        const val WIN_FILE_SHA256 = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\sha256"
        /** @hide **/
        @Suppress("unused")
        @Deprecated("Use FILE_LIST", ReplaceWith("FILE_LIST"), DeprecationLevel.ERROR)
        const val WIN_FILE_LIST = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\list"
        
        /** Relative path **/
        private const val FILE_DIR = "save"
        val FILE_CONFIG = "${File.separatorChar}${FILE_DIR}${File.separatorChar}config"
        val FILE_TOKEN = "${File.separatorChar}${FILE_DIR}${File.separatorChar}token"
        val FILE_LIST = "${File.separatorChar}${FILE_DIR}${File.separatorChar}list"
        
        const val RESPONSE_TYPE_JSON = "application/json"
        const val RESPONSE_TYPE_BINARY = "application/octet-stream"
        
    }
    
    /**
     * [isWindows]
     * @return [Boolean]
     **/
    @Deprecated("No longer use", ReplaceWith("NONE"),level = DeprecationLevel.ERROR)
    private fun isWindows() = System.getProperty("os.name").toLowerCase().startsWith("win")
    
    /**
     * [getTokenFile]
     * @return [File]
     **/
    fun getTokenFile() = File(servletContext.getRealPath(FILE_TOKEN)) // File(if (isWindows()) WIN_FILE_SHA256 else LINUX_FILE_SHA256)
    
    /**
     * [getConfigFile]
     * @return [File]
     **/
    fun getConfigFile() = File(servletContext.getRealPath(FILE_CONFIG)) // File(if (isWindows()) WIN_FILE_CONFIG else LINUX_FILE_CONFIG)
    
    /**
     * [getConfigFile]
     * @return [File]
     **/
    fun getListFile() = File(servletContext.getRealPath(FILE_LIST)) //File(if (isWindows()) WIN_FILE_LIST else LINUX_FILE_LIST)
    
    /**
     * [responseSingle]
     * @param resp [HttpServletResponse]
     * @param content [String]
     **/
    fun responseSingle(resp: HttpServletResponse, content: String) = tryCatch {
        response(resp, JsonObject().apply { addProperty(RESPONSE_HEAD, content) }.toString())
    }
    
    /**
     * [response]
     * @param resp [HttpServletResponse]
     * @param content [String]
     **/
    fun response(resp: HttpServletResponse, content: String) = tryCatch {
        resp.contentType = RESPONSE_TYPE_JSON
        resp.outputStream.writeAndClose(content)
    }
    
}