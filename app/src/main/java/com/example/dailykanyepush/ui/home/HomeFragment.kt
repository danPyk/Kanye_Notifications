package com.example.dailykanyepush.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.dailykanyepush.R
import com.google.firebase.messaging.FirebaseMessaging


//sys time, how to pass,

//first page with info
// report bug, buy bear, contact

//delete one cart
//first deploy on phone, then thinking how to manage hours

class HomeFragment : androidx.fragment.app.Fragment() {
    private val TOPIC = "kanyepush"

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding: com.example.dailykanyepush.databinding.FragmentHomeBinding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false
            )

        val application = requireNotNull(this.activity).application

        val viewModelFactory = SleepTrackerViewModelFactory(application)

        //ref to sleepTrackerViewModel
        val sleepTrackerViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(HomeViewModel::class.java)

        binding.setLifecycleOwner(this)

        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.quoteTextView.text = sleepTrackerViewModel.getQuote()
        binding.quoteTextView.invalidate()

        createChannel(
            getString(R.string.egg_notification_channel_id),
            getString(R.string.egg_notification_channel_name)
        )
        subscribeTopic()

        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )
                // TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.breakfast_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
        // TODO: Step 1.6 END create channel
    }

    //allow send notif to multiple users
    private fun subscribeTopic() {
        // [START subscribe_topic]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var message = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = getString(R.string.message_subscribe_failed)
                }
            }
        // [END subscribe_topics]
    }


}