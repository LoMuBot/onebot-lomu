package cn.luorenmu.handle

import cn.luorenmu.listen.log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.08.25 06:43
 */

@Component
@Aspect
class GlobalExceptionHandle {

    @Pointcut("execution(* cn.luorenmu.listen.GroupEventListenKt.*(..))")
    fun groupEventListenMethod() {
    }


    @AfterThrowing(pointcut = "groupEventListenMethod()", throwing = "ex")
    fun logAfterThrowing(joinPoint: JoinPoint, ex: Throwable) {
        println("Exception in method: ${joinPoint.signature}, Exception: ${ex.message}")
    }
}