package cn.luorenmu

/**
 * @author LoMu
 * Date 2024.07.04 23:55
 */


fun main() {
    val str =
        "[CQ:image,file=E4A1E20B09AA5851140415C9885B0405.jpg,subType=1,url=https://multimedia.nt.qq.com.cn/download?appid=1407&amp;fileid=CgoyODQyNzc1NzUyEhTXENNHcmuWNT3nYZWBPT8RJjl8lxiV8AMg_wooxLjrjsjPhwNQgL2jAQ&amp;spec=0&amp;rkey=CAQSKAB6JWENi5LMJfLItdMvgziGzbZN6pt-oiroEbFeNLY8MSeK8l2ake4,file_size=63509]"
    if (str.startsWith("[CQ:image") && str.endsWith("]")) {
        val regex = """file=([^,]+)""".toRegex()
        val matchResult = regex.find(str)
        if (matchResult != null) {
            val fileValue = matchResult.groupValues[1]
            println("File value: $fileValue")
        } else {
            println("No file parameter found.")
        }
    }
}
