package com.ubergeek42.WeechatAndroid.views

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ubergeek42.WeechatAndroid.R
import com.ubergeek42.WeechatAndroid.WeechatActivity
import com.ubergeek42.WeechatAndroid.service.P
import com.ubergeek42.WeechatAndroid.utils.WeaselMeasuringViewPager


val FULL_SCREEN_DRAWER_ENABLED = Build.VERSION.SDK_INT >= 27


fun onWeechatActivityCreated(activity: WeechatActivity) {
    if (!FULL_SCREEN_DRAWER_ENABLED) return

    val toolbarContainer = activity.findViewById<View>(R.id.toolbar_container)
    val viewPager = activity.findViewById<WeaselMeasuringViewPager>(R.id.main_viewpager)
    val navigationPadding = activity.findViewById<View>(R.id.navigation_padding)
    val rootView = viewPager.rootView

    navigationPadding.visibility = View.VISIBLE
    navigationPadding.setBackgroundColor(P.colorPrimaryDark)

    // todo use WindowCompat.setDecorFitsSystemWindows(window, false)
    // todo needs api 30+? and/or androidx.core:core-ktx:1.5.0-beta02
    rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    rootView.setOnApplyWindowInsetsListener listener@{ _, insets ->
        val systemTop = insets.systemWindowInsetTop
        val systemBottom = insets.systemWindowInsetBottom

        toolbarContainer.updatePadding(top = systemTop)
        navigationPadding.updateLayoutParams<ViewGroup.MarginLayoutParams> { height = systemBottom }
        viewPager.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = systemBottom }

        // todo deal with the keyboard

        insets
    }
}


fun onBufferListFragmentStarted(bufferList: Fragment) {
    if (!FULL_SCREEN_DRAWER_ENABLED) return

    val bufferListView = bufferList.requireView()
    val navigationPadding = bufferListView.findViewById<View>(R.id.navigation_padding)
    val layoutManager = bufferListView.findViewById<RecyclerView>(R.id.recycler)
            .layoutManager as FullScreenDrawerLinearLayoutManager

    navigationPadding.setBackgroundColor(P.colorPrimaryDark)

    val filterBar = bufferListView.findViewById<View>(R.id.filter_bar)

    fun applyInsets() {
        if (P.showBufferFilter) {
            navigationPadding.visibility = View.VISIBLE
            navigationPadding.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                height = SystemWindowInsets.bottom }
            filterBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = SystemWindowInsets.bottom }
            layoutManager.setInsets(SystemWindowInsets.top, 0)
        } else {
            navigationPadding.visibility = View.GONE
            layoutManager.setInsets(SystemWindowInsets.top, SystemWindowInsets.bottom)
        }
    }

    bufferListView.setOnApplyWindowInsetsListener { _, insets ->
        SystemWindowInsets.top = insets.systemWindowInsetTop
        SystemWindowInsets.bottom = insets.systemWindowInsetBottom

        applyInsets()

        insets
    }

    applyInsets()
}


object SystemWindowInsets {
    var top = 0
    var bottom = 0
}