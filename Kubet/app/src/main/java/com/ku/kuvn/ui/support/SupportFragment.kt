package com.ku.kuvn.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.ku.kuvn.databinding.FragmentSupportBinding
import com.ku.kuvn.ui.MainViewModel

class SupportFragment : Fragment() {

    private lateinit var binding: FragmentSupportBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSupportBinding.inflate(layoutInflater)
        binding.btnOpenZalo.setOnClickListener {
            val ownerActivity = activity
            val supportUrl = mainViewModel.getSupportUrlLiveData().value
            if (ownerActivity != null && !supportUrl.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(supportUrl))
                if (intent.resolveActivity(ownerActivity.packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
        mainViewModel = ViewModelProvider(activity!!).get()

        return binding.root
    }
}