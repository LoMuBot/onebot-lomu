package cn.luorenmu

import cn.luorenmu.common.utils.firstPinYin
import com.mikuac.shiro.common.utils.MsgUtils

/**
 * @author LoMu
 * Date 2024.07.04 23:55
 */


fun main() {
    println(MsgUtils.builder().voice("H:\\bot\\voice\\Emma character song.mp3").build())
}
