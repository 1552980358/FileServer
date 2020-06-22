import java.text.SimpleDateFormat
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * [Index]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/6/22
 * @time    : 10:34
 **/

@WebServlet("/")
class Index: HttpServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        resp?.apply {
            contentType = "text/html"
            writer.write(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis()))
        }
    }
}