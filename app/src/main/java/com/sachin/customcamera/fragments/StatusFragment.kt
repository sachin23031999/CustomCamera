package com.sachin.customcamera.fragments

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sachin.customcamera.R
import com.sachin.customcamera.repo.Repository
import com.sachin.customcamera.databinding.FragmentStatusBinding
import com.sachin.customcamera.interfaces.OnCaptureListener
import com.sachin.customcamera.utils.Constants.COUNTDOWN_TIME
import com.sachin.customcamera.viewmodel.MainViewModel
import com.sachin.customcamera.viewmodel.MainViewModelFactory
import com.sachin.customcamera.utils.Constants.EXPOSURE_L
import com.sachin.customcamera.utils.Constants.EXPOSURE_U
import com.sachin.customcamera.utils.Constants.PERIOD
import com.sachin.customcamera.utils.Logger.Companion.d
import com.sachin.customcamera.utils.Logger.Companion.e

class StatusFragment : Fragment() {
    private val TAG = StatusFragment::class.java.simpleName
    private lateinit var mBinding: FragmentStatusBinding
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
        mBinding = FragmentStatusBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.deleteImageFolder()
        updateStatusField(getString(R.string.capture_single_image))
        mViewModel.captureSingle(requireContext(), 1F, 100)
       // captureMultipleImages()
        val countDownTimer = object : CountDownTimer(COUNTDOWN_TIME, PERIOD) {
            override fun onTick(time: Long) {
                d(TAG, "onTick: $time")
                mBinding.tvTimer.text = "${time/1000} sec"
            }

            override fun onFinish() {
                mBinding.tvTimer.text = "0 sec"
                updateStatusField(getString(R.string.capture_multiple_image))
                captureMultipleImages()
            }

        }
        countDownTimer.start()
    }

    private fun captureMultipleImages() {
        var meanExpUri: Uri? = null
        val meanExposure = 0L
        mViewModel.captureMultiple(requireContext(), 1F, 100, EXPOSURE_L, EXPOSURE_U, object :
            OnCaptureListener {
            override fun onStart(exposure: Long) {
                d(TAG, "onStart(), exposure: $exposure")
                    updateStatusField(getString(R.string.capturing_img_for_exposure) + " " + exposure)
            }

            override fun onSaved(exposure: Long, uri: Uri) {
                d(TAG, "onSaved()")
                if(exposure == meanExposure)
                    meanExpUri = uri
            }

            override fun onComplete() {
                d(TAG, "onComplete()")
                    updateStatusField(getString(R.string.sending_image))
                    sendImage(meanExpUri!!)
            }

            override fun onError(error: String?) {
                e(TAG, "some error occurred, error: $error")
                Handler(Looper.getMainLooper()).post {
                    updateStatusField(getString(R.string.some_error_occurred))
                }
            }

        })
    }

    private fun sendImage(uri: Uri) {
        mViewModel.sendImage(uri) {
            if(it) {
                updateStatusField(getString(R.string.test_done))

            }
            else {
                updateStatusField(getString(R.string.test_failed))
            }
            mViewModel.deleteImageFolder()
        }

    }

    private fun updateStatusField(status: String)
        = Handler(Looper.getMainLooper()).post {
            mBinding.tvTestStatus.text = status
        }
}