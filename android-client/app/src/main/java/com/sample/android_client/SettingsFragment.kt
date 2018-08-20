package com.sample.android_client

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.setting_row_settings.*


class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displaySettings()
    }

    private fun displaySettings() {
        val adapter = GroupAdapter<ViewHolder>()

        // ここはもうちょっと何とかしたほうがいい
        adapter.add(SettingRowItem("Profile"))
        adapter.add(SettingRowItem("Language"))
        adapter.add(SettingRowItem("Design"))
        adapter.add(SettingRowItem("Font Size"))

        adapter.setOnItemClickListener { item, view ->
            Log.d("SettingsFragment", item.toString())
            // TODO : 各設定のアクティビティに遷移する
        }

        recycler_view_settings.adapter = adapter
    }
}

data class SettingRowItem(val name: String) : Item() {
    override fun getLayout(): Int = R.layout.setting_row_settings

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.text_view_settings.text = name

        viewHolder.icon_settings.setImageResource(
                when (name) {
                    // ここも何とかしたほうがよさそう
                    "Profile" -> R.drawable.ic_people_black_24dp
                    "Language" -> R.drawable.ic_font_download_black_24dp
                    "Design" -> R.drawable.ic_palette_black_24dp
                    "Font Size" -> R.drawable.ic_font_download_black_24dp
                    else -> R.drawable.ic_settings_black_24dp
                }
        )
    }
}