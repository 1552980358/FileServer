import utils.AuthorizeHttpServlet
import java.io.File
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/upload")
class Upload: AuthorizeHttpServlet() {
    
    companion object {
        
        private const val REQUEST_CONTENT_DISPOSITION = "Content-Type"
        private const val REQUEST_CONTENT_DISPOSITION_NAME = "name="
        private const val REQUEST_CONTENT_DISPOSITION_PATH = "path="
        
        private const val RESPONSE_NAME_NOT_PROVIDED = "name_not_provided"
        private const val RESPONSE_PATH_NOT_PROVIDED = "name_not_provided"
        private const val RESPONSE_NAME_NOT_FOUND = "name_not_found"
        private const val RESPONSE_CREATE_FAILED = "create_failed"
        private const val RESPONSE_WRITE_FAILED = "write_failed"
        private const val RESPONSE_ALREADY_EXISTS = "already_exists"
        private const val RESPONSE_SUCCESS = "success"
        
        private const val STR_SPACE = " "
        private const val STR_EMPTY = ""
    }
    
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
    
    }
    
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        req?:return
        resp?:return
        
        if (!authorize(req, resp)) {
            return
        }
        
        req.parts.forEach { part ->
            val contentDisposition = part.getHeader(REQUEST_CONTENT_DISPOSITION)
            val indexName = contentDisposition.indexOf(REQUEST_CONTENT_DISPOSITION_NAME)
            
            if (indexName == -1) {
                // responseSingle(resp, RESPONSE_NAME_NOT_PROVIDED)
                return
            }
            
            val divider = contentDisposition.lastIndexOf(';')
            if (divider == -1) {
                // responseSingle(resp, RESPONSE_PATH_NOT_PROVIDED)
                return
            }
            
            var name = try {
                contentDisposition.substring(indexName + 5, divider)
                    .replace(STR_SPACE, STR_EMPTY)
            } catch (e: Exception) {
                // responseSingle(resp, RESPONSE_NAME_NOT_PROVIDED)
                return
            }
            if (name.isEmpty()) {
                // responseSingle(resp, RESPONSE_NAME_NOT_PROVIDED)
                return
            }
            
            val pathIndex = contentDisposition.indexOf(REQUEST_CONTENT_DISPOSITION_PATH)
            if (pathIndex == -1) {
                // responseSingle(resp, RESPONSE_PATH_NOT_PROVIDED)
                return
            }
            
            var path = try {
                contentDisposition.substring(pathIndex + REQUEST_CONTENT_DISPOSITION_PATH.length)
            } catch (e: Exception) {
                // responseSingle(resp, RESPONSE_PATH_NOT_PROVIDED)
                return
            }
            
            if (path.isEmpty()) {
                // responseSingle(resp, RESPONSE_PATH_NOT_PROVIDED)
                return
            }
            
            when {
                path.contains('/') -> path = path.replace('/', File.separatorChar)
                path.contains('\\') -> path = path.replace('\\', File.separatorChar)
            }
            
            getListFile().apply {
                if (!exists()) {
                    // responseSingle(resp, RESPONSE_INTERNAL_ERROR)
                    return
                }
                
                for (line in readLines()) {
                    if (!line.startsWith(name)) {
                        continue
                    }
                    
                    name = line.substring(line.indexOf('=') + 1)
                    break
                }
                
            }
    
            if (name.isEmpty()) {
                // responseSingle(resp, RESPONSE_NAME_NOT_FOUND)
                return
            }
    
            when {
                name.contains('/') -> name = name.replace('/', File.separatorChar)
                name.contains('\\') -> name = name.replace('\\', File.separatorChar)
            }
            
            File(when {
                name.endsWith(File.separatorChar) && path.startsWith(File.separatorChar) -> name + path.substring(1)
                name.endsWith(File.separatorChar) || path.startsWith(File.separatorChar) -> name + path
                else -> name + File.separatorChar + path
            }).apply {
                if (exists()) {
                    // responseSingle(resp, RESPONSE_ALREADY_EXISTS)
                    return
                }
                if (createNewFile()) {
                    // responseSingle(resp, RESPONSE_CREATE_FAILED)
                    return
                }
                part.inputStream.use { `is` ->
                    outputStream().use { os ->
                        try {
                            var len = 0
                            val byteArray = ByteArray(1024)
                            while (len != -1) {
                                len = `is`.read(byteArray)
                                os.write(byteArray, 0, len)
                            }
                        } catch (e: Exception) {
                            // responseSingle(resp, RESPONSE_WRITE_FAILED)
                            os.close()
                            return
                        } finally {
                            os.flush()
                        }
                    }
                }
            }
            
            // responseSingle(resp, RESPONSE_SUCCESS)
            
        }
        
    }
    
}