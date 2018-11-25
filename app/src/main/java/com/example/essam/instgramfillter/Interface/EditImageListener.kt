package com.example.essam.instgramfillter.Interface

interface EditImageListener {
    fun  onBrightnessChanged(brightness:Int)
    fun  onSaturationChanged(saturation:Float)
    fun  onConstrantChanged(constrant:Float)
    fun editStart()
    fun editFinish()
}