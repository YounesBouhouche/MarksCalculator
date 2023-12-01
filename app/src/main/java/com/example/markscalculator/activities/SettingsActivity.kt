package com.example.markscalculator.activities

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import com.example.markscalculator.R
import com.example.markscalculator.components.CheckSettingsItem
import com.example.markscalculator.components.Dialog
import com.example.markscalculator.components.DialogButtons
import com.example.markscalculator.components.SettingsItem
import com.example.markscalculator.components.settingsLabel
import com.example.markscalculator.components.settingsRadioItems
import com.example.markscalculator.datastores.Settings
import com.example.markscalculator.findActivity
import com.example.markscalculator.ui.theme.AppTheme
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val dataStore = Settings(LocalContext.current)
            var colorThemeDialogShown by remember { mutableStateOf(false) }
            var appLanguageDialogShown by remember { mutableStateOf(false) }
            val dynamicColorsChecked by dataStore.dynamicColors.collectAsState(initial = true)
            val extraDarkChecked by dataStore.extraDark.collectAsState(initial = true)
            val theme by dataStore.theme.collectAsState(initial = "system")
            val colorTheme by dataStore.colorTheme.collectAsState(initial = "green")
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val listState = rememberLazyListState()
            val languages = mapOf(
                "system" to R.string.follow_system,
                "en" to R.string.english,
                "fr" to R.string.french,
                "ar" to R.string.arabic,
                "es" to R.string.spanish,
                "it" to R.string.italian,
                "in" to R.string.hindi
            )
            val language by dataStore.language.collectAsState(initial = "system")
            val isDark = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            var selectedLanguage by remember { mutableStateOf("") }
            val colors = listOf(
                "blue" to R.string.blue,
                "green" to R.string.green,
                "red" to R.string.red,
                "orange" to R.string.orange,
                "purple" to R.string.purple
            )
            val themeOptions = listOf(
                "system" to R.string.default_theme,
                "light" to R.string.light,
                "dark" to R.string.dark,
            )
            AppTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        topBar = {
                            Column {
                                LargeTopAppBar(
                                    title = {
                                        Text(
                                            stringResource(id = R.string.settings),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { (context as Activity).finish() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                        }
                                    },
                                    scrollBehavior = scrollBehavior
                                )
                            }
                        }
                    ) { paddingValues ->
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues), state = listState) {
                            settingsLabel(R.string.theme)
                            item {
                                SettingsItem(
                                    Icons.Default.InvertColors,
                                    R.string.app_theme,
                                    R.string.choose_app_theme
                                )
                            }
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                    SingleChoiceSegmentedButtonRow {
                                        themeOptions.forEachIndexed { index, pair ->
                                            SegmentedButton(
                                                shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
                                                onClick = {
                                                    scope.launch {
                                                        dataStore.saveSettings(theme = pair.first)
                                                    }
                                                },
                                                selected = theme == pair.first
                                            ) {
                                                Text(stringResource(id = pair.second))
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(Modifier.height(4.dp))
                            }
                            item {
                                AnimatedContent(targetState = isDark, label = "") {
                                    if (it)
                                        CheckSettingsItem(
                                            icon = Icons.Default.DarkMode,
                                            title = R.string.extra_dark_colors,
                                            text = R.string.extra_dark_description,
                                            checked = extraDarkChecked,
                                            onCheckedChange = { checked ->
                                                scope.launch {
                                                    dataStore.saveSettings(extraDark = checked)
                                                }
                                            }
                                        )
                                }
                            }
                            item {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    CheckSettingsItem(
                                        icon = Icons.Default.Palette,
                                        title = R.string.dynamic_colors,
                                        text = R.string.follow_system_dynamic_colors,
                                        checked = dynamicColorsChecked,
                                        onCheckedChange = { checked ->
                                            scope.launch {
                                                dataStore.saveSettings(dynamic = checked)
                                            }
                                        }
                                    )
                                }
                            }
                            item {
                                AnimatedContent(targetState = (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) or !dynamicColorsChecked, label = "") {
                                    if (it)
                                        SettingsItem(
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) Icons.Default.Palette else null,
                                            R.string.color_palette,
                                            colors.toMap()[colorTheme]!!,
                                            onClick = { colorThemeDialogShown = true }
                                        )
                                }
                            }
                            settingsLabel(R.string.general)
                            item {
                                SettingsItem(
                                    Icons.Default.Language,
                                    R.string.language,
                                    languages[language]!!,
                                    onClick = {
                                        appLanguageDialogShown = true
                                        selectedLanguage = language
                                    }
                                )
                            }
                            settingsLabel(R.string.about)
                            item {
                                OutlinedCard(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                    Spacer(Modifier.height(16.dp))
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                                        Spacer(Modifier.width(4.dp))
                                        //Icon(ImageVector.vectorResource(R.drawable.ic_app_icon), null, modifier = Modifier.size(60.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)) {
                                            Text(
                                                text = stringResource(id = R.string.app_name),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                                    context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
                                                else
                                                    context.packageManager.getPackageInfo(context.packageName, 0)).versionName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())){
                                        Spacer(Modifier.width(16.dp))
                                        OutlinedButton(onClick = {

                                        }) {
                                            Icon(ImageVector.vectorResource(R.drawable.ic_telegram_app), null)
                                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                            Text(stringResource(R.string.group_chat_coming_soon))
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        OutlinedButton(onClick = {

                                        }) {
                                            Icon(Icons.Rounded.Apps, null)
                                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                            Text(stringResource(R.string.more_apps_coming_soon))
                                        }
                                        Spacer(Modifier.width(16.dp))
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Rounded.Person, null, Modifier.size(64.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = stringResource(R.string.younes_bouhouche),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            stringResource(R.string.developer_description),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.outline,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            OutlinedIconButton(onClick = {}) {
                                                Icon(Icons.Default.Link, null)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                with(Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("mailto:")
                                                    putExtra(
                                                        Intent.EXTRA_EMAIL,
                                                        arrayOf("younes.bouhouche12@gmail.com")
                                                    )
                                                    putExtra(
                                                        Intent.EXTRA_SUBJECT,
                                                        "Feedback about Marks Calculator app"
                                                    )
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "\nApp Version:${
                                                            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                                                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
                                                            else
                                                                context.packageManager.getPackageInfo(context.packageName, 0)).versionName
                                                        }\nAPI Level:${Build.VERSION.SDK_INT}"
                                                    )
                                                }) {
                                                    if (this.resolveActivity(context.packageManager) != null)
                                                        context.startActivity(this)
                                                }
                                            }) {
                                                Icon(Icons.Default.Email, null)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                with(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("twitter://user?screen_name=younesbouh_05")
                                                    )
                                                ) {
                                                    if (this.resolveActivity(context.packageManager) != null)
                                                        context.startActivity(this)
                                                    else
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse("https://twitter.com/younesbouh_05")
                                                            )
                                                        )
                                                }
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_twitter),
                                                    null
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                with(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/younesbouh_05")
                                                    )
                                                ) {
                                                    if (this.resolveActivity(context.packageManager) != null)
                                                        context.startActivity(this)
                                                    else
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse("https://facebook.com/younesbouh_05")
                                                            )
                                                        )
                                                }
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_facebook),
                                                    null
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://www.instagram.com/younesbouh_05")
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_instagram),
                                                    null
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("tg://resolve?domain=younesbouh_05")
                                                    )
                                                )
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_telegram_app),
                                                    null
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(Modifier.height(16.dp))
                                Text(stringResource(id = R.string.more), modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(16.dp))
                            }
                            item {
                                OutlinedCard(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp), verticalAlignment = Alignment.CenterVertically){
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Default.Translate, null)
                                        Spacer(Modifier.width(16.dp))
                                        Column(modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)) {
                                            Text(
                                                text = stringResource(R.string.improve_translation),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = stringResource(R.string.improve_translation_description),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.navigationBarsPadding())
                            }
                        }
                    }
                }
                Dialog(
                    visible = colorThemeDialogShown,
                    onDismissRequest = { colorThemeDialogShown = false },
                    title = stringResource(R.string.color_palette),
                    centerTitle = true
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        settingsRadioItems(
                            colors,
                            colors.map { it.first }.indexOf(colorTheme),
                            { scope.launch { dataStore.saveSettings(colorTheme = colors.map { it.first }[it]) } }
                        ) { Text(stringResource(it.second)) }
                    }
                    DialogButtons(
                        okListener = { colorThemeDialogShown = false }
                    )
                }
                Dialog(
                    visible = appLanguageDialogShown,
                    onDismissRequest = { appLanguageDialogShown = false },
                    title = stringResource(R.string.language),
                    centerTitle = true
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        settingsRadioItems(
                            languages.toList(),
                            languages.map { it.key }.indexOf(selectedLanguage),
                            { selectedLanguage = languages.keys.elementAt(it) }
                        ) { Text(stringResource(it.second)) }
                    }
                    DialogButtons(
                        cancelListener = {
                            appLanguageDialogShown = false
                            selectedLanguage = language
                        },
                        okListener = {
                            appLanguageDialogShown = false
                            scope.launch {
                                dataStore.saveSettings(language = selectedLanguage)
                            }
                            context.findActivity()?.runOnUiThread {
                                Log.i("Default device language : ", LocaleManagerCompat.getSystemLocales(context)[0]!!.language)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                    context.getSystemService(LocaleManager::class.java)
                                        .applicationLocales = LocaleList.forLanguageTags(
                                        if (selectedLanguage == "system") LocaleManagerCompat.getSystemLocales(context)[0]!!.language
                                        else selectedLanguage
                                    )
                                else
                                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selectedLanguage))
                            }
                        }
                    )
                }
            }
        }
    }
}