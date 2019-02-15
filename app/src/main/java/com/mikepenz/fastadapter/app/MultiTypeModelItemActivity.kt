package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IInterceptor
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.app.model.IconModel
import com.mikepenz.fastadapter.app.model.ModelIconItem
import com.mikepenz.fastadapter.app.model.RightIconModel
import com.mikepenz.fastadapter.app.model.RightModelIconItem
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.iconics.Iconics
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import com.mikepenz.materialize.MaterializeBuilder
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.ArrayList
import java.util.Arrays
import kotlin.Comparator

class MultiTypeModelItemActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<IItem<out RecyclerView.ViewHolder>>

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_multi_model_item)

        //style our ui
        MaterializeBuilder().withActivity(this).build()

        //if you need multiple items for different models you can also do this be defining a Function which get's the model object and returns the item (extends IItem)
        val itemAdapter = ModelAdapter(object : IInterceptor<IconModel, ModelIconItem> {
            override fun intercept(element: IconModel): ModelIconItem? {
                return if (element is RightIconModel) {
                    RightModelIconItem(element)
                } else {
                    ModelIconItem(element)
                }
            }
        })

        //create our FastAdapter which will manage everything
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter))
        val selectExtension = fastAdapter.getOrCreateExtension<SelectExtension<IItem<*>>>(SelectExtension::class.java) as SelectExtension<*>
        selectExtension.isSelectable = true

        //init our gridLayoutManager and configure RV
        val gridLayoutManager = GridLayoutManager(this, 3)

        rv.layoutManager = gridLayoutManager
        rv.itemAnimator = SlideDownAlphaAnimator()
        rv.adapter = fastAdapter

        //order fonts by their name
        val mFonts = ArrayList(Iconics.registeredFonts)
        mFonts.sortWith(Comparator { object1, object2 -> object1.fontName.compareTo(object2.fontName) })

        //add all icons of all registered Fonts to the list
        val models = ArrayList<IconModel>()
        var i = 0
        for (font in mFonts) {
            for (icon in font.icons) {
                if (i % 3 == 0) {
                    models.add(IconModel(font.getIcon(icon)))
                } else {
                    models.add(RightIconModel(font.getIcon(icon)))
                }
                i++
            }
        }

        //fill with some sample data
        itemAdapter.add(models)

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle?) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
