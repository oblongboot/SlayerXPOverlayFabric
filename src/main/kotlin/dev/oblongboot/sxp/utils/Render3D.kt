package dev.oblongboot.sxp.utils

import dev.oblongboot.sxp.events.Context
import dev.oblongboot.sxp.utils.render.Layers
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

// all these must be called with something like @EventHandler fun onRender(event: WorldRenderEvent) to work properly
// the file with @EventHandler must be subscribed to the event bus in onInitialise func in slayerxpoverlay.kt
object Render3D {
    
    /**
     * Renders a filled box in 3D space
     * 
     * @param ctx The rendering context with all the matrix/buffer stuff
     * @param x Starting X coordinate
     * @param y Starting Y coordinate
     * @param z Starting Z coordinate
     * @param width How wide the box should be
     * @param height How tall the box should be
     * @param depth How deep the box should be
     * @param color What color to make it
     * @param phase if true, there is no depth check
     * @param translate Whether to translate relative to camera position
     */
    fun renderFilledBox(
        ctx: Context,
        x: Double, y: Double, z: Double,
        width: Double, height: Double, depth: Double,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {

        if (!ctx.stacksInit) return

        var cx = x
        var cy = y
        var cz = z

        val layer = if (phase) Layers.QUADS_ESP else Layers.QUADS
        val camPos = ctx.camera.cameraPos.negate()

        if (!phase) {
            cx += 0.003
            cy += 0.003
            cz += 0.003
        }

        if (translate) {
            ctx.matrixStack!!.push()
            ctx.matrixStack!!.translate(camPos.x, camPos.y, camPos.z)
        }

        val matrix = ctx.matrixStack!!.peek().positionMatrix
        val buffer = ctx.consumers.getBuffer(layer)

        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val a = color.alpha / 255f

        val minX = cx
        val minY = cy
        val minZ = cz
        val maxX = cx + width
        val maxY = cy + height
        val maxZ = cz + depth

        val vertices = listOf(
            Vec3d(minX, minY, minZ), Vec3d(maxX, minY, minZ),
            Vec3d(maxX, minY, maxZ), Vec3d(minX, minY, maxZ),
            Vec3d(minX, maxY, minZ), Vec3d(minX, maxY, maxZ),
            Vec3d(maxX, maxY, maxZ), Vec3d(maxX, maxY, minZ),
            Vec3d(minX, minY, minZ), Vec3d(minX, maxY, minZ),
            Vec3d(maxX, maxY, minZ), Vec3d(maxX, minY, minZ),
            Vec3d(maxX, minY, maxZ), Vec3d(maxX, maxY, maxZ),
            Vec3d(minX, maxY, maxZ), Vec3d(minX, minY, maxZ),
            Vec3d(minX, minY, minZ), Vec3d(minX, minY, maxZ),
            Vec3d(minX, maxY, maxZ), Vec3d(minX, maxY, minZ),
            Vec3d(maxX, minY, minZ), Vec3d(maxX, maxY, minZ),
            Vec3d(maxX, maxY, maxZ), Vec3d(maxX, minY, maxZ)
        )

        vertices.forEach { vec3d ->
            buffer.vertex(matrix, vec3d.x.toFloat(), vec3d.y.toFloat(), vec3d.z.toFloat())
                .color(r, g, b, a)
        }

        if (translate) ctx.matrixStack!!.pop()
    }
    
    /**
     * Renders a filled box using a Box object instead of coordinates
     * Basically just a convenience wrapper so you don't have to manually calculate dimensions
     * 
     * @param ctx Rendering context
     * @param box The Box object containing position and size info
     * @param color What color to render it
     * @param phase if true, there is no depth check
     * @param translate Whether to apply camera translation
     */
    fun renderFilledBox(
        ctx: Context,
        box: Box,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        renderFilledBox(
            ctx,
            box.minX, box.minY, box.minZ,
            box.lengthX, box.lengthY, box.lengthZ,
            color, phase, translate
        )
    }
    
    /**
     * Draws just the outline of a box (wireframe)
     * 
     * @param ctx Rendering context
     * @param x Starting X position
     * @param y Starting Y position
     * @param z Starting Z position
     * @param width Box width
     * @param height Box height
     * @param depth Box depth
     * @param color Line color
     * @param phase if true, there is no depth check
     * @param translate Whether to translate relative to camera
     */
    fun renderOutlinedBox(
        ctx: Context,
        x: Double, y: Double, z: Double,
        width: Double, height: Double, depth: Double,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        if (!ctx.stacksInit) return

        val layer = if (phase) Layers.LINES_ESP else Layers.LINES
        val camPos = ctx.camera.cameraPos.negate()

        if (translate) {
            ctx.matrixStack!!.push()
            ctx.matrixStack!!.translate(camPos.x, camPos.y, camPos.z)
        }

        val matrix = ctx.matrixStack!!.peek().positionMatrix
        val buffer = ctx.consumers.getBuffer(layer)

        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val a = color.alpha / 255f

        val minX = x.toFloat()
        val minY = y.toFloat()
        val minZ = z.toFloat()
        val maxX = (x + width).toFloat()
        val maxY = (y + height).toFloat()
        val maxZ = (z + depth).toFloat()

        val vertices = listOf(
            Vec3d(minX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(minX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), minY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), minZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(maxX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), maxZ.toDouble()),
            Vec3d(minX.toDouble(), maxY.toDouble(), minZ.toDouble())
        )

        val normals = mutableListOf<Vec3d>()

        for (i in 0 until vertices.size - 1 step 2) {
            val start = vertices[i]
            val end = vertices[i + 1]
            val direction = end.subtract(start).normalize()
            normals.add(direction)
            normals.add(direction)
        }

        vertices.zip(normals).forEach { (vec3d, normal) ->
            buffer.vertex(
                matrix,
                vec3d.x.toFloat(),
                vec3d.y.toFloat(),
                vec3d.z.toFloat()
            )
                .color(r, g, b, a)
                .normal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
        }

        if (translate) ctx.matrixStack!!.pop()
    }
    
    /**
     * Draws a box outline using a Box object
     * Same as the other one but takes a Box instead of raw coordinates
     * 
     * @param ctx Rendering context
     * @param box Box object with the position/dimensions
     * @param color What color for the outline
     * @param phase if true, there is no depth check
     * @param translate Camera translation toggle
     */
    fun renderOutlinedBox(
        ctx: Context,
        box: Box,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        renderOutlinedBox(
            ctx,
            box.minX, box.minY, box.minZ,
            box.lengthX, box.lengthY, box.lengthZ,
            color, phase, translate
        )
    }
    
    /**
     * Draws a line between two points in 3D space
     * Pretty straightforward - start point to end point
     * 
     * @param ctx Rendering context
     * @param start Where the line begins
     * @param end Where the line ends
     * @param color Line color
     * @param phase if true, there is no depth check
     * @param translate Camera translation toggle
     */
    fun renderLine(
        ctx: Context,
        start: Vec3d,
        end: Vec3d,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        if (!ctx.stacksInit) return
        
        val layer = if (phase) Layers.LINES_ESP else Layers.LINES
        val camPos = ctx.camera.cameraPos.negate()
        
        if (translate) {
            ctx.matrixStack!!.push()
            ctx.matrixStack!!.translate(camPos.x, camPos.y, camPos.z)
        }
        
        val matrixEntry = ctx.matrixStack!!.peek()
        val buffer = ctx.consumers.getBuffer(layer)
        
        buffer.vertex(matrixEntry, start.x.toFloat(), start.y.toFloat(), start.z.toFloat())
            .color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            .normal(matrixEntry, 0f, 1f, 0f)
        buffer.vertex(matrixEntry, end.x.toFloat(), end.y.toFloat(), end.z.toFloat())
            .color(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            .normal(matrixEntry, 0f, 1f, 0f)
        
        if (translate) ctx.matrixStack!!.pop()
    }
    
    /**
     * Draws a tracer line from your camera to a target position
     * like those lines pointing to entities in ESP mods
     * it's not hard to explain, just a tracer
     * 
     * @param ctx Rendering context
     * @param target Where the line should point to
     * @param color Tracer color
     * @param phase if true, there is no depth check
     */
    fun renderTracer(
        ctx: Context,
        target: Vec3d,
        color: Color,
        phase: Boolean = false
    ) {
        if (!ctx.stacksInit) return
        
        ctx.matrixStack!!.push()
        ctx.matrixStack!!.translate(-ctx.camera.cameraPos.x, -ctx.camera.cameraPos.y, -ctx.camera.cameraPos.z)
        
        renderLine(ctx, Vec3d.ZERO, target, color, phase, false)
        
        ctx.matrixStack!!.pop()
    }
    
    /**
     * Draws an outline around a specific block position
     * Useful for highlighting blocks you're targeting or whatever
     * 
     * @param ctx Rendering context
     * @param pos The block position to outline
     * @param color Outline color
     * @param phase if true, there is no depth check
     * @param translate Camera translation toggle
     */
    fun renderBlockOutline(
        ctx: Context,
        pos: BlockPos,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        val box = Box(pos)
        renderOutlinedBox(ctx, box, color, phase, translate)
    }
    
    /**
     * Renders a solid filled block at the given position
     * Makes the whole block colored, not just an outline
     * 
     * @param ctx Rendering context
     * @param pos Block position to fill
     * @param color Fill color
     * @param phase if true, there is no depth check
     * @param translate Camera translation toggle
     */
    fun renderFilledBlock(
        ctx: Context,
        pos: BlockPos,
        color: Color,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        val box = Box(pos)
        renderFilledBox(ctx, box, color, phase, translate)
    }
    
    /**
     * Draws a wireframe sphere in 3D space
     * Uses latitude/longitude lines to create the sphere effect
     * 
     * @param ctx Rendering context
     * @param center Center point of the sphere
     * @param radius How big the sphere should be
     * @param color Wireframe color
     * @param segments Number of lines to use (more = smoother but slower)
     * @param phase if true, there is no depth check
     * @param translate Camera translation toggle
     */
    fun renderSphere(
        ctx: Context,
        center: Vec3d,
        radius: Double,
        color: Color,
        segments: Int = 16,
        phase: Boolean = false,
        translate: Boolean = true
    ) {
        if (!ctx.stacksInit) return
        
        val layer = if (phase) Layers.LINES_ESP else Layers.LINES
        val camPos = ctx.camera.cameraPos.negate()
        
        if (translate) {
            ctx.matrixStack!!.push()
            ctx.matrixStack!!.translate(camPos.x, camPos.y, camPos.z)
        }
        
        val matrixEntry = ctx.matrixStack!!.peek()
        val buffer = ctx.consumers.getBuffer(layer)
        
        val cx = center.x
        val cy = center.y
        val cz = center.z
        
        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val a = color.alpha / 255f

        for (i in 0..segments) {
            val lat = Math.PI * i / segments
            val sinLat = sin(lat)
            val cosLat = cos(lat)
            
            for (j in 0 until segments) {
                val lng1 = 2 * Math.PI * j / segments
                val lng2 = 2 * Math.PI * (j + 1) / segments
                
                val x1 = cx + radius * sinLat * cos(lng1)
                val y1 = cy + radius * cosLat
                val z1 = cz + radius * sinLat * sin(lng1)
                
                val x2 = cx + radius * sinLat * cos(lng2)
                val z2 = cz + radius * sinLat * sin(lng2)
                
                buffer.vertex(matrixEntry, x1.toFloat(), y1.toFloat(), z1.toFloat())
                    .color(r, g, b, a)
                    .normal(matrixEntry, 0f, 1f, 0f)
                buffer.vertex(matrixEntry, x2.toFloat(), y1.toFloat(), z2.toFloat())
                    .color(r, g, b, a)
                    .normal(matrixEntry, 0f, 1f, 0f)
            }
        }
        
        if (translate) ctx.matrixStack!!.pop()
    }
    
    /**
    * checks if context is ready or something
    * likely always returns true
     */
    private val Context.stacksInit: Boolean
        get() = this.matrixStack != null
}