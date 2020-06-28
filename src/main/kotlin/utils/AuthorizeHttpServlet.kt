package utils

import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * [AuthorizeHttpServlet]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/6/24
 * @time    : 20:59
 **/

open class AuthorizeHttpServlet: BaseHttpServlet() {
    
    companion object {
        
        const val REQUEST_TOKEN = "token"
        
        const val RESPONSE_TOKEN_UNKNOWN = "token_unknown"
        
        const val RESPONSE_TOKEN_NOT_SPECIFIED = "token_not_specified"
    
        const val RESPONSE_NAME_NOT_FOUND = "name_not_found"
        
    }
    
    /**
     * [authorize]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     * @return [Boolean]
     **/
    fun authorize(req: HttpServletRequest, resp: HttpServletResponse): Boolean {
        val token = req.getParameter(REQUEST_TOKEN)
        if (token == null) {
            // resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_NOT_SPECIFIED) }.toString())
            responseSingle(resp, RESPONSE_TOKEN_NOT_SPECIFIED)
            return false
        }
    
        // 验证身份
        if (getTokenFile().readText() != token) {
            // resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_UNKNOWN) }.toString())
            responseSingle(resp, RESPONSE_TOKEN_UNKNOWN)
            return false
        }
        
        return true
    }
    
    /**
     * [getConvertDividers]
     * @param string [String]
     * @return [String]
     **/
    fun getConvertDividers(string: String) = string.replace('\\', '/').replace('/', File.separatorChar)
    
}