package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        if(view is ViewPager){
            view.adapter = SettingsTabsPagerAdapter(childFragmentManager)
            setupTabLayout(view)
        }

        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden){
            hideTabLayout()
        }else{
            setupTabLayout(null)
        }
    }

    private fun setupTabLayout(pager: ViewPager?){
        val tablayout = activity?.findViewById<TabLayout>(R.id.tablayout)
        tablayout?.visibility = View.VISIBLE
        tablayout?.setupWithViewPager(pager ?: settings_viewpager)
    }

    private fun hideTabLayout(){
        val tablayout = activity?.findViewById<TabLayout>(R.id.tablayout)
        tablayout?.visibility = View.GONE
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}

class SettingsTabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> SettingsFragmentGeneralTab()
            else -> SettingsFragmentDesignTab()
        }
    }

    override fun getCount(): Int {
        return 2;
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> FileManager.itemViewHolder.mainActivity?.resources?.getString(R.string.settings_fragment_general_tab) ?: "General"
            else -> FileManager.itemViewHolder.mainActivity?.resources?.getString(R.string.settings_fragment_design_tab) ?: "Design"
        }
    }
}