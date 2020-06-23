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
        req?:return
        resp?:return
        
        resp.apply {
            contentType = "text/html"
            characterEncoding = "UTF-8"
            writer.write(
                "<html>" +
                    "<head>" +
                    "<title>" +
                    "系统检测" +
                    "</title>" +
                    "<body>" +
                    "<p>服务器系统: ${System.getProperty("os.name")}</p>" +
                    "<p>客户IP: ${req.getHeader("x-forwarded-for")?:req.remoteAddr}</p>" +
                    "<p>User-Agent: ${req.getHeader("user-agent")}</p>" +
                    "</body>" +
                    "</html>"
            )
            
        }
    }
}