import com.google.gson.JsonArray
import com.google.gson.JsonObject
import utils.BaseHttpServlet
import utils.LINUX_FILE_LIST
import utils.LINUX_FILE_SHA256
import utils.RESPONSE_INTERNAL_ERROR
import utils.WIN_FILE_LIST
import utils.WIN_FILE_SHA256
import utils.isWindows
import java.io.File
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/browser")
class Browser: BaseHttpServlet() {
    
    companion object {
        /** Request head **/
        private const val REQUEST_TOKEN = "token"
        private const val REQUEST_PURPOSE = "purpose"
        private const val REQUEST_PURPOSE_GET_LIST = "list"
        private const val REQUEST_PURPOSE_FILE_LIST = "files"
        
        /** Error found **/
        private const val RESPONSE_TOKEN_UNKNOWN = "token_unknown"
        private const val RESPONSE_TOKEN_NOT_SPECIFIED = "token_not_specified"
        private const val RESPONSE_PURPOSE_NOT_SPECIFIED = "purpose_not_specified"
        private const val RESPONSE_LIST_NAME_NOT_FOUND = "list_name_not_found"
        private const val RESPONSE_DIR_NOT_FOUND = "dir_not_found"
    
        /** Response head **/
        private const val RESPONSE_GET_LIST_SUCCESS = "list_success"
        private const val RESPONSE_FILE_LIST_SUCCESS = "files_success"
        
        /** Response List **/
        private const val RESPONSE_LIST = "list"
        private const val RESPONSE_LIST_SIZE = "size"
        private const val RESPONSE_LIST_NAME = "name"
        private const val RESPONSE_LIST_PATH = "path"
        
        /** Response file list **/
        private const val FILE_LIST_NAME = "name"
        private const val FILE_LIST_DIR = "dir"
        private const val RESPONSE_FILE_LIST = "files"
        private const val RESPONSE_FILE_LIST_SIZE = "size"
    }
    
    /**
     * [doGet]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
    
    }
    
    /**
     * [doPost]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        req?:return
        resp?:return
        
        val token = req.getParameter(REQUEST_TOKEN)
        if (token == null) {
            // resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_NOT_SPECIFIED) }.toString())
            responseSingle(resp, RESPONSE_TOKEN_NOT_SPECIFIED)
            return
        }
    
        // 验证身份
        if (File(if (isWindows()) WIN_FILE_SHA256 else LINUX_FILE_SHA256).readText() != token) {
            // resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_UNKNOWN) }.toString())
            responseSingle(resp, RESPONSE_TOKEN_UNKNOWN)
            return
        }
        
        val purpose = req.getParameter(REQUEST_PURPOSE)
        if (purpose == null) {
            // resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_PURPOSE_NOT_SPECIFIED) }.toString())
            responseSingle(resp, RESPONSE_PURPOSE_NOT_SPECIFIED)
            return
        }
        
        when (purpose) {
            REQUEST_PURPOSE_GET_LIST -> getList(resp)
            REQUEST_PURPOSE_FILE_LIST -> getFiles(req, resp)
            else -> responseSingle(resp, RESPONSE_PURPOSE_NOT_SPECIFIED)
        }
        
    }
    
    /**
     * [getList]
     * @param resp [HttpServletResponse]
     **/
    private fun getList(resp: HttpServletResponse) {
        val jsonObject = JsonObject().apply {
            addProperty(RESPONSE_HEAD, RESPONSE_GET_LIST_SUCCESS)
        }
        File(if (isWindows()) WIN_FILE_LIST else LINUX_FILE_LIST).apply {
            if (!exists()) {
                response(resp, RESPONSE_INTERNAL_ERROR)
                return
            }
            
            if (length() == 0L) {
                response(resp, JsonObject().apply {
                    addProperty(RESPONSE_HEAD, RESPONSE_GET_LIST_SUCCESS)
                    addProperty(RESPONSE_LIST_SIZE, 0)
                    add(RESPONSE_LIST, JsonArray())
                }.toString())
                return
            }
            
            val jsonArray = JsonArray()
            @Suppress("LocalVariableName")
            var `=`: Int
            for (line in readLines()) {
                if (!line.contains('=')) {
                    continue
                }
                `=` = line.indexOf('=')
                jsonArray.add(
                    JsonObject().apply {
                        addProperty(RESPONSE_LIST_NAME, line.substring(0, `=`))
                        addProperty(RESPONSE_LIST_PATH, line.substring(`=` + 1))
                    }
                )
            }
            jsonObject.add(RESPONSE_LIST, jsonArray)
        }
        response(resp, jsonObject.toString())
    }
    
    /**
     * [getFiles]
     **/
    private fun getFiles(req: HttpServletRequest, resp: HttpServletResponse) {
    
    }
    
}