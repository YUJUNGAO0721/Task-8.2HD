package com.yujun.trucksharing.views

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yujun.trucksharing.R
import com.yujun.trucksharing.prefmanager.SharedPrefManager
import de.hdodenhof.circleimageview.CircleImageView

class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    var pref: SharedPrefManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        pref = SharedPrefManager(this.requireContext())

        val acBackBtn = rootView.findViewById<FloatingActionButton>(R.id.acBackBtn)
        val acPhoto = rootView.findViewById<CircleImageView>(R.id.acPhoto)
        val acFullName = rootView.findViewById<TextView>(R.id.acFullName)
        val acPhoneNo = rootView.findViewById<TextView>(R.id.acPhoneNo)

        acBackBtn.setOnClickListener {
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, HomeFragment.newInstance())
                .commitNow()
        }

        // set photo if previously uploaded
        val base = pref!!.getPROFILE_PHOTO().toString() + ""
        if (pref!!.getPROFILE_PHOTO()!!.length > 10) {
            val imageAsBytes = Base64.decode(base.toByteArray(), Base64.DEFAULT)
            acPhoto!!.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageAsBytes,
                    0,
                    imageAsBytes.size
                )
            )
        }

        acFullName.text = pref!!.getFULL_NAME()
        acPhoneNo.text = pref!!.getMOBILE()

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Let's handle onClick back btn
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.containerMain, HomeFragment.newInstance()).commitNow()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}