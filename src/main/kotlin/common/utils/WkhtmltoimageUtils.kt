package cn.luorenmu.common.utils

import cn.luorenmu.file.ReadWriteFile
import java.io.File
import java.io.IOException

/**
 * @author LoMu
 * Date 2025.03.18 19:37
 */
object WkhtmltoimageUtils {
     private val WKHTMLTOIMAGE_PATH: String = "${ReadWriteFile.CURRENT_PATH}/wkhtmltopdf/bin/wkhtmltoimage.exe"
   /// val WKHTMLTOIMAGE_PATH: String = "E:\\code\\software\\wkhtmltopdf\\bin\\wkhtmltoimage.exe"


    /**
     * 将 HTML 文件转换为图片
     *
     * @param htmlFile HTML 文件路径
     * @param outputImage 输出图片路径
     * @param options 可选参数（如缩放、DPI 等）
     * @return 是否成功
     */
    fun convertHtmlToImage(htmlFile: String, outputImage: String, options: Map<String, String> = emptyMap()): Boolean {
        return convert(htmlFile, outputImage, options)
    }

    /**
     * 将 URL 转换为图片
     *
     * @param url 网页 URL
     * @param outputImage 输出图片路径
     * @param options 可选参数（如缩放、DPI 等）
     * @return 是否成功
     */
    fun convertUrlToImage(url: String, outputImage: String, options: Map<String, String> = emptyMap()): Boolean {
        return convert(url, outputImage, options)
    }

    /**
     * 执行 wkhtmltoimage 命令
     *
     * @param input 输入文件或 URL
     * @param outputImage 输出图片路径
     * @param options 可选参数
     * @return 是否成功
     */
    private fun convert(input: String, outputImage: String, options: Map<String, String>): Boolean {
        // 构建命令
        val command = buildCommand(input, outputImage, options)

        try {
            // 执行命令
            val process = Runtime.getRuntime().exec(command)
            process.waitFor() // 等待命令执行完成

            // 检查输出文件是否存在
            return File(outputImage).exists()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 构建 wkhtmltoimage 命令
     *
     * @param input 输入文件或 URL
     * @param outputImage 输出图片路径
     * @param options 可选参数
     * @return 完整的命令字符串
     */
    private fun buildCommand(input: String, outputImage: String, options: Map<String, String>): Array<String> {
        val command = mutableListOf(WKHTMLTOIMAGE_PATH)

        // 添加可选参数
        options.forEach { (key, value) ->
            command.add("--$key")
            command.add(value)
        }

        // 添加输入和输出路径
        command.add(input)
        command.add(outputImage)

        return command.toTypedArray()
    }

}
