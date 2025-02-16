package cn.luorenmu

import io.github.humbleui.skija.*
import io.github.humbleui.types.Rect
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


/**
 * @author LoMu
 * Date 2025.01.31 01:36
 */
fun main() {
    val imageInfo = ImageInfo(800, 600, ColorType.RGB_565, ColorAlphaType.PREMUL)
    val surface = Surface.makeRaster(imageInfo)
    val canvas = surface.canvas
    val paint = Paint()
    paint.color = Color.makeRGB(255, 255, 255)
    val face = FontMgr.getDefault().matchFamilyStyle("微软雅黑", FontStyle.NORMAL)
    val font = Font(face, 50f)
    canvas.drawRect(Rect.makeXYWH(100f, 100f, 200f, 100f), paint);
    canvas.drawString("Hello, world", 100f, 100f, font, paint)
    val image: Image = surface.makeImageSnapshot()

    val pngBytes: ByteBuffer = EncoderPNG.encode(image)!!.toByteBuffer()
    try {
        val path = Path.of("output.png")
        val channel: ByteChannel = Files.newByteChannel(
            path,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
        )
        channel.write(pngBytes)
        channel.close()
    } catch (e: IOException) {
        println(e)
    }
}