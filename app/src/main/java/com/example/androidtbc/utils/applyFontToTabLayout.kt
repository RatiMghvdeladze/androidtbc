package com.example.androidtbc.utils

import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.viewpager2.widget.ViewPager2
import com.example.androidtbc.R
import com.google.android.material.tabs.TabLayout

fun synchronizeTabLayoutAndViewPager(
    tabLayout: TabLayout,
    viewPager2: ViewPager2,
    textSizeSp: Float = 14f
) {
    val typeface = ResourcesCompat.getFont(tabLayout.context, R.font.poppins_bold_italic)

    // Force equal width distribution for all tabs
    tabLayout.apply {
        tabMode = TabLayout.MODE_FIXED
        tabGravity = TabLayout.GRAVITY_FILL
    }

    // Initial configuration for all tabs
    applyFontToAllTabs(tabLayout, typeface, textSizeSp)

    // Add TabLayout listener to maintain font when tabs are clicked
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            applyFontToAllTabs(tabLayout, typeface, textSizeSp)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            // Do nothing here
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            // Do nothing here
        }
    })

    // Add ViewPager2 page change listener to maintain font when swiping
    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            // Reapply font to all tabs after page is selected via swiping
            applyFontToAllTabs(tabLayout, typeface, textSizeSp)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                // Also apply font when scrolling ends
                applyFontToAllTabs(tabLayout, typeface, textSizeSp)
            }
        }
    })
}

private fun applyFontToAllTabs(tabLayout: TabLayout, typeface: Typeface?, textSizeSp: Float) {
    // Small delay to ensure TabLayout has finished its own updates
    tabLayout.post {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.let { configureTabText(it, typeface, textSizeSp) }
        }
    }
}

private fun configureTabText(tab: TabLayout.Tab, typeface: Typeface?, textSizeSp: Float) {
    try {
        // Get the tab's custom view or create one
        val customView = tab.customView ?: TextView(tab.view.context).also {
            // Save the original text
            val originalText = tab.text

            // Configure the TextView
            it.apply {
                text = originalText
                this.typeface = typeface
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
                gravity = android.view.Gravity.CENTER
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(tab.view.context.getColor(android.R.color.white))

                // Match the parent's height and distribute width evenly
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }

            // Set our custom view to the tab
            tab.customView = it
        }
    } catch (e: Exception) {
        // Fallback method if custom view approach fails
        val tabView = tab.view
        val textView = findTextViewInTabView(tabView)
        textView?.apply {
            this.typeface = typeface
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
    }
}

private fun findTextViewInTabView(view: ViewGroup): TextView? {
    for (i in 0 until view.childCount) {
        val child = view.getChildAt(i)
        if (child is TextView) {
            return child
        } else if (child is ViewGroup) {
            val textView = findTextViewInTabView(child)
            if (textView != null) {
                return textView
            }
        }
    }
    return null
}