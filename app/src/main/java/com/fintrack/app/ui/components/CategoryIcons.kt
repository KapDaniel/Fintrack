package com.fintrack.app.ui.components

import androidx.annotation.DrawableRes
import com.fintrack.app.R

/**
 * Ключ категории из базы → иконка. Ключи хранятся строками, чтобы новая
 * категория не требовала миграции схемы.
 */
object CategoryIcons {

    @DrawableRes
    fun iconFor(iconKey: String): Int = when (iconKey) {
        "bus" -> R.drawable.ic_bus
        "shopping_bag" -> R.drawable.ic_shopping_bag
        "key" -> R.drawable.ic_key
        "briefcase" -> R.drawable.ic_briefcase
        "repeat" -> R.drawable.ic_repeat
        "piggy_bank" -> R.drawable.ic_piggy_bank
        "credit_card" -> R.drawable.ic_credit_card
        "coffee" -> R.drawable.ic_coffee
        "message" -> R.drawable.ic_message
        else -> R.drawable.ic_wallet
    }
}
