package cn.luorenmu.entiy


import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * @author LoMu
 * Date 2024.07.05 8:23
 */

class RecentlyMessageQueue<T>(private val maxSize: Int = 20) {
    private val log = KotlinLogging.logger {}

    /**
     *  存储近maxSize条最新消息
     *  key为addMessageToQueue中传入的形参Id
     *  value为List<T>
     */
    val map: MultiValueMap<Long, T> = LinkedMultiValueMap()


    /**
     * 消息指针
     * 确定下一条消息存放的位置
     * !在队列没有被填满的情况下 指针的值为null
     * 只有在队列被填满时指针才会开始从0开始移动
     */
    private val mapCurrentPoint: MutableMap<Long, Int> = mutableMapOf()

    /**
     * 上一条消息
     */
    fun lastMessage(id: Long): T? {
        return lastMessages(id, 1).firstOrNull()
    }

    /**
     * 返回最新num条消息
     * @param id id
     * @param num 消息数
     */
    fun lastMessages(id: Long, num: Int): ArrayList<T> {
        val messages = map[id] ?: return ArrayList()

        val num1 = if (num > messages.size) messages.size else num
        val list = arrayListOf<T>()

        mapCurrentPoint[id]?.let { currentPoint ->
            for (i in 1..num1) {
                val index = (currentPoint - i + maxSize) % maxSize
                messages.getOrNull(index)?.let {
                    list.add(it)
                } ?: run {
                    log.warn { "$index 消息队列为空" }
                }
            }
        } ?: run {
            if (messages.isNotEmpty()) {
                var index = messages.size
                for (i in 0 until num1) {
                    messages.getOrNull(--index)?.let {
                        list.add(it)
                    } ?: run {
                        log.warn { "$index 消息队列为空" }
                    }
                }
            }
        }

        list.reverse()
        return list
    }


    fun addMessageToQueue(id: Long, action: T) {
        var point = mapCurrentPoint[id] ?: 0
        if (point >= maxSize) {
            mapCurrentPoint[id] = 0
            point = 0
        }

        val groupMessageList = map[id]
        groupMessageList?.let {
            if (groupMessageList.size >= maxSize) {
                groupMessageList[point] = action
                mapCurrentPoint[id] = point + 1
            } else {
                groupMessageList.add(action)
            }
        } ?: run {
            map.add(id, action)
        }

    }
}