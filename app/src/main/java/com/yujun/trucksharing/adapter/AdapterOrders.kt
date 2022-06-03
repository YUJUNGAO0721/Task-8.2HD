package com.yujun.trucksharing.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yujun.trucksharing.MainActivity
import com.yujun.trucksharing.R
import com.yujun.trucksharing.model.OrdersModel
import com.yujun.trucksharing.prefmanager.SharedPrefManager

class AdapterOrders (var dataModelItemsModel : List<OrdersModel>, var mContext: Context) :
    RecyclerView.Adapter<AdapterOrders.RVHolder?>() {

    var pref: SharedPrefManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.item_rv, parent, false)
        return RVHolder(listItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        pref = SharedPrefManager(mContext)

        holder.hReceiverName.text = "Receiver: " + dataModelItemsModel[position].receiverName
        val parts: List<String> = dataModelItemsModel[position].itemLocation!!.split("lat/lng:")
        val title = parts[0]
        holder.hLocationAndTime.text = title + ", Time-" + dataModelItemsModel[position].pickUpTime
        holder.hDate.text = "Pick On: " + dataModelItemsModel[position].pickUpDate
        holder.hPostedBy.text = "Post By: " + pref!!.getUSER_NAME()

        val a = (mContext as MainActivity)
        holder.lnLayoutHolder.setOnClickListener {
            a.intentToDetails(
                dataModelItemsModel[position].itemId!!.toLong(), dataModelItemsModel[position].itemLocation!!,
                dataModelItemsModel[position].pickUpTime!!, dataModelItemsModel[position].receiverName!!,
                dataModelItemsModel[position].weight!!, dataModelItemsModel[position].width!!,
                dataModelItemsModel[position].height!!, dataModelItemsModel[position].length!!,
                dataModelItemsModel[position].goodType!!, dataModelItemsModel[position].vehicleType!!)

        }

        holder.hShareBtn.setOnClickListener {
            a.shareData(
                dataModelItemsModel[position].itemLocation!!, dataModelItemsModel[position].pickUpTime!!,
                dataModelItemsModel[position].receiverName!!, dataModelItemsModel[position].weight!!,
                dataModelItemsModel[position].width!!, dataModelItemsModel[position].height!!,
                dataModelItemsModel[position].length!!, dataModelItemsModel[position].goodType!!,
                dataModelItemsModel[position].vehicleType!!
            )
        }
    }

    override fun getItemCount(): Int {
        return dataModelItemsModel.size
    }

    class RVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hReceiverName: TextView = itemView.findViewById<View>(R.id.hReceiverName) as TextView
        var hLocationAndTime: TextView = itemView.findViewById<View>(R.id.hLocationAndTime) as TextView
        var hDate: TextView = itemView.findViewById<View>(R.id.hDate) as TextView
        var hPostedBy: TextView = itemView.findViewById<View>(R.id.hPostedBy) as TextView
        var hShareBtn: ImageView = itemView.findViewById<View>(R.id.shareBtn) as ImageView
        var lnLayoutHolder: LinearLayout = itemView.findViewById<View>(R.id.lnLayout) as LinearLayout

    }
}