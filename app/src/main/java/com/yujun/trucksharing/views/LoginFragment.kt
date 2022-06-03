package com.yujun.trucksharing.views

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.yujun.trucksharing.R
import com.yujun.trucksharing.UsersActivity
import kotlin.system.exitProcess

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        val a = this.requireActivity() as UsersActivity

        val eUserName = rootView.findViewById<EditText>(R.id.loginUserName)
        val ePassword = rootView.findViewById<EditText>(R.id.loginPassword)

        // signup
        val signUpBtn = rootView.findViewById<AppCompatButton>(R.id.signUpBtn)
        signUpBtn.setOnClickListener {
            (this.activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, SignupFragment.newInstance())
                .commitNow()
        }

        // login
        val loginBtn = rootView.findViewById<View>(R.id.loginBtn)
        loginBtn.setOnClickListener {
//            activity?.let {
//                val i = Intent(it, MainActivity::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(i)
//            }

            when {
                TextUtils.isEmpty(eUserName.text.toString().trim()) -> {
                    eUserName.error = "UserName Required!"
                }
                TextUtils.isEmpty(ePassword.text.toString().trim()) -> {
                    ePassword.error = "Password Required!"
                }
                else -> {
                    val usrName = eUserName.text.toString().trim()
                    val usrPassword = ePassword.text.toString().trim()
                    a.loginUser(usrName, usrPassword)
                }
            }
        }

        return rootView
    }

    // BackPress
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Let's handle onClick back btn
                    exitProcess(0)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }
}