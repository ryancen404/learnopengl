package com.ryancen.airhockey

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class AirHockeyRenderer {
    companion object {
        const val POSITION_COMPONENT_COUNT = 2

        // 每个浮点数都占用4个字节
        const val BYTES_PER_FLOAT = 4

    }

    // 定义顶点坐标
    val tableVerticesWithTriangles = floatArrayOf(
        // 长方形
        // Triangle 1
        0f, 0f,
        9f, 14f,
        0f, 14f,

        // Triangle 2
        0f, 0f,
        9f, 0f,
        9f, 14f,

        // Line 1； 中间线
        0f, 7f,
        9f, 7f,

        // Mallets; 木槌
        4.5f, 2f,
        4.5f, 12f
    )

    // 顶点数据
    val vertexData: FloatBuffer = ByteBuffer
        // 分配一块本地内存，这块内存不会被垃圾回收器管理
        .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
        // 指定字节顺序，保证数据排列正确
        .order(ByteOrder.nativeOrder())
        // 从ByteBuffer创建一个浮点缓冲区
        .asFloatBuffer()
        // 把数据从 虚拟机 的内存复制到本地内存
        .put(tableVerticesWithTriangles)


}