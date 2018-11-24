package app.dav.universalsoundboard.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        login_button.setOnClickListener {
            val redirectUrl = "universalsoundboard:///"
            val url: Uri = Uri.parse(FileManager.loginImplicitUrl + "?api_key=" + FileManager.apiKey + "&redirect_url=" + redirectUrl)
            val intent = Intent(Intent.ACTION_VIEW, url)
            val packageManager = activity?.packageManager ?: return@setOnClickListener
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    companion object {
        fun newInstance() = AccountFragment()
    }
}
