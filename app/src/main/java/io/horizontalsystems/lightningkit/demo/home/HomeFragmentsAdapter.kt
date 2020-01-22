package io.horizontalsystems.lightningkit.demo.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.horizontalsystems.lightningkit.demo.channels.ChannelsFragment
import io.horizontalsystems.lightningkit.demo.payments.PaymentsFragment

class HomeFragmentsAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val fragments = arrayOf(
        Pair("Channels", { ChannelsFragment() }),
        Pair("Payments", { PaymentsFragment() })
    )

    override fun getCount(): Int = fragments.size

    override fun getItem(i: Int): Fragment {
        return fragments[i].second.invoke()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position].first
    }
}
