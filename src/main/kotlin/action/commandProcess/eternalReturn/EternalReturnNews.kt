package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.commandProcess.eternalReturn.entity.EternalReturnNewsCache
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.request.RequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnOfficialWebsiteScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.sendGroupForwardMsg
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


/**
 * @author LoMu
 * Date 2025.02.20 18:01
 */

@Component("eternalReturnNews")
class EternalReturnNews(
    private val eternalReturnOfficialWebsiteScreenshot: EternalReturnOfficialWebsiteScreenshot,
    private val redisUtils: RedisUtils,
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val botContainer: BotContainer,
    private val requestData: RequestData,
) : CommandProcess {

    private val log = KotlinLogging.logger {}
    val regex = "<div class=\"er-article-detail__content er-article-content fr-view\">([\\s\\S]*?)</div>".toRegex()
    val imgRegex = "<img src=\"(.*?)\"".toRegex()

    /**
     * @param command 正则表达式 -> https://playeternalreturn.com/posts/news/([0-9]{4,6})
     */
    override fun process(command: String, sender: MessageSender): String? {
        val regex = command.toRegex()
        // 匹配到该命令必然存在
        val newsId = regex.find(sender.message)!!.groups[1]!!.value
        val news = eternalReturnRequestData.news(newsId) ?: run { return null }
        val messages = redisUtils.getCache("news:${newsId}", EternalReturnNewsCache::class.java, {
            val path = ReadWriteFile.CURRENT_PATH.substring(1) + "image/eternal_return/news/${newsId}"
            File(path).mkdirs()
            val screenshotPath = eternalReturnOfficialWebsiteScreenshot.screenshotNews(newsId, "${path}/$newsId.png")
            val forwardMessages = mutableListOf<String>()

            if (imageSplitter(screenshotPath, "${path}/$newsId")) {
                val screenshotPath1 = "${path}/${newsId}-1.png"
                val screenshotPath2 = "${path}/${newsId}-2.png"
                forwardMessages.add(screenshotPath1)
                forwardMessages.add(screenshotPath2)
            } else {
                forwardMessages.add(screenshotPath)
            }

            // 图片内容
            val articleImages = getArticleImages(news)
            if (articleImages.isNotEmpty()) {
                val localImages = downloadImg(path, articleImages)
                forwardMessages.addAll(localImages)
            }
            val messagesConvertCQ =
                forwardMessages.map { MsgUtils.builder().img(it).build() }.toMutableList()
            EternalReturnNewsCache(messagesConvertCQ)
        }, 30L, TimeUnit.DAYS)
        messages?.let {
            botContainer.getFirstBot().sendGroupForwardMsg(sender.groupOrSenderId, it.messages)
        }
        return null
    }

    private fun downloadImg(outputPath: String, imageUrls: MutableList<String>): MutableList<String> {
        val localImages = mutableListOf<String>()
        imageUrls.forEach {
            val lastIndexOf = it.lastIndexOf(".")
            val type = it.substring(lastIndexOf)
            val imgPath = "${outputPath}/${UUID.randomUUID()}.$type"
            requestData.downloadStream(it, imgPath)
            localImages.add(imgPath)
        }
        return localImages
    }


    /**
     * 判断图片是否需要分隔 这里约定 所有分隔的图片为原名称-1 原名称-2
     * @param imgPath 图片路径
     * @param outputPath 输出的路径没有标明文件类型
     * @return 是否进行了分隔
     */
    private fun imageSplitter(imgPath: String, outputPath: String): Boolean {
        try {
            val inputFile = File(imgPath)
            val image = ImageIO.read(inputFile)
            val width = image.width
            val height = image.height
            if (height > 3000) {
                val middleY = height / 2
                val topHalf = image.getSubimage(0, 0, width, middleY)
                val bottomHalf = image.getSubimage(0, middleY, width, height - middleY)
                val outputFile1 = File("${outputPath}-1.png")
                val outputFile2 = File("${outputPath}-2.png")

                ImageIO.write(topHalf, "png", outputFile1)
                ImageIO.write(bottomHalf, "png", outputFile2)

                return true
            }
        } catch (e: IOException) {
            log.error { e.printStackTrace() }
        }
        return false
    }

    /**
     * 获取所有图片
     */
    private fun getArticleImages(body: String): MutableList<String> {
        val list = mutableListOf<String>()
        regex.find(body)?.let {
            it.groups[1]?.let { value ->
                for (matchResult in imgRegex.findAll(value.value)) {
                    matchResult.groups[1]?.let { imgUrl ->
                        list.add(imgUrl.value)
                    }
                }
            }
        }
        return list
    }

    override fun commandName() = "eternalReturnNews"

    override fun state(id: Long) = true
}