import com.google.gson.JsonArray
import com.google.gson.JsonObject
import utils.AuthorizeHttpServlet
import java.io.File
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/browser")
class Browser: AuthorizeHttpServlet() {
    
    companion object {
        /** Request head **/
        private const val REQUEST_PURPOSE = "purpose"
        private const val REQUEST_PURPOSE_GET_LIST = "list"
        private const val REQUEST_PURPOSE_FILE_LIST = "files"
        
        /** Error found **/
        private const val RESPONSE_PURPOSE_NOT_SPECIFIED = "purpose_not_specified"
        private const val RESPONSE_LIST_NAME_NOT_FOUND = "list_name_not_found"
        private const val RESPONSE_LIST_NAME_NOT_SPECIFIED = "list_name_not_specified"
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
        private const val RESPONSE_FILE_IS_DIR = "isDir"
        private const val RESPONSE_FILE_NAME = "name"
        private const val RESPONSE_FILE_SIZE = "size"
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
        req ?: return
        resp ?: return
        
        resp.contentType = "application/json"
        if (!authorize(req, resp)) {
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
        val jsonObject: JsonObject
        getListFile().apply {
            if (!exists()) {
                responseSingle(resp, RESPONSE_INTERNAL_ERROR)
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
            
            jsonObject = JsonObject().apply {
                addProperty(RESPONSE_HEAD, RESPONSE_GET_LIST_SUCCESS)
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
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    private fun getFiles(req: HttpServletRequest, resp: HttpServletResponse) {
        val name = req.getParameter(FILE_LIST_NAME)
        if (name == null) {
            responseSingle(resp, RESPONSE_LIST_NAME_NOT_SPECIFIED)
            return
        }
    
        getListFile().apply {
            if (!exists()) {
                responseSingle(resp, RESPONSE_INTERNAL_ERROR)
                return
            }
            
            if (length() == 0L) {
                responseSingle(resp, RESPONSE_INTERNAL_ERROR)
                return
            }
            
            var index: Int
            for (i in readLines()) {
                index = i.indexOf('=')
                if (i.substring(0, index) != name) {
                    continue
                }
                
                var dir = req.getParameter(FILE_LIST_DIR)
                
                // 替换为系统的分隔符
                when {
                    dir == null -> {
                        dir = ""
                    }
                    dir.contains('/') -> {
                        dir = dir.replace('/', File.separatorChar)
                    }
                    dir.contains('\\') -> {
                        dir = dir.replace('\\', File.separatorChar)
                    }
                }
                
                
                val file = File(
                    "${i.substring(index + 1)}${if (dir.startsWith(File.separatorChar)) dir else "${File.separatorChar}$dir"}"
                )
                
                if (!file.exists()) {
                    response(resp, RESPONSE_DIR_NOT_FOUND)
                }
    
                val list = file.listFiles()
                if (list == null) {
                    responseSingle(resp, RESPONSE_INTERNAL_ERROR)
                }
    
                val jsonArray = JsonArray()
                list?.forEach { item ->
                    jsonArray.add(
                        JsonObject().apply {
                            addProperty(RESPONSE_FILE_NAME, item.name)
                            addProperty(RESPONSE_FILE_IS_DIR, item.isDirectory)
                            addProperty(RESPONSE_FILE_SIZE, item.length())
                        }
                    )
                }
                response(
                    resp,
                    JsonObject().apply {
                        addProperty(RESPONSE_HEAD, RESPONSE_FILE_LIST_SUCCESS)
                        addProperty(RESPONSE_FILE_LIST_SIZE, jsonArray.size())
                        add(RESPONSE_FILE_LIST, jsonArray)
                    }.toString()
                )
                
                return
            }
            
        }
        
        responseSingle(resp, RESPONSE_LIST_NAME_NOT_FOUND)
        
    }
    
}