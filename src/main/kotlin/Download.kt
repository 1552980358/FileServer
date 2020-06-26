import utils.AuthorizeHttpServlet
import java.io.File
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/download")
@MultipartConfig
class Download: AuthorizeHttpServlet() {
    
    companion object {
    
        /** Request header **/
        private const val REQUEST_NAME = "name"
        private const val REQUEST_PATH = "path"
        
        /** Response **/
        private const val RESPONSE_NAME_NOT_SPECIFIED = "name_not_specified"
        private const val RESPONSE_PATH_NOT_SPECIFIED = "path_not_specified"
        private const val RESPONSE_FILE_NOT_FOUND = "file_not_found"
        
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
        
        if (!authorize(req, resp)) {
            return
        }
        
        val name = req.getParameter(REQUEST_NAME)
        if (name == null) {
            resp.contentType = RESPONSE_TYPE_JSON
            responseSingle(resp, RESPONSE_NAME_NOT_SPECIFIED)
            return
        }
        
        var path = req.getParameter(REQUEST_PATH)
        if (path == null) {
            responseSingle(resp, RESPONSE_PATH_NOT_SPECIFIED)
            return
        }
        
        if (path.contains('\\')) {
            path = path.replace('\\', File.separatorChar)
        } else if (path.contains('/')) {
            path = path.replace('/', File.separatorChar)
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
            
            @Suppress("LocalVariableName")
            var `=`: Int
            for (line in readLines()) {
                if (!line.contains('=')) {
                    continue
                }
                
                `=` = line.indexOf('=')
                if (line.substring(0, `=`) != name) {
                    continue
                }
                
                var listPath = line.substring(`=` + 1)
                if (listPath.contains('/')) {
                    listPath = listPath.replace('/', File.separatorChar)
                } else if (listPath.contains('\\')) {
                    listPath = listPath.replace('\\', File.separatorChar)
                }
                
                val fullPath = when {
                    listPath.endsWith(File.separatorChar) && path.startsWith(File.separatorChar) -> listPath + path.substring(1)
                    listPath.endsWith(File.separatorChar) || path.startsWith(File.separatorChar) -> listPath + path
                    else -> listPath + File.separator + path
                }
                
                File(fullPath).apply {
                    if (!exists()) {
                        responseSingle(resp, RESPONSE_FILE_NOT_FOUND)
                        return
                    }
                    
                    resp.setHeader("Content-Disposition", "attachment; filename=$name")
                    resp.setContentLengthLong(length())
                    resp.contentType = RESPONSE_TYPE_BINARY
                    inputStream().use { `is` ->
                        resp.outputStream.use { os ->
                            var len = 0
                            val byteArray = ByteArray(1024)
                            while (len != -1) {
                                len = `is`.read(byteArray)
                                os.write(byteArray, 0, len)
                            }
                            os.flush()
                        }
                    }
                    
                }
                return
            }
            
            responseSingle(resp, RESPONSE_NAME_NOT_FOUND)
        }
        
    }
    
}