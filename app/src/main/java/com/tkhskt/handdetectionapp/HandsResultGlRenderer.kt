package com.tkhskt.handdetectionapp

import android.opengl.GLES20
import com.google.mediapipe.solutioncore.ResultGlRenderer
import com.google.mediapipe.solutions.hands.HandsResult


/** A custom implementation of [ResultGlRenderer] to render [HandsResult].  */
class HandsResultGlRenderer : ResultGlRenderer<HandsResult> {
    private var program = 0

    override fun setupRendering() {
    }

    override fun renderResult(result: HandsResult?, projectionMatrix: FloatArray) {
        if (result == null) {
            return
        }
    }

    /**
     * Deletes the shader program.
     *
     *
     * This is only necessary if one wants to release the program while keeping the context around.
     */
    fun release() {
        GLES20.glDeleteProgram(program)
    }
}