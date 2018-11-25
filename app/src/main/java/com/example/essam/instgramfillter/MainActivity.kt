package com.example.essam.instgramfillter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.essam.instgramfillter.Adapter.ViewPagerAdapter
import com.example.essam.instgramfillter.Controllers.EditImageFragment
import com.example.essam.instgramfillter.Controllers.FilterListFragment
import com.example.essam.instgramfillter.Interface.EditImageListener
import com.example.essam.instgramfillter.Interface.FilterImageListener
import com.example.essam.instgramfillter.Utilits.BitmapUtils
import com.example.essam.instgramfillter.Utilits.NonSwipeableViewPager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), FilterImageListener, EditImageListener {

    val SELECTED_GALLERY_PERMISSON = 999

    internal var originalImage: Bitmap? = null
    internal lateinit var filteredImage: Bitmap
    internal lateinit var finalImage: Bitmap

    internal lateinit var filterImageFragment: FilterListFragment
    internal lateinit var editImageFragment: EditImageFragment

    internal var finalBrightness = 0
    internal var finalSaturation = 1.0f
    internal var finalContrast = 1.0f


    init {
        System.loadLibrary("NativeImageProcessor")
    }
    object Main {
        val IMAGE_NAME = "flash.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Instagram Filter"

        loadImage()
        setUpViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

    }

    private fun setUpViewPager(viewPager: NonSwipeableViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add filter Image Fragment
        filterImageFragment = FilterListFragment()
        filterImageFragment.setListener(this)

        // add edit image Fragment
        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)


        //
        adapter.addFragment(filterImageFragment, "FILTER")
        adapter.addFragment(editImageFragment, "EDIT")
        viewPager!!.adapter = adapter

    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Main.IMAGE_NAME, 300, 300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(originalImage)

    }

    override fun onFilterImageSelected(fillter: Filter) {
        resetControls()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(fillter.processFilter(filteredImage))
        finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls()
        }
        finalBrightness = 0
        finalSaturation = 1.0f
        finalContrast = 1.0f
    }

    override fun onBrightnessChanged(brightness: Int) {
        finalBrightness = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(finalBrightness))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage!!.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onSaturationChanged(saturation: Float) {
        finalSaturation = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(finalSaturation))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage!!.copy(Bitmap.Config.ARGB_8888, true)))

    }

    override fun onConstrantChanged(constrant: Float) {
        finalContrast = constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(finalContrast))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage!!.copy(Bitmap.Config.ARGB_8888, true)))

    }

    override fun editStart() {
       
    }

    override fun editFinish() {
        val bitmap = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(finalBrightness))
        myFilter.addSubFilter(SaturationSubfilter(finalSaturation))
        myFilter.addSubFilter(ContrastSubFilter(finalContrast))
        finalImage = myFilter.processFilter(bitmap)
        image_preview.setImageBitmap(finalImage)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.save -> {
                saveImageInGallery()
            }
            R.id.open -> {
                openImageFromGallery()
            }

        }
        return true
    }

    private fun openImageFromGallery() {
        // user Dexter to request run time permission and process
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, SELECTED_GALLERY_PERMISSON)
                    } else
                        Toast.makeText(this@MainActivity, "Permission Denied ,", Toast.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()

    }

    private fun saveImageInGallery() {
        // user Dexter to request run time permission and process
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val path = BitmapUtils.insertImage(
                            contentResolver,
                            finalImage,
                            System.currentTimeMillis().toString() + "_image.jpg",
                            "My Image Filter"
                        )

                        if (!TextUtils.isEmpty(path)) {
                            val snackBar = Snackbar.make(coordinator, "Image Save In Gallery", Snackbar.LENGTH_LONG)
                                .setAction("OPEN", {
                                    openImage(path)
                                })
                            snackBar.show()
                        } else {
                            val snackBar = Snackbar.make(coordinator, "Unable To Save ", Snackbar.LENGTH_LONG)
                            snackBar.show()
                        }
                    } else
                        Toast.makeText(this@MainActivity, "Permission Denied ,", Toast.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun openImage(path: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path), "image/*")
        startActivity(intent)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECTED_GALLERY_PERMISSON) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, data!!.data, 800, 800)
            clearImage()

            originalImage = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
            bitmap.recycle()
            // render filter image
            filterImageFragment.displayImage(originalImage)
            image_preview.setImageBitmap(originalImage)
        }
    }

    private fun clearImage() {
        originalImage!!.recycle()
        filteredImage!!.recycle()
        finalImage!!.recycle()
    }

}
