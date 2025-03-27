package cn.luorenmu.common.aop

import com.mikuac.shiro.dto.event.message.MessageEvent
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.03.21 22:38
 */
@Component
@Aspect
class BlackBlockHandle {
    @Pointcut("@annotation(cn.luorenmu.common.annotation.BlackBlock)")
    fun pt() {

    }

    @Before("pt()")
    fun handle(jp: JoinPoint) {
        val args = jp.args
        for (any in args) {
            if (any is MessageEvent){
                
            }
        }
    }

}