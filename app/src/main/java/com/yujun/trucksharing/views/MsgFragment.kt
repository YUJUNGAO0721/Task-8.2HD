package com.yujun.trucksharing.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yujun.trucksharing.R
import com.yujun.trucksharing.msgutils.MessageViewHolder
import com.yujun.trucksharing.msgutils.Messages
import com.yujun.trucksharing.msgutils.MyButtonObserver
import com.yujun.trucksharing.msgutils.MyScrollToBottomObserver
import com.yujun.trucksharing.prefmanager.SharedPrefManager


class MsgFragment : Fragment() {

    companion object {
        fun newInstance() = MsgFragment()
    }

    private val TAG = "ChatMobile"

    var pref: SharedPrefManager? = null

    private val MESSAGES_CHILD = "messages-mobile"
    private val ANONYMOUS = "Anonymous"
    var sName: String? = null
    var usrProfileUrl: String? = null

    private val REQUEST_IMAGE = 2
    private val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"

//    private var mBinding: FragmentMsgBinding? = null
//    var binding: FragmentMsgBinding? = null

    private var recyclerView : RecyclerView? = null
    private var addMessageImageView : TextView? = null
    private var inputMessage : EditText? = null
    private var sendMessageBtn : TextView? = null
    private var progressBar : ProgressBar? = null

    // 1. Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null

    // initializing the Firebase Realtime Database and adding a listener to handle changes made to the data
    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mFirebaseAdapter: FirebaseRecyclerAdapter<Messages?, MessageViewHolder>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        mBinding = FragmentMsgBinding.inflate(inflater, container, false)
        val rootView = inflater.inflate(R.layout.fragment_msg, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        addMessageImageView = rootView.findViewById<TextView>(R.id.addMessageImageView)
        inputMessage = rootView.findViewById<EditText>(R.id.inputMessage)
        sendMessageBtn = rootView.findViewById<TextView>(R.id.sendMessageBtn)
        progressBar = rootView.findViewById<ProgressBar>(R.id.progressBar)

        pref = SharedPrefManager(this.requireContext())

        // 2. Initialize Firebase Auth and check if the user is signed in
        mFirebaseAuth = FirebaseAuth.getInstance()
        var mLinearLayoutManager = LinearLayoutManager(this.requireContext())
        mLinearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = mLinearLayoutManager

        // Initialize Realtime Database
        mDatabase = FirebaseDatabase.getInstance()
        val messagesRef = mDatabase!!.reference.child(MESSAGES_CHILD)
        mDatabaseReference = mDatabase!!.reference.child("Users")

        // The FirebaseRecyclerAdapter class comes from the FirebaseUI library
        // See: https://github.com/firebase/FirebaseUI-Android
        val options =
            FirebaseRecyclerOptions.Builder<Messages>()
                .setQuery(
                    messagesRef,
                    Messages::class.java
                )
                .build()
        mFirebaseAdapter = object : FirebaseRecyclerAdapter<Messages?, MessageViewHolder>(
            options
        ) {
            override fun onCreateViewHolder(
                viewGroup: ViewGroup,
                i: Int
            ): MessageViewHolder {
                val inflaterV = LayoutInflater.from(viewGroup.context)
                return MessageViewHolder(
                    inflaterV.inflate(R.layout.item_message, viewGroup, false)
                )
            }

            override fun onBindViewHolder(
                vh: MessageViewHolder,
                position: Int,
                message: Messages
            ) {
                progressBar!!.visibility = ProgressBar.INVISIBLE
                vh.bindMessage(message)
            }
        }
        mLinearLayoutManager = LinearLayoutManager(this.requireContext())
        mLinearLayoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = mLinearLayoutManager
        recyclerView!!.adapter = mFirebaseAdapter

        // Scroll down when a new message arrives
        // See MyScrollToBottomObserver.java for details
        mFirebaseAdapter!!.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                recyclerView!!,
                mFirebaseAdapter!!,
                mLinearLayoutManager
            )
        )
        inputMessage!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                sendMessageBtn!!.isEnabled =
                    charSequence.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // Disable the send button when there's no text in the input field
        // See MyButtonObserver.java for details
        inputMessage!!.addTextChangedListener(
            MyButtonObserver(
                sendMessageBtn!!
            )
        )

        // When the send button is clicked, send a text message
        sendMessageBtn!!.setOnClickListener { // Send messages on click.
            val messages =
                Messages(
                    inputMessage!!.text.toString(),
                    getUserName(),
                    getUserPhotoUrl(),
                    null /* no image */
                )
            mDatabase!!.reference.child(MESSAGES_CHILD).push().setValue(messages)
            inputMessage!!.setText("")
        }

        // When the image button is clicked, launch the image picker
        addMessageImageView!!.setOnClickListener { // Select image for image message on click.
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

//        return mBinding!!.root
        return rootView
    }

    // Once the user has selected an image, a call to the MainActivity's onActivityResult will be fired.
    // This is where you handle the user's image selection.
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val uri = data.data
                Log.d(TAG, "Uri: " + uri.toString())
                val user = mFirebaseAuth!!.currentUser
                val tempMessage =
                    Messages(
                        null, getUserName(), getUserPhotoUrl(), LOADING_IMAGE_URL
                    )
                mDatabase!!.reference.child(MESSAGES_CHILD).push()
                    .setValue(tempMessage,
                        DatabaseReference.CompletionListener { databaseError, databaseReference ->
                            if (databaseError != null) {
                                Log.w(
                                    TAG, "Unable to write message to database.",
                                    databaseError.toException()
                                )
                                return@CompletionListener
                            }

                            // Build a StorageReference and then upload the file
                            val key = databaseReference.key
                            val storageReference = FirebaseStorage.getInstance()
                                .getReference(user!!.uid)
                                .child(key!!)
                                .child(uri!!.lastPathSegment!!)
                            putImageInStorage(storageReference, uri, key)
                        })
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // store name of the user in a String var
        val mUserId = mFirebaseAuth!!.currentUser!!.uid
        mDatabaseReference!!.child(mUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sName = snapshot.child("fullname").value.toString()
                usrProfileUrl = snapshot.child("profileurl").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Once the upload is complete you will update the message to use the appropriate image.
    private fun putImageInStorage(storageReference: StorageReference, uri: Uri?, key: String?) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri!!)
            .addOnSuccessListener(this.requireActivity()) { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val messages =
                            Messages(
                                null, getUserName(), getUserPhotoUrl(), uri.toString()
                            )
                        mDatabase!!.reference
                            .child(MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(messages)
                    }
            }
            .addOnFailureListener(
                this.requireActivity()
            ) { e -> Log.w(TAG, "Image upload task was not successful.", e) }
    }

    override fun onPause() {
        mFirebaseAdapter!!.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAdapter!!.startListening()
    }

    // implement the getUserPhotoUrl() amd getUserName() methods
    private fun getUserPhotoUrl(): String? {

        val user = mFirebaseAuth!!.currentUser
        return if (user != null && user.photoUrl != null) {
            user.photoUrl.toString()
        } else pref!!.getPROFILE_PHOTO().toString() + ""

    }

    private fun getUserName(): String? {
        val user = mFirebaseAuth!!.currentUser
        return if (user != null) {
            user.displayName
        } else pref!!.getFULL_NAME()

    }
}