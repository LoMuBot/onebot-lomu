package cn.luorenmu.task.entiy

/**
 * @author LoMu
 * Date 2024.09.27 21:07
 */
data class WorkDate(
    val code: Int,
    val data: WorkDateData,
    val msg: String,
)

data class WorkDateData(
    val date: String,
    val work: Boolean,
)