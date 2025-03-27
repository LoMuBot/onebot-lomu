package cn.luorenmu.service

import cn.luorenmu.action.request.DeepSeekRequestData
import cn.luorenmu.listen.entity.MessageSender
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

/**
 * @author LoMu
 * Date 2025.02.01 19:00
 */

@Service
class ChatService(
    private val deepSeekRequestData: DeepSeekRequestData,
) {
    private val log = KotlinLogging.logger { }

    /**
     * 由ai来判断哪一条消息最适合作为关键词
     * @params keywords 关键词組成的列表
     * @params reply 回复
     * @return 关键词
     */
    fun extractKeywordsFromReply(keywords: MutableList<String>, reply: String) {
        val prompt = """
             请根据以下对话内容，完成以下任务：
            1. 判断对话内容中哪些关键词最适合作为回复关键词，并输出一个关键词列表(如果没有符合的关键词请为is_empty设置为true)，以逗号分隔。
            2. 如果回复中包含: "cqhttp协议视频"、"cqhttp协议json"、"cqhttp协议语音"、"非cqhttp超链接" 则回复为关键词列表应当为空
            3. 如果回复中包含: "cqhttp协议图片"、"cqhttp协议at"、"cqhttp协议reply"请提取其中的图片名、去除at/reply
            4. 按照"json"的格式输出结果，例如："{keywors:["早上好哦~","早上好"],reply:"你也是",is_empty:false}"
           
             关键词的内容：
            $keywords

            回复内容：
            $reply
            
            请为回复内容筛选出最合适的关键词，并输出结果：
        """
        val resp = deepSeekRequestData.buildRequest(prompt, "user") {
            it.responseFormat.type = "json_object"
            it
        }
        val body = resp.body()
        log.info { body }
    }


    fun chat(messageSender: MessageSender): String? {
        return null
    }
}