package com.yujun.trucksharing.views

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yujun.trucksharing.R
import com.yujun.trucksharing.UsersActivity
import com.yujun.trucksharing.prefmanager.SharedPrefManager
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class SignupFragment : Fragment() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private var uploadPhoto: CircleImageView? = null
    private val MY_CAMERA_PERMISSION_CODE = 200
    private val MY_STORAGE_WRITE_CODE = 200
    private val REQUEST_GALLERY = 0
    private val REQUEST_CAMERA = 1
    private var imageUri: Uri? = null
    var photoType = 0
    var encoded = ""
    var pref: SharedPrefManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_signup, container, false)

        pref = SharedPrefManager(this.requireContext())

        val a = this.requireActivity() as UsersActivity

        uploadPhoto = rootView.findViewById<CircleImageView>(R.id.uploadPhoto)

        // set photo if previously uploaded
        val base = pref!!.getPROFILE_PHOTO().toString() + ""
        if (pref!!.getPROFILE_PHOTO()!!.length > 10) {
            val imageAsBytes = Base64.decode(base.toByteArray(), Base64.DEFAULT)
            uploadPhoto!!.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageAsBytes,
                    0,
                    imageAsBytes.size
                )
            )
        }

        val eFullName = rootView.findViewById<EditText>(R.id.fullName)
        val ePhone = rootView.findViewById<EditText>(R.id.phoneNumber)
        val eUserName = rootView.findViewById<EditText>(R.id.signUpUserName)
        val ePassword = rootView.findViewById<EditText>(R.id.password)
        val eConfirmPassword = rootView.findViewById<EditText>(R.id.confirmPassword)

        val createAccount = rootView.findViewById<AppCompatButton>(R.id.createAccount)
        createAccount.setOnClickListener {
            when {
                TextUtils.isEmpty(eFullName.text.toString().trim()) -> {
                    eFullName.error = "Name required!"
                }
                TextUtils.isEmpty(eUserName.text.toString().trim()) -> {
                    eUserName.error = "UserName required!"
                }
                TextUtils.isEmpty(ePhone.text.toString().trim()) -> {
                    ePhone.error = "Phone required!"
                }
                TextUtils.isEmpty(ePassword.text.toString().trim()) -> {
                    ePassword.error = "Password required!"
                }
                TextUtils.isEmpty(eConfirmPassword.text.toString().trim()) -> {
                    eConfirmPassword.error = "Confirm password required!"
                }
                else -> {
                    val p = ePassword.text.toString().trim()
                    val cP = eConfirmPassword.text.toString().trim()
                    if(p != cP) {
                        eConfirmPassword.error = "Password Mismatch!"
                    } else {
                        val fullName = eFullName.text.toString().trim()
                        val userName = eUserName.text.toString().trim()
                        val phoneNo = ePhone.text.toString().trim()
                        val password = ePassword.text.toString().trim()
                        val photoStr = pref!!.getPROFILE_PHOTO().toString() + ""
                        Log.d("res", "$fullName : $userName : $phoneNo : $password : $photoStr")
                        a.createNewUser(fullName, userName, phoneNo, password, photoStr)
                    }
                }
            }
        }

        uploadPhoto!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this.requireContext(),
                    Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    AlertDialog.Builder(this.requireContext())
                        .setTitle("Action: Accept Camera Permission")
                        .setMessage("This app needs the camera permission, please accept to take photos with your camera")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(Manifest.permission.CAMERA),
                                MY_CAMERA_PERMISSION_CODE
                            )
                        }
                        .create()
                        .show()
                } else {
                    // We can request the permission.
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                    val filename = System.currentTimeMillis().toString() + ".jpg"
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, filename)
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    imageUri = (this.requireActivity()).contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                    val intent = Intent()
                    intent.action = MediaStore.ACTION_IMAGE_CAPTURE
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            } else if (ActivityCompat.checkSelfPermission(this.requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    AlertDialog.Builder(this.requireContext())
                        .setTitle("Write Storage Permission")
                        .setMessage("This app needs the external permission to write, please accept to use the permission functionality")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_STORAGE_WRITE_CODE
                            )
                        }
                        .create()
                        .show()
                } else {
                    // We can request the permission.
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_STORAGE_WRITE_CODE
                    )
                }
            } else {
                // Permission previously granted
                galleryChooseGallery()
            }
        }

        return rootView
    }

    private fun galleryChooseGallery() {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.dialog_chooser)
        dialog.setCancelable(false)

        val cameraBtn = dialog.findViewById<ImageView>(R.id.cameraBtn)
        val galleryBtn = dialog.findViewById<ImageView>(R.id.galleryBtn)

        cameraBtn.setOnClickListener {
            photoType = 1
            dialog.dismiss()
            val filename = System.currentTimeMillis().toString() + ".jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, filename)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            imageUri = (context as Activity).contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            val intent = Intent()
            intent.action = MediaStore.ACTION_IMAGE_CAPTURE
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        }

        galleryBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_GALLERY)
        }

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation_1
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GALLERY -> {
                // GALLERY
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImage = data!!.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            //getApplicationContext().getContentResolver(),
                            (context as Activity).contentResolver,
                            selectedImage
                        )
                        uploadPhoto!!.setImageBitmap(bitmap)
                        uploadPhoto!!.setPadding(0, 0, 0, 0)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
                        pref!!.setPROFILE_PHOTO(encoded)
                        Log.d("logImgGallery", encoded)
                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception $e")
                    }
                }
            }
            REQUEST_CAMERA -> {
                // CAMERA
                if (resultCode == Activity.RESULT_OK) {
                    if (imageUri != null) {
                        var `is`: InputStream? = null
                        var bitmap: Bitmap? = null
                        try {
                            `is` = (context as Activity).contentResolver.openInputStream(imageUri!!)
                            val options = BitmapFactory.Options()
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888
                            options.inSampleSize = 2
                            options.inScreenDensity = DisplayMetrics.DENSITY_LOW
                            bitmap = BitmapFactory.decodeStream(`is`, null, options)
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                            val byteArray = byteArrayOutputStream.toByteArray()
                            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
                            pref!!.setPROFILE_PHOTO(encoded)
                            val decodedString = Base64.decode(encoded, Base64.DEFAULT)
                            val decodedByte =
                                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            uploadPhoto!!.setImageBitmap(decodedByte)
                            uploadPhoto!!.setPadding(0, 0, 0, 0)
                            Log.d("logImgCam", encoded)
                        } catch (e: FileNotFoundException) {
                            Log.w("failed", "Failed to find the file: $imageUri", e)
                        } finally {
                            bitmap?.recycle()
                            if (`is` != null) {
                                try {
                                    `is`.close()
                                } catch (e: IOException) {
                                    Log.w("failed", "Failed to close InputStream", e)
                                }
                            }
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    // BackPress
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Let's handle onClick back btn
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance()).commitNow()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}