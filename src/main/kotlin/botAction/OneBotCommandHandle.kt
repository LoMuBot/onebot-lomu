package cn.luorenmu.service

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.entiy.Command
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.repository.GroupMessageRepository
import cn.luorenmu.request.RequestController
import cn.luorenmu.web.WebPageScreenshot
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import org.springframework.stereotype.Service

/**
 * @author LoMu
 * Date 2024.07.13 1:52
 */
@Service
class OneBotCommandHandle(
    private val webPageScreenshot: WebPageScreenshot,
    private val groupMessageRepository: GroupMessageRepository
) {


    fun isCommand(command: String): Boolean {
        for (value in Command.entries) {
            if (command.contains(value.str)) {
                return true
            }
        }
        return false
    }

    fun process(command: String, senderId: Long, msgId: Int): String {
        if (command.contains(Regex(Command.FF14Bind.str))) {
            return ff14Bind(senderId)
        }

        if (command.contains(Command.MihoyoBind.str)) {
            return "not support"
        }

        if (command.contains(Command.EternalReturnPlayers.str)) {
            return eternalReturnFindPlayers(command, msgId)
        }

       /* if(command.contains(Regex(Command.WhoAtMe.str))){
            return whoAtMe(senderId)
        }*/


        // When the program sends this message, it means that an unknown error has occurred internally
        return "error internally"
    }

    private fun whoAtMe(id: Long): String {
        return "not support"
    }

    private fun eternalReturnFindPlayers(command: String, id: Int): String {
        val nickname = command.replace(" ", "").replace(Command.EternalReturnPlayers.str, "")
        if (nickname.isBlank()) {
            return ""
        }
        var url = JsonObjectUtils.getString("request.eternal_return_request.players")
        url = MatcherData.replaceDollardName(url, "nickname", nickname)
        val path = ReadWriteFile.currentPathFileName("image/${nickname}.png").substring(1)
        synchronized(webPageScreenshot){
            webPageScreenshot.setHttpUrl(url).screenshotAllCrop(381, 150, 1131, -500, 2000).outputImageFile(path)
        }
        return MsgUtils.builder().reply(id).img(OneBotMedia().file(path).cache(false).proxy(false)).build()
    }

    private fun ff14Bind(id: Long): String {
        val requestController = RequestController("ff14_request.create_qrcode")
        val response = requestController.request()
        val path = ReadWriteFile.currentPathFileName("qrcode/${id}.png").substring(1)

        val file = ReadWriteFile.writeStreamFile(
            path,
            response.bodyStream()
        )
        val img = MsgUtils.builder().at(id).img(OneBotMedia().file(file.absolutePath).cache(false).proxy(false))
        return img.build()
    }
}