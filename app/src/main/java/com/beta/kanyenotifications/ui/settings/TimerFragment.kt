package com.beta.kanyenotifications.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.beta.kanyenotifications.R
import com.beta.kanyenotifications.databinding.FragmentTimerBinding
import com.google.android.material.snackbar.Snackbar


class TimerFragment : androidx.fragment.app.Fragment() {


    private lateinit var timerViewModel: TimerViewModel
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        timerViewModel =
                ViewModelProvider(this).get(TimerViewModel::class.java)
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val timerViewModelFactory = TimerViewModelFactory(application)

        val timerViewModel =
            ViewModelProvider(
                this, timerViewModelFactory
            ).get(TimerViewModel::class.java)


        _binding?.lifecycleOwner = this
        _binding?.timerViewModel = timerViewModel

        _binding?.timePicker?.setIs24HourView(true)
        binding?.let { hideKeyboardInputInTimePicker(this.resources.configuration.orientation, it.timePicker) }

        _binding?.textview?.text = timerViewModel.getTime()
        //todo add popup which came from sky when time is set
        _binding?.btnDB?.setOnClickListener{

            val settedTimeHour = _binding?.timePicker?.hour.toString()
            val hourAffterCheck = timerViewModel.checkIfSingle(settedTimeHour)

            val settedTimeMinute = _binding?.timePicker?.minute.toString()
            val minuteAffterCheck = timerViewModel.checkIfSingle(settedTimeMinute)

            val sumTime = hourAffterCheck+minuteAffterCheck
            _binding?.textview?.text = getString(R.string.set_time,
                "$hourAffterCheck:$minuteAffterCheck")

            val fileName = "UserTimeSetting"

            timerViewModel.insertTimeToFile(fileName, sumTime)
        }

        return binding?.root
    }

    //set pop up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firstLaunch()
    }

    private fun hideKeyboardInputInTimePicker(orientation: Int, timePicker: TimePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ((timePicker.getChildAt(0) as LinearLayout).getChildAt(4) as LinearLayout).getChildAt(
                        0).visibility = View.GONE
                }
                else
                {
                    (((timePicker.getChildAt(0) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(
                        2) as LinearLayout).getChildAt(0).visibility = View.GONE
                }
            } catch (ex: Exception) {
            }

        }

    }

    //custom back behavior
    private fun fileExist(): Boolean? {
        return context?.getFileStreamPath("UserTimeSetting")?.exists()
    }

    //handle back btn after first launch
    override fun onAttach(context: Context) {
        super.onAttach(context)
        object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                Toast.makeText(context, "handleOnBackPressed", Toast.LENGTH_SHORT).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val fileExist = fileExist()
            if (fileExist!!) {
               findNavController().navigateUp()
            } else {
                Toast.makeText(context, "You need to save time first", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //if it is first launch, show pop up
    private fun firstLaunch() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(getString(R.string.pref_first_see_timer), false)
        if (!previouslyStarted) {
            val edit = prefs.edit()
            edit.putBoolean(getString(R.string.pref_first_see_timer), java.lang.Boolean.TRUE)
            edit.apply()
            showPopUp()
        }
    }

    private fun showPopUp() {
        val coordinatorLayout =
            requireView().findViewById(R.id.coordinatorLayout) as CoordinatorLayout
        try {
            Snackbar.make(
                coordinatorLayout,
                R.string.snackbar_message,
                2500
            ).show()
        } catch (e: NullPointerException) {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
