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
        
        const val RESPONSE_HEAD = "response"
        
    }
    
    fun responseSingle(resp: HttpServletResponse, content: String) = tryCatch {
        resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, content) }.toString())
    }
    
    fun response(resp: HttpServletResponse, content: String) = tryCatch {
        resp.outputStream.writeAndClose(content)
    }
    
}