package com.example.essam.instgramfillter.Adapter


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.DialogTitle

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private var fragmentList = ArrayList<Fragment>()
    private var fragmentListTitle = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]

    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentListTitle[position]
    }
}