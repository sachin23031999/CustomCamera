package com.sachin.customcamera.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sachin.customcamera.viewmodel.MainViewModel
import com.sachin.customcamera.R
import com.sachin.customcamera.databinding.FragmentOnboardBinding
import com.sachin.customcamera.repo.Repository
import com.sachin.customcamera.utils.Utils
import com.sachin.customcamera.viewmodel.MainViewModelFactory
import com.sachin.customcamera.utils.Logger.Companion.d

class OnboardFragment : Fragment() {
    private val TAG = OnboardFragment::class.java.simpleName
    private lateinit var mBinding: FragmentOnboardBinding
    private lateinit var mRepository: Repository
    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepository = Repository(requireContext())
        mViewModel = ViewModelProvider(requireActivity(), MainViewModelFactory(mRepository))[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentOnboardBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFields()
        mBinding.btTakeTest.setOnClickListener {
            d(TAG, "take test click")
            if(mBinding.etName.text.isEmpty() && mBinding.etEmail.text.isEmpty()) {
                Utils.showToast(requireContext(), getString(R.string.enter_required_info))
                return@setOnClickListener
            }
            if(mBinding.etName.text.isEmpty()) {
                Utils.showToast(requireContext(), getString(R.string.name_required))
                return@setOnClickListener
            }
            if(mBinding.etEmail.text.isEmpty()) {
                Utils.showToast(requireContext(), getString(R.string.email_required))
                return@setOnClickListener
            }

            mViewModel.saveInfo(mBinding.etName.text.toString(), mBinding.etEmail.text.toString())
            Utils.navigateTo(
                requireActivity(),
                R.id.fragment_container,
                StatusFragment(),
                ""
            )
        }
    }

    override fun onResume() {
        super.onResume()
        setFields()
    }
    private fun setFields() {
        val (name, email) = mViewModel.getInfo()
        if(!name.isNullOrEmpty()) mBinding.etName.setText(name)
        if(!email.isNullOrEmpty()) mBinding.etEmail.setText(email)
    }
}