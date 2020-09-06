package com.ku.kuvn.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityMainBinding
import com.ku.kuvn.ui.calculation.CalculationDetailFragment
import com.ku.kuvn.ui.calculation.CalculationFragment
import com.ku.kuvn.ui.gift.GiftFragment
import com.ku.kuvn.ui.home.HomeFragment
import com.ku.kuvn.ui.support.SupportFragment
import com.ku.kuvn.utils.hideSoftKeyboard
import com.ku.kuvn.utils.openMenu

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var homeFragment: HomeFragment? = null
    private var giftFragment: GiftFragment? = null
    private var calculationFragment: CalculationFragment? = null
    private var supportFragment: SupportFragment? = null
    private var calculationDetailFragment: CalculationDetailFragment? = null

    private var currentFrag: Fragment? = null
    private var progress: ProgressDialog? = null
    private val fragmentTags = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.icBack.setOnClickListener {
            hideCalDetail()
        }

        binding.tvSetting.setOnClickListener {
            viewModel.fetchMenuSetting()
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener listener@{ item ->
            hideSoftKeyboard()

            when (item.itemId) {
                R.id.main -> showHome()
                R.id.gift -> showGift()
                R.id.calculation -> showCalculation()
                R.id.support -> showSupport()
            }
            return@listener true
        }

        restoreSavedFragments(savedInstanceState)

        if (savedInstanceState == null) {
            showHome()
        }

        viewModel = ViewModelProvider(this).get()
        viewModel.init()
        viewModel.getMenuSettingLiveData().observe(this, Observer { menu ->
            if (menu != null) {
                openMenu(this, menu)
            }
        })
        viewModel.getMenuTitleLiveData().observe(this, Observer { title ->
            if (!title.isNullOrEmpty()) {
                binding.tvSetting.visibility = View.VISIBLE
                binding.tvSetting.text = title
            }
        })
        viewModel.getShowLoadingLiveData().observe(this, Observer { visible ->
            if (visible != null) {
                setLoadingWhenFetchingMenuVisible(visible)
            }
        })
    }

    private fun restoreSavedFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val fragments = supportFragmentManager.fragments
            if (!fragments.isNullOrEmpty()) {
                for (fragment in fragments) {
                    when (fragment) {
                        is HomeFragment -> homeFragment = fragment
                        is GiftFragment -> giftFragment = fragment
                        is CalculationFragment -> calculationFragment = fragment
                        is CalculationDetailFragment -> calculationDetailFragment = fragment
                        is SupportFragment -> supportFragment = fragment
                    }
                    fragmentTags.add(fragment::class.java.simpleName)
                }
                currentFrag = supportFragmentManager.findFragmentByTag(
                        savedInstanceState.getString("tag")) ?: homeFragment

                onFragmentShow(currentFrag!!)
            }
        }
    }

    private fun setLoadingWhenFetchingMenuVisible(visible: Boolean) {
        val progressDialog = progress ?: ProgressDialog(this, R.style.ProgressDialogTheme)
        progress = progressDialog

        if (visible) {
            progressDialog.setCancelable(false)
            progressDialog.show()
        } else {
            progressDialog.setCancelable(true)
            progressDialog.dismiss()
        }
    }

    private fun showHome() {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        showContent(homeFragment!!, HomeFragment::class.simpleName!!)
    }

    private fun showGift() {
        if (giftFragment == null) {
            giftFragment = GiftFragment()
        }
        showContent(giftFragment!!, GiftFragment::class.simpleName!!)
    }

    fun openGift() {
        binding.bottomNavigation.selectedItemId = R.id.gift
    }

    private fun showSupport() {
        if (supportFragment == null) {
            supportFragment = SupportFragment()
        }
        showContent(supportFragment!!, SupportFragment::class.simpleName!!)
    }

    fun openSupport() {
        binding.bottomNavigation.selectedItemId = R.id.support
    }

    private fun showCalculation() {
        val calDetailFrag = calculationDetailFragment
        if (calDetailFrag != null && calDetailFrag.isAdded) {
            showContent(calDetailFrag, CalculationDetailFragment::class.simpleName!!)
        } else {
            calculationDetailFragment = null

            if (calculationFragment == null) {
                calculationFragment = CalculationFragment()
            }
            showContent(calculationFragment!!, CalculationFragment::class.simpleName!!)
        }
    }

    fun openCalculation() {
        binding.bottomNavigation.selectedItemId = R.id.calculation
    }

    fun showCalDetail(calInput: CalculationFragment.Input) {
        val calDetailFrag = CalculationDetailFragment()
        calDetailFrag.calInput = calInput
        calculationDetailFragment = calDetailFrag
        showContent(calDetailFrag, CalculationDetailFragment::class.simpleName!!)
    }

    private fun hideCalDetail() {
        val transaction = supportFragmentManager.beginTransaction()
        val calDetailFrag = calculationDetailFragment
        if (calDetailFrag != null && calDetailFrag.isAdded) {
            fragmentTags.remove(CalculationDetailFragment::class.simpleName!!)
            transaction.remove(calDetailFrag)
        }

        if (calculationFragment == null) {
            calculationFragment = CalculationFragment()
        }
        showContent(calculationFragment!!, CalculationFragment::class.simpleName!!, transaction)
    }

    private fun showContent(fragment: Fragment,
                            tag: String,
                            transaction: FragmentTransaction = supportFragmentManager.beginTransaction()) {
        currentFrag?.let {
            if (it is HomeFragment) {
                transaction.hide(it).setMaxLifecycle(it, Lifecycle.State.STARTED)
            } else {
                transaction.hide(it)
            }
        }
        currentFrag = fragment

        if (supportFragmentManager.findFragmentByTag(tag) != null) {
            if (fragment is HomeFragment) {
                transaction.show(fragment).setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            } else {
                transaction.show(fragment)
            }
        } else if (!fragmentTags.contains(tag)) {
            fragmentTags.add(tag)
            transaction.add(R.id.content_container, fragment, tag)
        }
        transaction.commit()

        onFragmentShow(fragment)
    }

    private fun onFragmentShow(fragment: Fragment) {
        if (fragment is HomeFragment) {
            binding.mainTopBar.visibility = View.VISIBLE
            binding.defaultTopBar.root.visibility = View.GONE
        } else {
            val txtResId = when (fragment) {
                is GiftFragment -> R.string.bottom_gift
                is CalculationFragment, is CalculationDetailFragment -> R.string.bottom_calculation
                is SupportFragment -> R.string.bottom_support
                else -> R.string.empty_string
            }

            if (fragment is CalculationDetailFragment) {
                binding.defaultTopBar.icBack.visibility = View.VISIBLE
            } else {
                binding.defaultTopBar.icBack.visibility = View.GONE
            }
            binding.defaultTopBar.tvTitle.setText(txtResId)
            binding.mainTopBar.visibility = View.GONE
            binding.defaultTopBar.root.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (binding.bottomNavigation.selectedItemId == R.id.main) {
            super.onBackPressed()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.main
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("tag", currentFrag?.tag)
        super.onSaveInstanceState(outState)
    }
}
