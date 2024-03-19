package ru.shumikhin.clockview

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.slider.Slider
import ru.shumikhin.clockview.customview.CustomAnalogClock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val clock = findViewById<CustomAnalogClock>(R.id.customAnalogClock)
        val colorSlider = findViewById<Slider>(R.id.slider_background_color)
        colorSlider.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(p0: Slider) {
                }
                override fun onStopTrackingTouch(slider: Slider) {
                    val c = slider.value.toInt() * 100
                    val red = (c shr 16) and 0xFF
                    val green = (c shr 8) and 0xFF
                    val blue = c and 0xFF
                    clock.setClockBackgroundColor(Color.rgb(red, green, blue))
                }
            }
        )

        val sizeSlider = findViewById<Slider>(R.id.slider_size)
        sizeSlider.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }
                override fun onStopTrackingTouch(p0: Slider) {
                    clock.setSize(p0.value.toInt() * 10)
                }
            }
        )

        val textSizeSlider = findViewById<Slider>(R.id.slider_text_size)
        textSizeSlider.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                }
                override fun onStopTrackingTouch(p0: Slider) {
                    clock.setTextSize(p0.value.toInt())
                }
            }
        )
    }
}