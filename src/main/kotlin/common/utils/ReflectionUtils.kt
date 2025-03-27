package cn.luorenmu.common.utils

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * @author LoMu
 * Date 2025.03.18 23:52
 */
object ReflectionUtils {

    /**
     * 将对象转换为url参数
     */
    fun <T : Any> convertObjToUrlParams(kClass: KClass<T>, obj: T): String {
        val fields = kClass.declaredMemberProperties
        return fields.map { property ->
            property.isAccessible = true

            val value = property.get(obj) ?: return@map ""
            "${property.name}=$value"
        }.joinToString("&")
    }
}