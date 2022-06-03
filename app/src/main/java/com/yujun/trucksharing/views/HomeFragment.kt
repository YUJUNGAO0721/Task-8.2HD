package com.yujun.trucksharing.views

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yujun.trucksharing.MainActivity
import com.yujun.trucksharing.R
import com.yujun.trucksharing.adapter.AdapterOrders
import com.yujun.trucksharing.prefmanager.SharedPrefManager
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var adapterOrders : AdapterOrders? = null
    var itemsRecycler : RecyclerView? = null

    var pref: SharedPrefManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        pref = SharedPrefManager(this.requireContext())

        val floatingBtn = rootView.findViewById<FloatingActionButton>(R.id.floatingBtn)
        floatingBtn.setOnClickListener {
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.containerMain, NewDeliveryFragment.newInstance())
                .commitNow()
        }

        // set photo if previously uploaded
        val homeProfile = rootView.findViewById<CircleImageView>(R.id.homeProfile)
        val base = pref!!.getPROFILE_PHOTO().toString() + ""
        if (pref!!.getPROFILE_PHOTO()!!.length > 10) {
            val imageAsBytes = Base64.decode(base.toByteArray(), Base64.DEFAULT)
            homeProfile!!.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageAsBytes,
                    0,
                    imageAsBytes.size
                )
            )
        }

        val a = this.requireActivity() as MainActivity

        /**
         * Init adapter and recyclerView
         * Set data to UI
         */
        itemsRecycler = rootView.findViewById(R.id.homeRecyclerView)

        val layoutManager = LinearLayoutManager(this@HomeFragment.context)
        itemsRecycler!!.layoutManager = layoutManager

        adapterOrders = AdapterOrders(a.readOrdersData(), this@HomeFragment.requireContext())
        itemsRecycler!!.adapter = adapterOrders

        // Check if data is empty
        val noDataTv = rootView.findViewById<TextView>(R.id.noData)
        if (a.readOrdersData().isEmpty()) {
            noDataTv.visibility = View.VISIBLE
            itemsRecycler!!.visibility = View.GONE
        } else {
            itemsRecycler!!.visibility = View.VISIBLE
            noDataTv.visibility = View.GONE
        }

        return rootView
    }
}