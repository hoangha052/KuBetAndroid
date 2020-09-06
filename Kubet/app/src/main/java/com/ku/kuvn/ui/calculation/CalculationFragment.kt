package com.ku.kuvn.ui.calculation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.ku.kuvn.R
import com.ku.kuvn.databinding.FragmentCalculationBinding
import com.ku.kuvn.ui.MainActivity
import com.ku.kuvn.utils.hideSoftKeyboard
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CalculationFragment : Fragment() {

    private lateinit var binding: FragmentCalculationBinding

    private var initialCapital: Long = 0
    private var interestRatePercent: Float = 0.0f
    private var capital: Long = 0
    private var capitalByTimeOption = CapitalByTimeOption.YEAR
    private var duration: Int = 0

    private val numberFormat: DecimalFormat by lazy {
        val dfs = DecimalFormatSymbols()
        dfs.groupingSeparator = '.'
        DecimalFormat("##,###", dfs)
    }

    private val interestRateFormatter: DecimalFormat by lazy {
        val dfs = DecimalFormatSymbols()
        dfs.groupingSeparator = '.'
        dfs.decimalSeparator = '.'
        val df = DecimalFormat("##.#", dfs)
        df.minimumFractionDigits = 1
        df
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCalculationBinding.inflate(layoutInflater)
        binding.etInitialCapital.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val displayNumber = s?.toString()
                if (!displayNumber.isNullOrEmpty()) {
                    binding.etInitialCapital.removeTextChangedListener(this)

                    val newStr = displayNumber.replace(".", "")
                    initialCapital = newStr.toLong()
                    val text = numberFormat.format(initialCapital)
                    binding.etInitialCapital.setText(text)
                    binding.etInitialCapital.setSelection(binding.etInitialCapital.text.length)
                    binding.etInitialCapital.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.etCapital.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val displayNumber = s?.toString()
                if (!displayNumber.isNullOrEmpty()) {
                    binding.etCapital.removeTextChangedListener(this)

                    val newStr = displayNumber.replace(".", "")
                    capital = newStr.toLong()
                    val text = numberFormat.format(capital)
                    binding.etCapital.setText(text)
                    binding.etCapital.setSelection(binding.etCapital.text.length)
                    binding.etCapital.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etInterestRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val displayNumber = s?.toString()
                if (!displayNumber.isNullOrEmpty()) {
                    var value = displayNumber.toFloat()
                    if (value >= 100) {
                        value /= 10
                        binding.etInterestRate.removeTextChangedListener(this)
                        val text = interestRateFormatter.format(value.toDouble())
                        binding.etInterestRate.setText(text)
                        binding.etInterestRate.setSelection(binding.etInterestRate.text.length)
                        binding.etInterestRate.addTextChangedListener(this)
                    }
                    interestRatePercent = value
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etDuration.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val displayNumber = s?.toString()
                if (!displayNumber.isNullOrEmpty()) {
                    duration = displayNumber.toInt()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etDuration.setOnEditorActionListener editor@ { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculate()
            }
            return@editor false
        }

        binding.segmentTimeOptions.setOnCheckedChangeListener { _, checkedId ->
            capitalByTimeOption = when (checkedId) {
                R.id.rd_month -> CapitalByTimeOption.MONTH
                else -> CapitalByTimeOption.YEAR
            }
        }
        binding.rdYear.isChecked = true

        binding.btnCal.setOnClickListener {
            calculate()
            activity?.hideSoftKeyboard()
        }

        binding.root.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activity?.hideSoftKeyboard()
            }
        }
        return binding.root
    }

    private fun calculate() {
        val ownerActivity = activity
        if (ownerActivity is MainActivity) {
            ownerActivity.showCalDetail(
                Input(
                    initialCapital, interestRatePercent, capital, capitalByTimeOption, duration
                )
            )
        }
    }

    enum class CapitalByTimeOption(val value: Int) {
        MONTH(12), YEAR(1)
    }

    data class Input(val initialCapital: Long, val interestRatePercent: Float, val capital: Long,
                     val capitalByTimeOption: CapitalByTimeOption, val duration: Int): Parcelable {

        val totalCapital: Long by lazy {
            capital * capitalByTimeOption.value * duration
        }

        val total: Double by lazy {
            var total = initialCapital.toDouble()
            val yearPeriod = capitalByTimeOption.value * duration
            val interestRate = 1 + (interestRatePercent / (100 * capitalByTimeOption.value))

            for (i in 1..yearPeriod) {
                total = total * interestRate + capital
            }
            total
        }

        val totalInterest: Double by lazy {
            total - initialCapital - totalCapital
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readFloat(), parcel.readLong(),
                CapitalByTimeOption.valueOf(parcel.readString()!!), parcel.readInt())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(initialCapital)
            parcel.writeFloat(interestRatePercent)
            parcel.writeLong(capital)
            parcel.writeString(capitalByTimeOption.name)
            parcel.writeInt(duration)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<Input> {
            override fun createFromParcel(parcel: Parcel) = Input(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Input>(size)
        }
    }
}