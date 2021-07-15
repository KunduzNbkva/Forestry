package kg.forestry.ui.reports

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import kg.forestry.R
import kg.forestry.ui.core.base.BaseActivity
import kg.core.utils.*
import kg.forestry.localstorage.model.Plant
import kotlinx.android.synthetic.main.activity_report.*
import org.parceler.Parcels

class ReportActivity :
    BaseActivity<ReportViewModel>(R.layout.activity_report, ReportViewModel::class) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.apply {
            title = getString(R.string.report)
            setNavigationOnClickListener { onBackPressed() }
        }
        year2020.setChecked(true)

        year2019.setOnClickListener {
            clearButtonsStates(); year2019.setChecked(true)
            ll_report.gone()
        }
        year2020.setOnClickListener {
            clearButtonsStates(); year2020.setChecked(true)
            ll_report.visible()
        }
        year2021.setOnClickListener {
            clearButtonsStates(); year2021.setChecked(true)
            ll_report.gone()
        }
        year2022.setOnClickListener {
            clearButtonsStates(); year2022.setChecked(true)
            ll_report.gone()
        }
        parseDataFromIntent()

        btn_share.setOnClickListener {
            val bm = ll_report.getBitmap()
            saveImage(bm)

        }

    }

    private fun parseDataFromIntent() {
        val plant = Plant::class.java.canonicalName
        Parcels.unwrap<Plant>(intent.getParcelableExtra(plant))?.let {
            setupViews(it)
            countEachStateInPlant(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun countEachStateInPlant(it: Plant) {
        var emptyCount = 0
        var treeCount = 0
        var bushCount = 0
        var baseCount = 0
        var grassCount = 0
        var windCount = 0
        var stoneCount = 0

        val sideList = mutableListOf<Side>()
        val distancesList = mutableListOf<Distance>()
        val values = mutableListOf<String>()
        val heights = mutableListOf<String>()
        var averageHeight = mutableListOf<String>()

        sideList.add(it.southSide)
        sideList.add(it.westSide)
        sideList.add(it.eastSide)
        sideList.add(it.northSide)

        sideList.forEach {
            distancesList.add(it.m5)
            distancesList.add(it.m10)
            distancesList.add(it.m15)
            distancesList.add(it.m20)
            distancesList.add(it.m25)
        }
        distancesList.forEach {
            values.add(it.d50)
            values.add(it.d30)
            values.add(it.d70)
            values.add(it.d90)
            values.add(it.d10)
            heights.add(it.plant_height)
        }
        values.forEach {
            when (it) {
                "EMPTY" -> emptyCount++
                "TREE" -> treeCount++
                "BUSH" -> bushCount++
                "BASE" -> baseCount++
                "GRASS" -> grassCount++
                "WIND" -> windCount++
                "STONE" -> stoneCount++
            }
        }

        heights.forEach {
            averageHeight.add(it.replace(("[^\\d.]").toRegex(), ""))
        }
        val sum = averageHeight.filter { it.isNotEmpty() }
        var value = 0
        sum.forEach { value += it.toInt() }
        val valuesPie = mutableListOf<PieEntry>()

        var summ = 0
        if(sum.count() > 0){
            summ = value / sum.size
        }

        valuesPie.add(PieEntry(treeCount.toFloat()))
        valuesPie.add(PieEntry(emptyCount.toFloat()))
        valuesPie.add(PieEntry(bushCount.toFloat()))
        valuesPie.add(PieEntry(grassCount.toFloat()))
        valuesPie.add(PieEntry(baseCount.toFloat()))
        valuesPie.add(PieEntry(stoneCount.toFloat()))
        valuesPie.add(PieEntry(windCount.toFloat()))
        setupPieChart(valuesPie, summ)

        cvv_kamen.setNewTitle(getResourceString(R.string.stone).plus(" - $stoneCount"))
        cvv_golaya.setNewTitle(getResourceString(R.string.bare_place).plus(" - $emptyCount"))
        cvv_opad.setNewTitle(getResourceString(R.string.decline).plus(" - $windCount"))
        cvv_osnova_rasteni.setNewTitle(getResourceString(R.string.plant_base).plus(" - $baseCount"))
        cvv_kustarnik.setNewTitle(getResourceString(R.string.bush).plus(" - $treeCount"))
        cvv_poedaemoe.setNewTitle(getResourceString(R.string.eaten_plant).plus(" - $bushCount"))
        cvv_ne_poedaemoe.setNewTitle(getResourceString(R.string.non_eaten_plant).plus(" - $grassCount"))

    }

    private fun clearButtonsStates() {
        year2019.setChecked(false)
        year2020.setChecked(false)
        year2021.setChecked(false)
        year2022.setChecked(false)
    }

    private fun setupViews(plant: Plant?) {
        plant?.let {
            name_pasture.setTitle(it.pastureName)
            plot_name.setTitle(it.plotName)
            location.setTitle(it.plantLocation.getLocationAsString())
            date.setTitle(it.date)
        }
    }


    private fun setupPieChart(
        values: MutableList<PieEntry>,
        sum: Int
    ) {
        height_bush.setHeight("$sum")

        val dataSet = PieDataSet(values, null)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueLinePart1OffsetPercentage = 30f
        val legend = piechart.legend
        legend.isEnabled = false
        piechart.setExtraOffsets(0f, 5f, 0f, 5f)
        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)
        piechart.data = data
        dataSet.setColors(
            resources.getColor(R.color.orange),
            resources.getColor(R.color.dark_blue),
            resources.getColor(R.color.blue),
            resources.getColor(R.color.light_green),
            resources.getColor(R.color.gray),
            resources.getColor(R.color.dark_gray),
            resources.getColor(R.color.green)
        )
        piechart.animateXY(1000, 1000)
    }

    fun View.getBitmap(): Bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888).also {
        //create a canvas for this bitmap (it)
        Canvas(it).apply {
            //if the view has a background, draw it to the canvas, else draw a white screen
            //because canvas default background is black
            background?.draw(this) ?: drawColor(Color.WHITE)
            //draw the view to the canvas
            draw(this)
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        //Generating a file name
//            val filename = "${System.currentTimeMillis()}.jpg"
//
//            //Output stream
//            var fos: OutputStream? = null
//
//            //For devices running android >= Q
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                //getting the contentResolver
//                context?.contentResolver?.also { resolver ->
//
//                    //Content resolver will process the contentvalues
//                    val contentValues = ContentValues().apply {
//
//                        //putting file information in content values
//                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//                    }
//
//                    //Inserting the contentValues to contentResolver and getting the Uri
//                    val imageUri: Uri? =
//                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//                    //Opening an outputstream with the Uri that we got
//                    fos = imageUri?.let { resolver.openOutputStream(it) }
//                }
//            } else {
//                //These for devices running on android < Q
//                //So I don't think an explanation is needed here
//                val imagesDir =
//                    Environment.getRootDirectory()
//                val image = File(imagesDir, filename)
//                fos = FileOutputStream(image)
//            }
//
//        fos?.use {
//            //Finally writing the bitmap to the output stream that we opened
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//            Toast.makeText(this, "Сохранено в галерее!", Toast.LENGTH_SHORT ).show()
//        }


    }

    companion object {
        fun start(context: Context, plant: Plant? = null) {
            val intent = Intent(context, ReportActivity::class.java)
            plant?.let {
                intent.putExtra(
                    Plant::class.java.canonicalName,
                    Parcels.wrap(plant)
                )
            }
            context.startActivity(intent)
        }
    }
}
