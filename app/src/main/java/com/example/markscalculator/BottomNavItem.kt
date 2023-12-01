package com.example.markscalculator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

sealed class BottomNavItem (var title: String, var icon: ImageVector, var screen_route: String) {
    object Home : BottomNavItem("Home", Icons.Rounded.Home, "home")
    object History : BottomNavItem("Scan", Icons.Rounded.QrCodeScanner, "scan")
}