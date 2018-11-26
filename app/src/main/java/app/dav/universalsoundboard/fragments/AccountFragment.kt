package app.dav.universalsoundboard.fragments

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.davandroidlibrary.models.DavPlan
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import kotlinx.android.synthetic.main.fragment_account.*
import java.text.DecimalFormat

class AccountFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        account_fragment_link.movementMethod = LinkMovementMethod.getInstance()
        account_fragment_upgrade_link.movementMethod = LinkMovementMethod.getInstance()

        account_fragment_login_button.setOnClickListener {
            val redirectUrl = "universalsoundboard:///"
            val url: Uri = Uri.parse(FileManager.loginImplicitUrl + "?api_key=" + FileManager.apiKey + "&redirect_url=" + redirectUrl)
            val intent = Intent(Intent.ACTION_VIEW, url)
            val packageManager = activity?.packageManager ?: return@setOnClickListener
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        account_fragment_signup_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dav-apps.tech/signup"))
            val packageManager = activity?.packageManager ?: return@setOnClickListener
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        account_fragment_logout_button.setOnClickListener {
            val fragment = LogoutDialogFragment()
            fragment.show(fragmentManager, "logout")
        }

        FileManager.itemViewHolder.user.observe(this, Observer {
            it ?: return@Observer

            // If the user is logged in, hide the login page and show the user details
            account_fragment_constraint_layout_1.visibility = if(it.isLoggedIn) View.GONE else View.VISIBLE
            account_fragment_constraint_layout_2.visibility = if(it.isLoggedIn) View.VISIBLE else View.GONE

            if(it.isLoggedIn){
                // Set the username TextView
                account_fragment_username_text_view.text = it.username

                // Set the avatar ImageView
                val avatar = it.avatar
                if(avatar.exists()){
                    val avatarDrawable = RoundedBitmapDrawableFactory.create(resources, avatar.path)
                    avatarDrawable.cornerRadius = 600f
                    account_fragment_avatar_image_view.setImageDrawable(avatarDrawable)
                }

                // Set the used storage text
                val usedStorageGB = it.usedStorage / 1000000000.0
                val totalStorageGB = it.totalStorage / 1000000000.0
                val usedStorageGBString = DecimalFormat("#.#").format(usedStorageGB)
                val totalStorageGBString = DecimalFormat("#.#").format(totalStorageGB)
                account_fragment_storage_text_view.text = getString(R.string.account_fragment_used_storage, usedStorageGBString, totalStorageGBString)

                // Set the value of the progress bar
                account_fragment_storage_progress_bar.progress = ((usedStorageGB / totalStorageGB) * 100).toInt()

                // Set the visibility of the upgrade link
                account_fragment_upgrade_link.visibility = if(it.plan == DavPlan.Free) View.VISIBLE else View.GONE
            }
        })
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
