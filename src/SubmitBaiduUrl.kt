package src

import org.dom4j.DocumentException
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.*
import java.net.URL

/**
 * 使用百度API提交站点链接
 * 1.查看代码中的todo,根据todo提示,将信息填为自己的即可
 */

fun main() {
    // TODO: 将site和token替换为自己的站点及toke,如:
//    val site = "blog.yanjingtp.cn"
//    val token = "U3b3d1v2vbLicdaa"  //此token为无效token,仅做示例
    val site = ""
    val token = ""
    val url = "http://data.zz.baidu.com/urls?site=${site}&token=${token}" //接口调用地址
    val json = post(url, urls) //执行推送方法
    println("结果是$json") //打印推送结果
}

//保存url
//解析xml的站点地图
val urls: List<String>
    get() {
        val urls: MutableList<String> = ArrayList()

        //添加非站点地图中的url
        // TODO: 替换为自己非站点地图中的url,如无需添加,可删除
        urls.add("https://blog.yanjingtp.cn/tags/android")
        urls.add("https://blog.yanjingtp.cn/tags/androidstudio")
        urls.add("https://blog.yanjingtp.cn/tags/mac")
        urls.add("https://blog.yanjingtp.cn/tags/flutter")
        urls.add("https://blog.yanjingtp.cn/tags/tmp2.0")
        urls.add("https://blog.yanjingtp.cn/tags/%E6%97%B6%E5%85%89%E6%9C%BA%E5%99%A8%E5%A4%87%E4%BB%BD")
        urls.add("https://blog.yanjingtp.cn/tags/solo")
        urls.add("https://blog.yanjingtp.cn/tags/adb")
        urls.add("https://blog.yanjingtp.cn/tags/win11")
        urls.add("https://blog.yanjingtp.cn/archives/2022/02")
        urls.add("https://blog.yanjingtp.cn/archives/2022/01")
        urls.add("https://blog.yanjingtp.cn/tags.html")
        urls.add("https://blog.yanjingtp.cn/archives.html")
        urls.add("https://blog.yanjingtp.cn")


        try {
            //此处解析站点地图,
            // TODO: 需要根据自己的站点地图结构进行调整,可参考我的站点地图https://blog.yanjingtp.cn/rss.xml
            // TODO: 填入自己的站点地图地址如:https://blog.yanjingtp.cn/rss.xml
            val rss = ""
            val saxReader = SAXReader()
            val document: org.dom4j.Document = saxReader.read(rss)
            val rootElement: Element = document.rootElement
            val channel: Element = rootElement.elements("channel")[0] as Element

            val list: List<Element> = channel.elements("item") as List<Element>
            for (element in list) {
                val list1: List<Element> = element.elements() as List<Element>
                for (e in list1) {
                    if (e.name.equals("link")) {
                        urls.add(e.text)
                    }
                }
            }
        } catch (e: DocumentException) {
            e.printStackTrace()
            println(e.message)
        }

        //保存url
        try {
            //TODO:需要保存url的路径,为空则不保存,如:"/www/wwwroot/blog.yanjingtp.cn/Sitemap.txt"
            val path = ""
            if (path.isEmpty()) {
                return urls
            }
            val file = File(path)
            if (file.exists()) {
                file.delete()
                file.createNewFile()
            }
            println(file.absolutePath)
            val writer = BufferedWriter(FileWriter(file))
            for (url in urls) {
                writer.write(url)
                writer.newLine()
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return urls
    }

private fun post(PostUrl: String?, Parameters: List<String>?): String? {
    if (null == PostUrl || null == Parameters || Parameters.isEmpty()) {
        return null
    }
    var result: String? = ""
    var out: PrintWriter? = null
    var `in`: BufferedReader? = null
    try {
        //建立URL之间的连接
        val conn = URL(PostUrl).openConnection()
        //设置通用的请求属性
        conn.setRequestProperty("Host", "data.zz.baidu.com")
        conn.setRequestProperty("User-Agent", "curl/7.12.1")
        conn.setRequestProperty("Content-Length", "83")
        conn.setRequestProperty("Content-Type", "text/plain")

        //发送POST请求必须设置如下两行
        conn.doInput = true
        conn.doOutput = true

        //获取conn对应的输出流
        out = PrintWriter(conn.getOutputStream())
        //发送请求参数
        var param = ""
        for (s in Parameters) {
            param += """
                    $s
                    
                    """.trimIndent()
        }
        out.print(param.trim { it <= ' ' })
        //进行输出流的缓冲
        out.flush()
        //通过BufferedReader输入流来读取Url的响应
        `in` = BufferedReader(InputStreamReader(conn.getInputStream()))
        var line: String?
        while (`in`.readLine().also { line = it } != null) {
            result += line
        }
    } catch (e: Exception) {
        println("发送post请求出现异常！$e")
        e.printStackTrace()
    } finally {
        try {
            out?.close()
            `in`?.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
    return result
}
