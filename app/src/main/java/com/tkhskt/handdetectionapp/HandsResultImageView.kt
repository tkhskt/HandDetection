package com.tkhskt.handdetectionapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint

import androidx.appcompat.widget.AppCompatImageView
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsResult


/** An ImageView implementation for displaying [HandsResult].  */
class HandsResultImageView(context: Context) : AppCompatImageView(context) {

    private var latest: Bitmap? = null

    /**
     * Sets a [HandsResult] to render.
     *
     * @param result a [HandsResult] object that contains the solution outputs and the input
     * [Bitmap].
     */
    fun setHandsResult(result: HandsResult?) {
        if (result == null) {
            return
        }
        val bmInput: Bitmap = result.inputBitmap()
        val width = bmInput.width
        val height = bmInput.height
        val latest = Bitmap.createBitmap(width, height, bmInput.config).also {
            this.latest = it
        }
        val canvas = Canvas(latest)
        canvas.drawBitmap(bmInput, Matrix(), null)
        val numHands: Int = result.multiHandLandmarks().size
        for (i in 0 until numHands) {
            drawLandmarksOnCanvas(
                result.multiHandLandmarks().get(i).getLandmarkList(),
                result.multiHandedness().get(i).getLabel().equals("Left"),
                canvas,
                width,
                height
            )
        }
    }

    /** Updates the image view with the latest [HandsResult].  */
    fun update() {
        postInvalidate()
        if (latest != null) {
            setImageBitmap(latest)
        }
    }

    private fun drawLandmarksOnCanvas(
        handLandmarkList: List<LandmarkProto.NormalizedLandmark>,
        isLeftHand: Boolean,
        canvas: Canvas,
        width: Int,
        height: Int
    ) {
        // Draw connections.
        for (c in Hands.HAND_CONNECTIONS) {
            val connectionPaint = Paint()
            connectionPaint.setColor(
                if (isLeftHand) LEFT_HAND_CONNECTION_COLOR else RIGHT_HAND_CONNECTION_COLOR
            )
            connectionPaint.setStrokeWidth(CONNECTION_THICKNESS)
            val start: LandmarkProto.NormalizedLandmark = handLandmarkList[c.start()]
            val end: LandmarkProto.NormalizedLandmark = handLandmarkList[c.end()]
            canvas.drawLine(
                start.getX() * width,
                start.getY() * height,
                end.getX() * width,
                end.getY() * height,
                connectionPaint
            )
        }
        val landmarkPaint = Paint()
        landmarkPaint.setColor(if (isLeftHand) LEFT_HAND_LANDMARK_COLOR else RIGHT_HAND_LANDMARK_COLOR)
        // Draws landmarks.
        for (landmark in handLandmarkList) {
            canvas.drawCircle(
                landmark.getX() * width, landmark.getY() * height, LANDMARK_RADIUS, landmarkPaint
            )
        }
        // Draws hollow circles around landmarks.
        landmarkPaint.setColor(
            if (isLeftHand) LEFT_HAND_HOLLOW_CIRCLE_COLOR else RIGHT_HAND_HOLLOW_CIRCLE_COLOR
        )
        landmarkPaint.setStrokeWidth(HOLLOW_CIRCLE_WIDTH)
        landmarkPaint.setStyle(Paint.Style.STROKE)
        for (landmark in handLandmarkList) {
            canvas.drawCircle(
                landmark.getX() * width,
                landmark.getY() * height,
                LANDMARK_RADIUS + HOLLOW_CIRCLE_WIDTH,
                landmarkPaint
            )
        }
    }


    companion object {
        private const val TAG = "HandsResultImageView"

        private val LEFT_HAND_CONNECTION_COLOR = Color.parseColor("#30FF30")
        private val RIGHT_HAND_CONNECTION_COLOR = Color.parseColor("#FF3030")
        private const val CONNECTION_THICKNESS = 8f // Pixels

        private val LEFT_HAND_HOLLOW_CIRCLE_COLOR = Color.parseColor("#30FF30")
        private val RIGHT_HAND_HOLLOW_CIRCLE_COLOR = Color.parseColor("#FF3030")
        private const val HOLLOW_CIRCLE_WIDTH = 5f // Pixels

        private val LEFT_HAND_LANDMARK_COLOR = Color.parseColor("#FF3030")
        private val RIGHT_HAND_LANDMARK_COLOR = Color.parseColor("#30FF30")
        private const val LANDMARK_RADIUS = 10f // Pixels
    }

    init {
        scaleType = ScaleType.FIT_CENTER
    }
}