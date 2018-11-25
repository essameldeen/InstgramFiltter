package com.example.essam.instgramfillter.Controllers


import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.essam.instgramfillter.Adapter.ThumbnailAdapter
import com.example.essam.instgramfillter.Interface.FilterImageListener
import com.example.essam.instgramfillter.MainActivity

import com.example.essam.instgramfillter.R
import com.example.essam.instgramfillter.Utilits.BitmapUtils
import com.example.essam.instgramfillter.Utilits.SpaceItemDecoration
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_filter_list.*


class FilterListFragment : Fragment(), FilterImageListener {
    internal var listener: FilterImageListener? = null
    internal lateinit var adapter: ThumbnailAdapter
    internal lateinit var listThumbnailItem: MutableList<ThumbnailItem>

    fun setListener(listener: FilterImageListener) {
        this.listener = listener
    }

    override fun onFilterImageSelected(fillter: Filter) {
        if (listener != null) {
            listener!!.onFilterImageSelected(fillter)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listThumbnailItem = ArrayList()

        setUpAdapter()
        initRecycleView()
        displayImage(null)

    }

     fun displayImage(bitmap: Bitmap?) {
        val r = Runnable {
            var thumbnailImage: Bitmap?
            if (bitmap == null) {
                thumbnailImage = BitmapUtils.getBitmapFromAssets(activity!!, MainActivity.Main.IMAGE_NAME, 100, 100)

            } else {
                thumbnailImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            }

            if (thumbnailImage == null)
                return@Runnable

            ThumbnailsManager.clearThumbs()
            listThumbnailItem.clear()

            // add Normal Bitmap
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbnailImage
            thumbnailItem.filterName = "Normal"
            ThumbnailsManager.addThumb(thumbnailItem)

            // add filter package
            val filters = FilterPack.getFilterPack(activity!!)
            for (filter in filters) {
                val item = ThumbnailItem()
                item.image = thumbnailImage
                item.filter = filter
                item.filterName = filter.name
                ThumbnailsManager.addThumb(item)
            }
            listThumbnailItem.addAll(ThumbnailsManager.processThumbs(activity))

            activity!!.runOnUiThread{
                adapter.notifyDataSetChanged()
            }
        }
        Thread(r).start()

    }

    private fun initRecycleView() {
        recycle_filter_list.setHasFixedSize(true)
        recycle_filter_list.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        recycle_filter_list.adapter = adapter
        recycle_filter_list.itemAnimator = DefaultItemAnimator()

        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()

        recycle_filter_list.addItemDecoration(SpaceItemDecoration(space))
        recycle_filter_list.adapter = adapter
    }

    private fun setUpAdapter() {
        adapter = ThumbnailAdapter(activity!!, listThumbnailItem, this)
    }


}
