package com.example.essam.instgramfillter.Controllers


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.essam.instgramfillter.Interface.EditImageListener

import com.example.essam.instgramfillter.R
import kotlinx.android.synthetic.main.fragment_edit_image.*

class EditImageFragment : Fragment(), SeekBar.OnSeekBarChangeListener {


    private var listener: EditImageListener? = null

     fun setListener(listener: EditImageListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSeekBar()


    }

    private fun setupSeekBar() {
        seekBar_brightness.max = 200
        seekBar_brightness.progress = 100
        seekBar_brightness.setOnSeekBarChangeListener(this)

        seekBar_constraint.max = 20
        seekBar_constraint.progress = 0
        seekBar_constraint.setOnSeekBarChangeListener(this)

        seekBar_saturation.max = 30
        seekBar_saturation.progress = 10
        seekBar_saturation.setOnSeekBarChangeListener(this)

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        var progress = progress
        if (listener != null) {
            when {
                seekBar!!.id == R.id.seekBar_brightness -> listener!!.onBrightnessChanged(progress - 100)
                seekBar!!.id == R.id.seekBar_constraint -> {
                    progress += 10
                    val floatVal = .10f * progress
                    listener!!.onConstrantChanged(floatVal)
                }
                seekBar!!.id == R.id.seekBar_saturation -> {
                    val floatVal = .10f * progress
                    listener!!.onSaturationChanged(floatVal)
                }
            }
        }

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (listener != null) {
            listener!!.editStart()
        }

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (listener != null) {
            listener!!.editFinish()
        }
    }

    fun resetControls() {
        seekBar_brightness.progress = 100
        seekBar_constraint.progress = 0
        seekBar_saturation.progress = 10
    }


}
