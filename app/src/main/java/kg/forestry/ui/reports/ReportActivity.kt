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


//    private fun countEmptyValue( list:MutableList<String>, value: Int){
//        list.forEach{
//            when(it){
//                getString(R.string.not_observe) -> value = value
//                getString(R.string.ten) -> value += 10
//                getString(R.string.thirty) -> value += 30
//                getString(R.string.fifty) -> emptyCount += 50
//                getString(R.string.seventy) -> emptyCount += 70
//                getString(R.string.ninety) -> emptyCount += 90
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun countEachStateInPlant(it: Plant) {
        var emptyCount = 0
        var treeCount = 0
        var bushCount = 0
        var baseCount = 0
        var grassCount = 0
        var windCount = 0
        var stoneCount = 0
        var eatenCount = 0
        var nonEatenCount = 0



        val sideList = mutableListOf<Side>()
        val distancesList = mutableListOf<NewDistance>()
        val values = mutableListOf<String>()
        val heights = mutableListOf<String>()
        val averageHeight = mutableListOf<String>()

        //---
        val emptyValues = mutableListOf<String>()
        val bushValues = mutableListOf<String>()
        val treeValues = mutableListOf<String>()
        val baseValues = mutableListOf<String>()
        val grassValues = mutableListOf<String>()
        val eatenValues = mutableListOf<String>()
        val nonEatenValues = mutableListOf<String>()
        val stoneValues = mutableListOf<String>()
        val opadValues = mutableListOf<String>()





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
            if(!it.bush.isNullOrEmpty())  bushValues.add(it.bush!!)
            if(!it.stone.isNullOrEmpty())  stoneValues.add(it.stone!!)
            if(!it.eatenPlant.isNullOrEmpty()) eatenValues.add(it.eatenPlant!!)
            if(!it.nonEatenPlant.isNullOrEmpty())  nonEatenValues.add(it.nonEatenPlant!!)
            if(!it.base.isNullOrEmpty())  baseValues.add(it.base!!)
            if(!it.empty.isNullOrEmpty())  emptyValues.add(it.empty!!)
            if(!it.opad.isNullOrEmpty())  opadValues.add(it.opad!!)
            heights.add(it.plant_height!!)
        }

        emptyValues.forEach{
            when(it){
                getString(R.string.not_observe) -> emptyCount = 0
                getString(R.string.ten) -> emptyCount += 10
                getString(R.string.thirty) -> emptyCount += 30
                getString(R.string.fifty) -> emptyCount += 50
                getString(R.string.seventy) -> emptyCount += 70
                getString(R.string.ninety) -> emptyCount += 90
            }
        }
        bushValues.forEach{
            when(it){
                getString(R.string.not_observe) -> bushCount = 0
                getString(R.string.ten) -> bushCount += 10
                getString(R.string.thirty) -> bushCount += 30
                getString(R.string.fifty) -> bushCount += 50
                getString(R.string.seventy) -> bushCount += 70
                getString(R.string.ninety) -> bushCount += 90
            }
        }
        eatenValues.forEach{
            when(it){
                getString(R.string.not_observe) -> eatenCount = 0
                getString(R.string.ten) -> eatenCount += 10
                getString(R.string.thirty) -> eatenCount += 30
                getString(R.string.fifty) -> eatenCount += 50
                getString(R.string.seventy) -> eatenCount += 70
                getString(R.string.ninety) -> eatenCount += 90
            }
        }
        nonEatenValues.forEach{
            when(it){
                getString(R.string.not_observe) -> nonEatenCount = 0
                getString(R.string.ten) -> nonEatenCount += 10
                getString(R.string.thirty) -> nonEatenCount += 30
                getString(R.string.fifty) -> nonEatenCount += 50
                getString(R.string.seventy) -> nonEatenCount += 70
                getString(R.string.ninety) -> nonEatenCount += 90
            }
        }
        stoneValues.forEach{
            when(it){
                getString(R.string.not_observe) -> stoneCount = 0
                getString(R.string.ten) -> stoneCount += 10
                getString(R.string.thirty) -> stoneCount += 30
                getString(R.string.fifty) -> stoneCount += 50
                getString(R.string.seventy) -> stoneCount += 70
                getString(R.string.ninety) -> stoneCount += 90
            }
        }
        opadValues.forEach{
            when(it){
                getString(R.string.not_observe) -> windCount = 0
                getString(R.string.ten) -> windCount += 10
                getString(R.string.thirty) -> windCount += 30
                getString(R.string.fifty) -> windCount += 50
                getString(R.string.seventy) -> windCount += 70
                getString(R.string.ninety) -> windCount += 90
            }
        }
        baseValues.forEach{
            when(it){
                getString(R.string.not_observe) -> baseCount = 0
                getString(R.string.ten) -> baseCount += 10
                getString(R.string.thirty) -> baseCount += 30
                getString(R.string.fifty) -> baseCount += 50
                getString(R.string.seventy) -> baseCount += 70
                getString(R.string.ninety) -> baseCount += 90
            }
        }

        stoneCount /= stoneValues.size


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
        stoneCount /= stoneValues.size
        emptyCount /= emptyValues.size
        nonEatenCount /= nonEatenValues.size
        eatenCount /= eatenValues.size
        baseCount /= baseValues.size
        bushCount /= bushValues.size
        windCount /= opadValues.size

        valuesPie.add(PieEntry(nonEatenCount.toFloat()))
        valuesPie.add(PieEntry(emptyCount.toFloat()))
        valuesPie.add(PieEntry(bushCount.toFloat()))
        valuesPie.add(PieEntry(eatenCount.toFloat()))
        valuesPie.add(PieEntry(baseCount.toFloat()))
        valuesPie.add(PieEntry(stoneCount.toFloat()))
        valuesPie.add(PieEntry(windCount.toFloat()))
        setupPieChart(valuesPie, summ)

        cvv_kamen.setNewTitle(getResourceString(R.string.stone).plus(" - $stoneCount"))
        cvv_golaya.setNewTitle(getResourceString(R.string.bare_place).plus(" - $emptyCount"))
        cvv_opad.setNewTitle(getResourceString(R.string.decline).plus(" - $windCount"))
        cvv_osnova_rasteni.setNewTitle(getResourceString(R.string.plant_base).plus(" - $baseCount"))
        cvv_kustarnik.setNewTitle(getResourceString(R.string.bush).plus(" - $bushCount"))
        cvv_poedaemoe.setNewTitle(getResourceString(R.string.eaten_plant).plus(" - $eatenCount"))
        cvv_ne_poedaemoe.setNewTitle(getResourceString(R.string.non_eaten_plant).plus(" - $nonEatenCount"))

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
