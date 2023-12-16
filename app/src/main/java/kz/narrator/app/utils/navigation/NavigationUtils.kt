package kz.narrator.app.utils.navigation

import androidx.navigation.NavOptions
import kz.narrator.app.R

fun getSlideLeftAnimBuilder(): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()

    navBuilder.setEnterAnim(R.anim.slide_enter_from_right_anim)
    navBuilder.setExitAnim(R.anim.nav_default_pop_exit_anim)
    navBuilder.setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
    navBuilder.setPopExitAnim(R.anim.slide_exit_to_right_anim)

    return navBuilder
}

fun getSlideUpAnimBuilder(): NavOptions.Builder {
    val navBuilder = NavOptions.Builder()

    navBuilder.setEnterAnim(R.anim.slide_up)
    navBuilder.setExitAnim(R.anim.nav_default_pop_exit_anim)
    navBuilder.setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
    navBuilder.setPopExitAnim(R.anim.slide_down)

    return navBuilder
}