import com.google.gson.JsonObject
import lib.github1552980358.ktExtension.jvm.io.writeAndClose
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/authorize")
class Authorization: HttpServlet() {
    
    companion object {
        
        /** Request **/
        private const val REQUEST_TYPE = "type"
        private const val REQUEST_TYPE_LOGIN = "login"
        private const val REQUEST_TYPE_AUTHORIZE = "authorize"
        
        /** Login params **/
        private const val LOGIN_AC = "ac"
        private const val LOGIN_PW = "pw"
        
        /** Authorization param **/
        private const val AUTHORIZE_TOKEN = "token"
        
        /** Response message **/
        private const val RESPONSE_HEAD = "response"
        
        /** Unknown found **/
        private const val RESPONSE_INTERNAL_ERROR = "internal_error"
        private const val RESPONSE_UNKNOWN_TYPE = "unknown_type"
        private const val RESPONSE_UNKNOWN_ID_PW = "unknown_id_pw"
        private const val RESPONSE_UNKNOWN_TOKEN = "unknown_token"
        private const val RESPONSE_ID_PW_NOT_SPECIFIED = "id_pw_not_specified"
        private const val RESPONSE_TOKEN_NOT_SPECIFIED = "token_not_specified"
    
        /** Success **/
        private const val RESPONSE_LOGIN_SUCCESS = "login_success"
        private const val RESPONSE_LOGIN_SUCCESS_TOKEN = "token"
        private const val RESPONSE_AUTHORIZATION_SUCCESS = "authorized"
        
        /**
         * File path
         * Directory and files will be created with script,
         * edit and modify the script before starting up the service
         **/
        /** Linux **/
        private const val LINUX_FILE_CONFIG = "/root/.fileServer/config"
        private const val LINUX_FILE_SHA256 = "/root/.fileServer/sha256"
        /** Windows **/
        private const val WIN_FILE_CONFIG = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\config"
        private const val WIN_FILE_SHA256 = "C:\\Program Files\\Tomcat\\webapps\\ROOT\\saves\\sha256"
    }
    
    /**
     * [doGet]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        resp?.apply {
        
        }
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
        when (req.getParameter(REQUEST_TYPE)) {
            REQUEST_TYPE_AUTHORIZE -> authorize(req, resp)
            REQUEST_TYPE_LOGIN -> login(req, resp)
            else -> tryCatch { unknown(resp) }
        }
        
    }
    
    /**
     * [login]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    private fun login(req: HttpServletRequest, resp: HttpServletResponse) {
        val ac = req.getParameter(LOGIN_AC)
        val pw = req.getParameter(LOGIN_PW)
        
        if (ac == null || pw == null) {
            
            tryCatch {
                resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_ID_PW_NOT_SPECIFIED) }.toString())
            }
            return
        }
        
        val dir = File(if (isWindows()) WIN_FILE_CONFIG else LINUX_FILE_CONFIG)
        dir.apply {
            if (!exists()) {
                resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_INTERNAL_ERROR) }.toString())
                return
            }
            readLines().apply {
                if (size < 2) {
                    return
                }
                if (!checkAcPw(ac, this[0], resp) || !checkAcPw(pw, this[1], resp)) {
                    return
                }
            }
        }
        
        val sha256 = getSHA256(dir.readBytes())
        tryCatch {
            resp.outputStream.writeAndClose(
                JsonObject().apply {
                    addProperty(RESPONSE_HEAD, RESPONSE_LOGIN_SUCCESS)
                    addProperty(RESPONSE_LOGIN_SUCCESS_TOKEN, sha256)
                }.toString()
            )
        }
        File(if (isWindows()) WIN_FILE_SHA256 else LINUX_FILE_SHA256).apply {
            if (exists()) {
                tryCatch { delete() }
            }
            tryCatch { createNewFile() }
            tryCatch { writeText(sha256) }
        }
    }
    
    /**
     * [authorize]
     * @param req [HttpServletRequest]
     * @param resp [HttpServletResponse]
     **/
    private fun authorize(req: HttpServletRequest, resp: HttpServletResponse) {
        val token = req.getParameter(AUTHORIZE_TOKEN)
        if (token == null) {
            resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_TOKEN_NOT_SPECIFIED) }.toString())
            return
        }
        
        File(if (isWindows()) WIN_FILE_SHA256 else LINUX_FILE_SHA256).apply {
            if (!exists()) {
                JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_INTERNAL_ERROR) }.toString()
                resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_INTERNAL_ERROR) }.toString())
                return
            }
            
            val sha256 = readText()
            if (sha256.isEmpty()) {
                resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_INTERNAL_ERROR) }.toString())
                return
            }
            
            if (token == sha256) {
                resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_AUTHORIZATION_SUCCESS) }.toString())
                return
            }
            
            resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_UNKNOWN_TOKEN) }.toString())
        }
    }
    
    /**
     * [unknown]
     * @param resp [HttpServletResponse]
     **/
    private fun unknown(resp: HttpServletResponse) {
        resp.outputStream.writeAndClose(JsonObject().apply { addProperty(RESPONSE_HEAD, RESPONSE_UNKNOWN_TYPE) }.toString())
    }
    
    /**
     * [checkAcPw]
     * @param in [String]
     * @param local [String]
     * @param resp [HttpServletResponse]
     * @return [Boolean]
     **/
    private fun checkAcPw(`in`: String, local: String, resp: HttpServletResponse): Boolean {
        if (local.substring(local.indexOf('=') + 1) == `in`) {
            return true
        }
        
        resp.outputStream.writeAndClose("{\"$RESPONSE_HEAD\"=\"$RESPONSE_UNKNOWN_ID_PW\"}")
        return false
    }
    
    /**
     * [getSHA256]
     * @param byteArray [ByteArray]
     * @return [String]
     **/
    private fun getSHA256(byteArray: ByteArray): String {
        if (byteArray.isEmpty()) {
            // 返回32个0
            return "00000000000000000000000000000000"
        }
        
        val result = BigInteger(1, MessageDigest.getInstance("SHA-256").digest(byteArray)).toString(16)
        
        if (result.length > 32) {
            return result
        }
        
        return StringBuilder().apply { repeat((0 until 32 - result.length).count()) { append('0') } }.toString() + result
    }
    
}