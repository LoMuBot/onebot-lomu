package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.GroupMessage
import org.springframework.data.mongodb.repository.MongoRepository


/**
 * @author LoMu
 * Date 2024.07.04 10:47
 */


interface GroupMessageRepository: MongoRepository<GroupMessage, String> {

}