package core.layout

import java.awt.*
import java.awt.image.BufferedImage
import java.util.*


/**
 * @author LoMu
 * Date 2025.02.04 19:19
 */
class LayoutModel {
    // 基础坐标容器
    var image: BufferedImage? = null
    var g2d: Graphics2D? = null
    var currentX = 0
    var currentY = 0
    private val stack: Deque<Point> = ArrayDeque()


    fun LayoutSystem(width: Int, height: Int) {
        image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        g2d = image!!.createGraphics()
        g2d!!.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        resetOrigin(20, 20) // 初始边距
        watermark()
    }

    private fun watermark() {
        g2d!!.font = Font("微软雅黑", Font.BOLD, 8)
        g2d!!.color = Color.gray
        g2d!!.drawString("Powered by LoMu", 0, 8)

        g2d!!.font = Font("微软雅黑", Font.PLAIN, 8)
        g2d!!.color = Color.black
    }

    // 坐标原点控制
    fun pushOrigin(x: Int, y: Int) {
        stack.push(Point(currentX, currentY))
        currentX += x
        currentY += y
    }

    fun popOrigin() {
        val p: Point = stack.pop()
        currentX = p.x
        currentY = p.y
    }

    fun resetOrigin(x: Int, y: Int) {
        currentX = x
        currentY = y
        stack.clear()
    }
}