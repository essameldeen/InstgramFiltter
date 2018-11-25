package com.example.essam.instgramfillter.Interface

import com.zomato.photofilters.imageprocessors.Filter

interface FilterImageListener {
    fun  onFilterImageSelected(fillter:Filter)
}