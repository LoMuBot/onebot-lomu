package cn.luorenmu.entiy

import cn.luorenmu.listen.log
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

/**
 * @author LoMu
 * Date 2024.07.05 8:23
 */

class RecentlyMessageQueue<T>(private val maxSize: Int = 20) {

    /**
     *  存储近maxSize条最新消息
     *  key为addMessageToQueue中传入的形参Id
     *  value为List<GroupMessage>
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
    fun lastMessages(id: Long, num: Int): ArrayList<T?> {
        if (map[id] == null) {
            return ArrayList()
        }
        var num1 = num
        if (num1 > map[id]!!.size) {
            num1 = map[id]!!.size
        }
        val list = arrayListOf<T?>()
        mapCurrentPoint[id]?.let {

            for (i in 1..num1) {
                var index = it - i
                if (index < 0) {
                    index += maxSize
                }
                try {
                    list.add(map[id]?.get(index))
                } catch (e: IndexOutOfBoundsException) {
                    log.error {
                        "IndexOutOfBoundsException -> ${e.message}  \n " +
                                "-> size : ${map[id]!!.size} \n" +
                                "-> queue.toString() : ${map[id]!!.joinToString { queue -> queue.toString() }} \n" +
                                "-> id: Long, num: Int : ${id},${num}}"
                    }
                }

            }
        } ?: let {
            if (it.map.isNotEmpty()) {
                var index = map[id]!!.size
                for (i in 0..<num1) {
                    list.add(map[id]?.get(--index))
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