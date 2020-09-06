package com.ku.kuvn.ui.calculation

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.ku.kuvn.databinding.FragmentCalculationDetailBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CalculationDetailFragment : Fragment() {

    private lateinit var binding: FragmentCalculationDetailBinding

    private val mainHandler = Handler(Looper.getMainLooper())
    private val startAnimationRunnable = Runnable {
        binding.graphInvestmentInterest.visibility = View.VISIBLE
        binding.graphInvestmentInterest.animateY(600, Easing.EaseInOutQuad)
    }

    var calInput: CalculationFragment.Input? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCalculationDetailBinding.inflate(layoutInflater)

        binding.graphInvestmentInterest.apply {
            setUsePercentValues(true)

            description.isEnabled = false
            setExtraOffsets(0f, 10f, 0f, 10f)

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setDrawEntryLabels(false)

            setTransparentCircleAlpha(0)
            holeRadius = 57f

            isRotationEnabled = false
            rotationAngle = 0f

            setDrawCenterText(true)
            isHighlightPerTapEnabled = true

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)

                textSize = 13f

                form = Legend.LegendForm.CIRCLE
                formSize = 12f
                formToTextSpace = 12f

                yEntrySpace = 5f
                yOffset = 5f
            }
        }

        setData(savedInstanceState)

        return binding.root
    }

    private fun setData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            calInput = savedInstanceState.getParcelable("input")
        }
        val calInput = calInput ?: return

        val entries: ArrayList<PieEntry> = ArrayList<PieEntry>().apply {
            add(PieEntry(calInput.initialCapital.toFloat(), "Vốn ban đầu"))
            add(PieEntry(calInput.totalCapital.toFloat(), "Tổng vốn góp"))
            add(PieEntry(calInput.totalInterest.toFloat(), "Tổng lãi"))
        }

        val dataSet = PieDataSet(entries, "").apply {
            selectionShift = 5f
            sliceSpace = 3f
            colors = listOf(Color.parseColor("#F7B200"), Color.parseColor("#03B0FF"), Color.parseColor("#FF6475"))
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.graphInvestmentInterest))
            setValueTextSize(14f)
            setValueTypeface(Typeface.DEFAULT_BOLD)
            setValueTextColor(Color.BLACK)
        }

        binding.graphInvestmentInterest.apply {
            setData(data)
            visibility = View.INVISIBLE
            mainHandler.postDelayed(startAnimationRunnable, 300)
        }

        val dfs = DecimalFormatSymbols()
        dfs.groupingSeparator = '.'
        val df = DecimalFormat("##,###", dfs)
        df.maximumFractionDigits = 0

        binding.tvTotal.text = df.format(calInput.total)
        binding.tvInitialCapital.text = df.format(calInput.initialCapital)
        binding.tvTotalCapital.text = df.format(calInput.totalCapital)
        binding.tvTotalInterest.text = df.format(calInput.totalInterest)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        calInput?.let { outState.putParcelable("input", it) }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(startAnimationRunnable)
    }
}