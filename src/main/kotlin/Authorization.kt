import lib.github1552980358.ktExtension.jvm.io.writeAndClose
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
        private const val RESPOND_UNKNOWN_TYPE = "unknown_type"
        private const val RESPOND_UNKNOWN_ID_PW = "unknown_id_pw"
        private const val RESPOND_UNKNOWN_TOKEN = "unknown_token"
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
        
        when (req.getParameter(REQUEST_TYPE)) {
            REQUEST_TYPE_AUTHORIZE -> {
        
            }
            REQUEST_TYPE_LOGIN -> {
                login(req, resp)
            }
            else -> {
                try {
                    unknown(resp)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
    }
    
    private fun login(req: HttpServletRequest, resp: HttpServletResponse) {
        val ac = req.getParameter(LOGIN_AC)
        val pw = req.getParameter(LOGIN_PW)
        
        resp.contentType = "application/json"
        val dir = File(FILE_CONFIG)
        dir.readLines().apply {
            if (!checkAcPw(ac, this[0], resp) || !checkAcPw(pw, this[1], resp)) {
                return
            }
        }
        
        val sha256 = getSHA256(dir.readBytes())
        resp.outputStream.writeAndClose("{\"$RESPOND_HEAD\"=\"$RESPOND_LOGIN_SUCCESS\",\"$RESPOND_LOGIN_SUCCESS_TOKEN\"$sha256}")
        File(FILE_SHA256).writeText(sha256)
    }
    
    private fun authorize(req: HttpServletRequest, resp: HttpServletResponse) {
    
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
        
        return StringBuilder().apply { for (i in 0 until 32 - result.length) { append('0') } }.toString() + result
    }
    
}