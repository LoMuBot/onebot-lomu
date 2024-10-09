package cn.luorenmu.task.entiy

/**
 * @author LoMu
 * Date 2024.09.26 20:11
 */
data class CourseDataApiResponse(
    val success: Boolean,
    val status: Int,
    val msg: String,
    val data: List<CourseData>,
)

data class CourseData(
    val week: String,
    val lesson: Int,
    val time: Int,
    var subject: String,
)