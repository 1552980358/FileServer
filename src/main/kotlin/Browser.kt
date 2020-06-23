import com.google.gson.JsonObject
import lib.github1552980358.ktExtension.jvm.io.writeAndClose
import java.io.File
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/browser")
class Browser: HttpServlet() {
    
    companion object {
        private const val REQUEST_TOKEN = "token"
        private const val REQUEST_PURPOSE = "purpose"
        private const val REQUEST_PURPOSE_GET_LIST = "list"
        private const val REQUEST_PURPOSE_FILE_LIST = "files"
        
        private const val FILE_LIST_NAME = "name"
        private const val FILE_LIST_DIR = "dir"
    
        private const val RESPONSE_HEAD = "response"
        
        private const val RESPONSE_TOKEN_UNKNOWN = "token_unknown"
        private const val RESPONSE_TOKEN_NOT_SPECIFIED = "token_not_specified"
        private const val RESPONSE_PURPOSE_NOT_SPECIFIED = "purpose_not_specified"
        private const val RESPONSE_LIST_NAME_NOT_FOUND = "list_name_not_found"
        private const val RESPONSE_DIR_NOT_FOUND = "dir_not_found"
    
        private const val RESPONSE_GET_LIST_SUCCESS = "list_success"
        private const val RESPONSE_FILE_LIST_SUCCESS = "files_success"
        private const val RESPONSE_FILE_LIST = "files"
        private const val RESPONSE_LIST = "list"
        
    }
    
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
    
    }
    
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        req?:return
        resp?:return
        
        val token = req.getParameter(REQUEST_TOKEN)
        if (token == null) {
            resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_NOT_SPECIFIED) }.toString())
            return
        }
    
        // 验证身份
        if (File(if (isWindows()) WIN_FILE_SHA256 else LINUX_FILE_SHA256).readText() != token) {
            resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_UNKNOWN) }.toString())
            return
        }
        
        val purpose = req.getParameter(REQUEST_PURPOSE)
        if (purpose == null) {
            resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_PURPOSE_NOT_SPECIFIED) }.toString())
            return
        }
        
        when (purpose) {
            REQUEST_PURPOSE_GET_LIST -> getList(req, resp)
            REQUEST_PURPOSE_FILE_LIST -> getFiles(req, resp)
            else -> unknown(resp)
        }
        
    }
    
    private fun getList(req: HttpServletRequest, resp: HttpServletResponse) {
    
    }
    
    private fun getFiles(req: HttpServletRequest, resp: HttpServletResponse) {
    
    }
    
    private fun unknown(resp: HttpServletResponse, category: String = RESPONSE_PURPOSE_NOT_SPECIFIED) {
        resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, category) }.toString())
    }
    
}