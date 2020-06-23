import lib.github1552980358.ktExtension.jvm.io.writeAndClose
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import java.io.BufferedOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/Authorization")
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
        
        /** Respond message **/
        private const val RESPOND_HEAD = "respond"
        
        /** Unknown found **/
        private const val RESPOND_INTERNAL_ERROR = "internal_error"
        private const val RESPOND_UNKNOWN_TYPE = "unknown_type"
        private const val RESPOND_UNKNOWN_ID_PW = "unknown_id_pw"
        private const val RESPOND_UNKNOWN_TOKEN = "unknown_token"
        private const val RESPOND_ID_PW_NOT_SPECIFIED = "id_pw_not_specified"
        private const val RESPOND_TOKEN_NOT_SPECIFIED = "token_not_specified"
    
        /** Success **/
        private const val RESPOND_LOGIN_SUCCESS = "login_success"
        private const val RESPOND_LOGIN_SUCCESS_TOKEN = "token"
        private const val RESPOND_AUTHORIZATION_SUCCESS = "authorized"
        
        /** File path **/
        private const val FILE_CONFIG = "/root/.fileServer/config"
        private const val FILE_SHA256 = "/root/.fileServer/sha256"
    }
    
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        resp?.apply {
            contentType = "text/html"
        }
    }
    
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
    
    private fun login(req: HttpServletRequest, resp: HttpServletResponse) {
        val ac = req.getParameter(LOGIN_AC)
        val pw = req.getParameter(LOGIN_PW)
        
        if (ac == null || pw == null) {
            tryCatch { resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_ID_PW_NOT_SPECIFIED\"}") }
            return
        }
        
        val dir = File(FILE_CONFIG)
        dir.apply {
            if (!exists()) {
                resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_INTERNAL_ERROR\"}")
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
                "{\"$RESPOND_HEAD\"=\"$RESPOND_LOGIN_SUCCESS\",\"$RESPOND_LOGIN_SUCCESS_TOKEN\"$sha256}"
            )
        }
        File(FILE_SHA256).apply {
            if (exists()) {
                tryCatch { delete() }
            }
            tryCatch { createNewFile() }
            tryCatch { writeText(sha256) }
        }
    }
    
    private fun authorize(req: HttpServletRequest, resp: HttpServletResponse) {
        val token = req.getParameter(AUTHORIZE_TOKEN)
        if (token == null) {
            resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_TOKEN_NOT_SPECIFIED\"}")
            return
        }
        
        File(FILE_SHA256).apply {
            if (!exists()) {
                resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_INTERNAL_ERROR\"}")
                return
            }
            
            val sha256 = readText()
            if (sha256.isEmpty()) {
                resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_INTERNAL_ERROR\"}")
                return
            }
            
            if (token == sha256) {
                resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_AUTHORIZATION_SUCCESS\"}")
                return
            }
    
            resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_UNKNOWN_TOKEN\"}")
        }
    }
    
    private fun unknown(resp: HttpServletResponse) {
        resp.outputStream.use { os ->
            BufferedOutputStream(os).use { bos ->
                bos.write("{\"$RESPOND_HEAD\"=\"$RESPOND_UNKNOWN_TYPE\"}".toByteArray())
                bos.flush()
            }
            os.flush()
        }
    }
    
    private fun checkAcPw(`in`: String, local: String, resp: HttpServletResponse): Boolean {
        if (local.substring(local.indexOf('=') + 1) == `in`) {
            return true
        }
        
        resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_UNKNOWN_ID_PW\"}")
        return false
    }
    
    private fun getSHA256(byteArray: ByteArray): String {
        if (byteArray.isEmpty()) {
            // 返回32个0
            return "00000000000000000000000000000000"
        }
        
        val result = BigInteger(1, MessageDigest.getInstance("SHA-256").digest(byteArray)).toString(16)
        
        if (result.length > 32) {
            return result
        }
        
        return StringBuilder().apply { (0 until 32 - result.length).forEach { append('0') } }.toString() + result
    }
    
}