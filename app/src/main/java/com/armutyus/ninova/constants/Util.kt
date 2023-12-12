package com.armutyus.ninova.constants

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources.Theme
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.armutyus.ninova.NinovaApplication
import com.armutyus.ninova.R

class Util {

    companion object {

        fun progressDrawable(context: Context): CircularProgressDrawable {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.backgroundColor = R.color.md_theme_light_tertiary
            circularProgressDrawable.start()

            return circularProgressDrawable
        }

        fun View.fadeIn(durationMillis: Long) {
            this.startAnimation(AlphaAnimation(0F, 1F).apply {
                duration = durationMillis
                fillAfter = false
            })
        }

        fun Int.toLocalizedString(vararg formatArgs: Any? = emptyArray()): String =
            NinovaApplication.instance.getString(this, *formatArgs)

        private fun isDarkModeEnabled(context: Context): Boolean {
            val currentNightMode =
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }

        fun ImageView.setImageTint(context: Context) {
            val tintColor = if (isDarkModeEnabled(context)) Color.WHITE else Color.BLACK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.colorFilter =
                    BlendModeColorFilter(tintColor, BlendMode.SRC_ATOP)
            } else {
                this.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
            }
        }

        fun checkAndApplyTheme(themePreferences: SharedPreferences, theme: Theme) {
            when (themePreferences.getString("theme", Constants.NINOVA_LIGHT_THEME)) {
                Constants.NINOVA_LIGHT_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.NINOVA_LIGHT_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Ninova, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                Constants.NINOVA_DARK_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.NINOVA_DARK_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Ninova, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                Constants.BERGAMA_LIGHT_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.BERGAMA_LIGHT_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Bergama, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                Constants.BERGAMA_DARK_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.BERGAMA_DARK_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Bergama, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                Constants.ALEXANDRIA_LIGHT_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.ALEXANDRIA_LIGHT_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Alexandria, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                Constants.ALEXANDRIA_DARK_THEME -> {
                    themePreferences.edit()?.putString("theme", Constants.ALEXANDRIA_DARK_THEME)
                        ?.apply()
                    theme.applyStyle(R.style.Theme_Alexandria, true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

            }
        }

    }

}