package ru.shumikhin.clockview.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.View.BaseSavedState
import ru.shumikhin.clockview.R
import java.util.*
import kotlin.math.*

class CustomAnalogClock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var mHeight = 0
    private var mWidth = 0
    private val mClockHours = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private var mPadding = 0
    private var mNumeralSpacing = 0
    private var mHourHandTruncation = 0
    private var mMinuteHandTruncation = 0
    private var mSecondHandTruncation = 0
    private var mRadius = 0
    private val mRect = Rect()
    private var isInit = false
    private var savedSize: Int = 0

    // Custom attributes
    private var mTextSize: Float = 11f
    private var textColor: Int = Color.BLACK
    private var clockBackgroundColor: Int = Color.WHITE
    private var clockBorderColor: Int = Color.BLACK
    private var clockBorderWidth: Float = 10f
    private var hourHandColor: Int = Color.BLACK
    private var minuteHandColor: Int = Color.BLACK
    private var secondHandColor: Int = Color.GRAY
    private var hourHandWidth = 18f
    private var minuteHandWidth = 13f
    private var secondHandWidth = 10f
    private var centerPointColor: Int = Color.BLACK
    private var minutesPointColor: Int = Color.BLACK
    private var minutesPointSize: Float = 1f
    private var centerPointSize: Float = 12f
    private var hoursPointSize: Float = 3f

    init {
        setupAttributes(attrs)
    }

    private val backgroundPaint = Paint().apply {
        color = clockBackgroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        color = clockBorderColor
        style = Paint.Style.STROKE
        strokeWidth = clockBorderWidth
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        textSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                mTextSize,
                resources.displayMetrics
            )
    }

    private val minutesPointPaint = Paint().apply {
        style = Paint.Style.FILL
        color = minutesPointColor
    }

    private val centerPointPaint = Paint().apply {
        style = Paint.Style.FILL
        color = centerPointColor
    }

    private val secondHandPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = secondHandWidth
        color = secondHandColor
    }
    private val minuteHandPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = minuteHandWidth
        color = minuteHandColor
    }
    private val hourHandPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = hourHandWidth
        color = hourHandColor
    }

    fun setClockBackgroundColor(color: Int) {
        clockBackgroundColor = color
        backgroundPaint.color = color
    }

    fun setSize(size: Int) {
        savedSize = size
        mRadius = size / 2 - mPadding
        mHourHandTruncation = mRadius / 2
        mMinuteHandTruncation = mRadius - mRadius / 4
        mSecondHandTruncation = mRadius - mRadius / 6
    }

    fun setTextSize(size: Int) {
        mTextSize = size.toFloat()
        textPaint.textSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                size.toFloat(),
                resources.displayMetrics
            )
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            this.backgroundColor = clockBackgroundColor
            this.size = (mRadius + mPadding) * 2
            this.textSize = mTextSize.toInt()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setClockBackgroundColor(state.backgroundColor)
            setSize(state.size)
            setTextSize(state.textSize)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 250
        val desiredHeight = 250

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(
                desiredWidth,
                widthSize
            )

            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(
                desiredHeight,
                heightSize
            )

            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInit) {
            mHeight = height
            mWidth = width
            mPadding = mNumeralSpacing + 50
            val minAttr = if (savedSize == 0)
                min(mHeight, mWidth) else savedSize
            mRadius = minAttr / 2 - mPadding
            mHourHandTruncation = mRadius / 2
            mMinuteHandTruncation = mRadius - mRadius / 4
            mSecondHandTruncation = mRadius - mRadius / 6
            isInit = true
        }

        // Draw background
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mRadius + mPadding - 10).toFloat(),
            backgroundPaint
        )

        // Draw circle border
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mRadius + mPadding - 10).toFloat(),
            borderPaint
        )

        // Draw numerals
        for (hour in mClockHours) {
            val tmp = hour.toString()
            textPaint.getTextBounds(tmp, 0, tmp.length, mRect)
            val angle = PI / 6 * (hour - 3)
            val customAngel = if (hour != 6) angle else angle + 0.009f
            val textPadding = mTextSize.toInt() + 45
            val x =
                (mWidth / 2 + cos(customAngel) * (mRadius + mPadding - textPadding) - mRect.width() / 2).toFloat()
            val y =
                (mHeight / 2 + sin(angle) * (mRadius + mPadding - textPadding) + mRect.height() / 2).toFloat()
            canvas.drawText(tmp, x, y, textPaint)
        }
        // Draw minutes points
        for (i in 0 until 60) {
            val angle = PI / 30 * i
            val radiusX = mRadius + mPadding - 25
            val radiusY = mRadius + mPadding - 25
            val x = (mWidth / 2 + cos(angle) * radiusX).toFloat()
            val y = (mHeight / 2 + sin(angle) * radiusY).toFloat()
            val circleRadius = if (i % 5 == 0) hoursPointSize else minutesPointSize
            canvas.drawCircle(
                x, y,
                circleRadius,
                minutesPointPaint
            )
        }

        // Draw clock hands
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Draw hours
        drawHandLine(
            canvas = canvas,
            moment = (hour + minute / 60f) * 5f,
            isHour = true,
            isSecond = false
        )

        // Draw minutes
        drawHandLine(
            canvas = canvas, moment = minute.toFloat(),
            isHour = false,
            isSecond = false
        )

        // Draw seconds
        drawHandLine(
            canvas = canvas,
            moment = second.toFloat(),
            isHour = false,
            isSecond = true
        )

        // Draw clock center
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            centerPointSize,
            centerPointPaint
        )

        // Invalidate for the next representation of time
        postInvalidateDelayed(500)
    }

    private fun drawHandLine(canvas: Canvas, moment: Float, isHour: Boolean, isSecond: Boolean) {
        val angle = PI * moment / 30 - PI / 2
        val handRadius = when {
            isHour -> mHourHandTruncation
            isSecond -> mSecondHandTruncation
            else -> mMinuteHandTruncation
        }
        val handPaint = when {
            isHour -> hourHandPaint
            isSecond -> secondHandPaint
            else -> minuteHandPaint
        }
        canvas.drawLine(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mWidth / 2 + cos(angle) * handRadius).toFloat(),
            (mHeight / 2 + sin(angle) * handRadius).toFloat(),
            handPaint
        )
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomAnalogClock,
            0,
            0,
        ).apply {
            try {
                mTextSize =
                    getFloat(R.styleable.CustomAnalogClock_textSize, mTextSize)
                textColor =
                    getInteger(R.styleable.CustomAnalogClock_textColor, textColor)
                clockBackgroundColor =
                    getInteger(R.styleable.CustomAnalogClock_backgroundColor, clockBackgroundColor)
                clockBorderColor =
                    getInteger(R.styleable.CustomAnalogClock_clockBorderColor, clockBorderColor)
                clockBorderWidth =
                    getFloat(R.styleable.CustomAnalogClock_clockBorderWidth, clockBorderWidth)
                hourHandColor =
                    getInteger(R.styleable.CustomAnalogClock_hourHandColor, hourHandColor)
                minuteHandColor =
                    getInteger(R.styleable.CustomAnalogClock_minuteHandColor, minuteHandColor)
                secondHandColor =
                    getInteger(R.styleable.CustomAnalogClock_secondHandColor, secondHandColor)
                hourHandWidth =
                    getFloat(R.styleable.CustomAnalogClock_hourHandWidth, hourHandWidth)
                minuteHandWidth =
                    getFloat(R.styleable.CustomAnalogClock_minuteHandWidth, minuteHandWidth)
                secondHandWidth =
                    getFloat(R.styleable.CustomAnalogClock_secondHandWidth, secondHandWidth)
                centerPointColor =
                    getInteger(R.styleable.CustomAnalogClock_centerPointColor, centerPointColor)
                minutesPointColor =
                    getInteger(R.styleable.CustomAnalogClock_minutesPointColor, minutesPointColor)
                minutesPointSize =
                    getFloat(R.styleable.CustomAnalogClock_minutesPointSize, minutesPointSize)
                centerPointSize =
                    getFloat(R.styleable.CustomAnalogClock_centerPointSize, centerPointSize)
                hoursPointSize =
                    getFloat(R.styleable.CustomAnalogClock_hoursPointSize, hoursPointSize)
            } finally {
                recycle()
            }
        }
    }
}

private class SavedState : BaseSavedState {
    var backgroundColor: Int = 0
    var size: Int = 0
    var textSize: Int = 0


    constructor(superState: Parcelable?) : super(superState)


    constructor(source: Parcel?) : super(source) {
        source?.apply {
            backgroundColor = readInt()
            size = readInt()
            textSize = readInt()
        }
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(backgroundColor)
        out.writeInt(size)
        out.writeInt(textSize)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}
