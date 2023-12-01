package com.example.markscalculator.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import com.example.markscalculator.BarcodeScanner
import com.example.markscalculator.Calculation
import com.example.markscalculator.CalculationActivity
import com.example.markscalculator.CalculationData
import com.example.markscalculator.Calculations
import com.example.markscalculator.R
import com.example.markscalculator.components.Dialog
import com.example.markscalculator.components.DialogButtons
import com.example.markscalculator.ui.theme.AppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.math.RoundingMode
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            App()
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

@Composable
fun App() {
    var visible by remember { mutableStateOf(false) }
    val scrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(scrollState)
    val context = LocalContext.current
    val density = LocalDensity.current
    val dataStore = Calculations(context)
    val calculations = dataStore.calculations.collectAsState(initial = mutableListOf()).value
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var clearListAlertShown by remember { mutableStateOf(false) }
    var qrCodeAlertShown by remember { mutableStateOf(false) }
    var renameAlertShown by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val navigationBarHeight = with(density) { WindowInsets.systemBars.getBottom(this).toDp() }
    BackHandler(enabled = visible) {
        visible = false
    }
    val scope = rememberCoroutineScope()
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var appNameVisible by remember { mutableStateOf(true) }
    var placeholderVisible by remember { mutableStateOf(false) }

    val types = listOf(
        stringResource(id = R.string.primary),
        stringResource(id = R.string.middle),
        stringResource(id = R.string.secondary),
        stringResource(id = R.string.exam)
    )
    val levels = listOf(
        stringResource(id = R.string.first_year),
        stringResource(id = R.string.second_year),
        stringResource(id = R.string.third_year),
        stringResource(id = R.string.fourth_year),
        stringResource(id = R.string.fifth_year)
    )
    val branches = listOf(
        stringResource(id = R.string.scientific),
        stringResource(id = R.string.literature),
    )
    val secondaryBranches = listOf(
        stringResource(id = R.string.scientific),
        stringResource(id = R.string.mathematics),
        stringResource(id = R.string.math_tech),
        stringResource(id = R.string.management),
        stringResource(id = R.string.foreign_languages),
        stringResource(id = R.string.philosophy),
    )
    val examLevels = listOf(
        stringResource(R.string.bem),
        stringResource(R.string.bachelor)
    )
    var primaryLevelValue by remember { mutableIntStateOf(0) }
    var middleLevelValue by remember { mutableIntStateOf(0) }
    var secondaryLevelValue by remember { mutableIntStateOf(0) }
    var secondaryLevelBranchValue by remember { mutableIntStateOf(0) }
    var secondaryLevelSecondBranchValue by remember { mutableIntStateOf(0) }
    var examValue by remember { mutableIntStateOf(0) }
    var examBranchValue by remember { mutableIntStateOf(0) }
    val searchBarPadding = animateDpAsState(targetValue = if (searchActive) 0.dp else 8.dp, label = "Searchbar padding").value
    val navIconAngle = animateFloatAsState(if (searchActive) -360f else 0f, label = "Searchbar icons")
    val clipboardManager = LocalClipboardManager.current
    fun levelString(level: String): String {
        with(listOf("ap", "am", "as").indexOf(level.substring(1..2))) {
            return if (level.startsWith("bac") or level.startsWith("bem"))
                examLevels[listOf("bem", "bac").indexOf(level.substring(0..2))] + " - " +
                        secondaryBranches[listOf("sci", "math", "math_tech", "mng", "lang", "philo").indexOf(level.substring(4))]
            else
                "${levels[level.first().digitToInt() - 1]} ${types[this]}" +
                        if (this == 2) {
                            " - " +
                                    if (level.first().digitToInt() == 1)
                                        branches[listOf("sci", "lit").indexOf(level.substring(4))]
                                    else
                                        secondaryBranches[listOf("sci", "math", "math_tech", "mng", "lang", "philo").indexOf(level.substring(4))]
                        } else ""
        }
    }
    val searchResult = calculations.filter {
        it.name.contains(searchQuery, ignoreCase = true) or
                it.result.toString().contains(searchQuery, ignoreCase = true) or
                levelString(it.level).contains(searchQuery, ignoreCase = true) or
                searchQuery.contains(it.name, ignoreCase = true) or
                searchQuery.contains(it.result.toString(), ignoreCase = true) or
                searchQuery.contains(levelString(it.level), ignoreCase = true)
    }
    val statusBarHeight = with(density) { WindowInsets.systemBars.getTop(this).toDp() }
    LaunchedEffect(true) {
        delay(750)
        appNameVisible = false
        delay(250)
        placeholderVisible = true
    }
    LaunchedEffect(searchActive) {
        if (!searchActive) searchQuery = ""
    }
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    AnimatedContent(
                        targetState = placeholderVisible,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "Searchbar animation"
                    ) {
                        if (!it)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp + statusBarHeight)
                                    .statusBarsPadding()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(R.string.title_first_string),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    stringResource(R.string.second_title_string),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        else
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { query -> searchQuery = query },
                                onSearch = { /*searchActive = false*/ },
                                active = searchActive,
                                onActiveChange = { active -> searchActive = active },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = searchBarPadding
                                    )
                                    .imePadding(),
                                placeholder = { Text(stringResource(R.string.search_by_name_level_or_mark)) },
                                leadingIcon = {
                                    IconButton(
                                        onClick = {
                                            searchActive = !searchActive
                                        },
                                        modifier = Modifier.rotate(navIconAngle.value)
                                    ) {
                                        AnimatedContent(
                                            targetState = searchActive,
                                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                                            label = ""
                                        ) { target ->
                                            if (target)
                                                Icon(
                                                    Icons.AutoMirrored.Default.ArrowBack,
                                                    null,
                                                )
                                            else
                                                Icon(
                                                    Icons.Default.Search,
                                                    null,
                                                )
                                        }
                                    }
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (searchActive)
                                                clipboardManager.getText().let { text ->
                                                    if (text != null) searchQuery = text.toString()
                                                }
                                            else if (searchQuery == "")
                                                menuExpanded = true
                                            else
                                                searchQuery = ""
                                        },
                                        modifier = Modifier.rotate(-navIconAngle.value)
                                    ) {
                                        AnimatedContent(
                                            targetState = searchActive,
                                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                                            label = ""
                                        ) { target ->
                                            if (target)
                                                AnimatedContent(targetState = searchQuery == "", label = "") { paste ->
                                                    if(paste)
                                                        Icon(
                                                            Icons.Default.ContentPaste,
                                                            null,
                                                        )
                                                    else
                                                        Icon(
                                                            Icons.Default.Clear,
                                                            null,
                                                        )
                                                }
                                            else
                                                Icon(
                                                    Icons.Default.MoreVert,
                                                    null,
                                                )
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        listOf(stringResource(R.string.clear_list), stringResource(R.string.settings)).forEachIndexed { i, s ->
                                            if ((i == 1) or (calculations.isNotEmpty()))
                                                DropdownMenuItem(
                                                    leadingIcon = {
                                                        Icon(
                                                            if(i == 0) Icons.Default.DeleteOutline else Icons.Default.Settings,
                                                            null
                                                        )
                                                    },
                                                    text = { Text(s) },
                                                    onClick = {
                                                        menuExpanded = false
                                                        if(i == 0)
                                                            clearListAlertShown = true
                                                        else
                                                            context.startActivity(
                                                                Intent(
                                                                    context,
                                                                    SettingsActivity::class.java
                                                                )
                                                            )
                                                    }
                                                )
                                        }
                                    }
                                }
                            ) {
                                AnimatedContent(targetState = searchQuery == "", label = "Search history animation") { emptyQuery ->
                                    if (emptyQuery)
                                        Box(Modifier.fillMaxSize()) {
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.Search, null, Modifier.size(120.dp))
                                                Spacer(Modifier.height(12.dp))
                                                Text(
                                                    text = stringResource(R.string.type_to_search),
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    else
                                        AnimatedContent(targetState = searchResult.isEmpty(), label = "Search result animation") { emptyList ->
                                            if (emptyList)
                                                Box(Modifier.fillMaxSize()) {
                                                    Column(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(Icons.Default.Search, null, Modifier.size(120.dp))
                                                        Spacer(Modifier.height(12.dp))
                                                        Text(
                                                            text = stringResource(R.string.no_result),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            else
                                                LazyColumn(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentPadding = PaddingValues(vertical = 16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    itemsIndexed(searchResult) { _, item ->
                                                        ListItem(
                                                            headlineContent = { Text(if (item.name == "") stringResource(R.string.untitled) else item.name) },
                                                            supportingContent = {
                                                                Column {
                                                                    Spacer(Modifier.height(6.dp))
                                                                    Text(levelString(item.level))
                                                                }
                                                            },
                                                            trailingContent = {
                                                                Text("${item.result.toBigDecimal().setScale(2, RoundingMode.FLOOR)}")
                                                            },
                                                            modifier = Modifier.clickable {
                                                                context.startActivity(
                                                                    Intent(
                                                                        context,
                                                                        CalculationActivity::class.java
                                                                    ).apply {
                                                                        putExtra(
                                                                            "CalculationObject",
                                                                            item
                                                                        )
                                                                        putExtra(
                                                                            "CalculationObjectIndex",
                                                                            calculations.indexOf(
                                                                                item
                                                                            )
                                                                        )
                                                                    }
                                                                )
                                                            }
                                                        )
                                                    }
                                                }
                                        }
                                }
                            }
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        !visible,
                        enter = fadeIn(animationSpec = tween()),
                        exit = fadeOut(animationSpec = tween())
                    ) {
                        Column {
                            val message = stringResource(R.string.sorry_this_qr_code_is_invalid)
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    scope.launch {
                                        BarcodeScanner(context).startScan { result ->
                                            if (result != null) {
                                                try {
                                                    with(
                                                        Gson().fromJson<CalculationData>(
                                                            result,
                                                            (object :
                                                                TypeToken<CalculationData>() {}).type
                                                        )
                                                    ) {
                                                        val newCalculation = Calculation(
                                                            this.level,
                                                            this.name,
                                                            this.sport,
                                                            this.art,
                                                            this.sport
                                                        )
                                                        newCalculation.updateData(this.data)
                                                        scope.launch {
                                                            val intent =
                                                                Intent(
                                                                    context,
                                                                    CalculationActivity::class.java
                                                                )
                                                            intent.putExtra(
                                                                "CalculationObject",
                                                                newCalculation
                                                            )
                                                            intent.putExtra(
                                                                "CalculationObjectIndex",
                                                                dataStore.calculations.first().count()
                                                            )
                                                            dataStore.addItem(newCalculation)
                                                            context.startActivity(intent)
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.QrCodeScanner, null)
                            }
                            Spacer(Modifier.height(12.dp))
                            FloatingActionButton(onClick = { visible = true }) {
                                Icon(Icons.Default.Add, null)
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedContent(
                        targetState = calculations.isNotEmpty(),
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "List animation"
                    ) {
                        if (it)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(calculations) { item ->
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp)
                                                ) {
                                                    OutlinedCard(
                                                        modifier = Modifier
                                                            .combinedClickable(
                                                                onClick = {
                                                                    if (selectedIndex == calculations.indexOf(
                                                                            item
                                                                        )
                                                                    )
                                                                        selectedIndex = -1
                                                                    else {
                                                                        context.startActivity(
                                                                            Intent(
                                                                                context,
                                                                                CalculationActivity::class.java
                                                                            ).apply {
                                                                                putExtra(
                                                                                    "CalculationObject",
                                                                                    item
                                                                                )
                                                                                putExtra(
                                                                                    "CalculationObjectIndex",
                                                                                    calculations.indexOf(
                                                                                        item
                                                                                    )
                                                                                )
                                                                            }
                                                                        )
                                                                    }
                                                                },
                                                                onLongClick = {
                                                                    selectedIndex =
                                                                        calculations.indexOf(
                                                                            item
                                                                        )
                                                                }
                                                            )
                                                            .fillMaxWidth(),
                                                        border =
                                                        if (selectedIndex == calculations.indexOf(
                                                                item
                                                            )
                                                        )
                                                            BorderStroke(
                                                                color = MaterialTheme.colorScheme.primary,
                                                                width = 2.dp
                                                            )
                                                        else
                                                            BorderStroke(
                                                                color = MaterialTheme.colorScheme.outline,
                                                                width = 1.dp
                                                            )
                                                    ) {
                                                        Spacer(Modifier.height(8.dp))
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(
                                                                    horizontal = 16.dp,
                                                                    vertical = 8.dp
                                                                ),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .weight(1f)
                                                            ) {
                                                                Text(
                                                                    if (item.name == "") stringResource(
                                                                        R.string.untitled
                                                                    ) else item.name,
                                                                    style = MaterialTheme.typography.titleLarge,
                                                                    color = MaterialTheme.colorScheme.onBackground
                                                                )
                                                                Spacer(Modifier.height(4.dp))
                                                                Text(
                                                                    "${stringResource(R.string.level)}: ${levelString(item.level)}",
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                                                        alpha = .7f
                                                                    )
                                                                )
                                                            }
                                                            Text(
                                                                String.format(
                                                                    "%.2f",
                                                                    item.result
                                                                ),
                                                                style = MaterialTheme.typography.titleLarge,
                                                                color = MaterialTheme.colorScheme.onBackground
                                                            )
                                                        }
                                                        Spacer(Modifier.height(4.dp))
                                                        AnimatedContent(
                                                            targetState = selectedIndex == calculations.indexOf(
                                                                item
                                                            ),
                                                            label = "Extra buttons animation"
                                                        ) { state ->
                                                            if (state) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceEvenly
                                                                ) {
                                                                    IconButton(onClick = {
                                                                        renameAlertShown = true
                                                                    }) {
                                                                        Icon(
                                                                            Icons.Default.Edit,
                                                                            null
                                                                        )
                                                                    }
                                                                    IconButton(onClick = {
                                                                        qrCodeAlertShown = true
                                                                    }) {
                                                                        Icon(
                                                                            Icons.Default.Share,
                                                                            null
                                                                        )
                                                                    }
                                                                    IconButton(onClick = {
                                                                        selectedIndex = -1
                                                                        scope.launch {
                                                                            dataStore.removeItem(
                                                                                calculations.indexOf(
                                                                                    item
                                                                                )
                                                                            )
                                                                        }
                                                                    }) {
                                                                        Icon(
                                                                            Icons.Default.Delete,
                                                                            null
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Spacer(Modifier.height(4.dp))
                                                    }
                                                }
                                    }
                                    item {
                                        Spacer(Modifier.height(navigationBarHeight))
                                    }
                                }
                            }
                        else
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.ic_app_icon),
                                    null,
                                    modifier = Modifier.size(128.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(32.dp))
                                Text(
                                    stringResource(R.string.all_saved_items_will_show_up_here),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            var calculationName by remember { mutableStateOf("") }
            var level by remember { mutableIntStateOf(0) }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.choose_level)) },
                        navigationIcon = {
                            IconButton(onClick = { visible = false }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    )
                },
                bottomBar = {
                    Column {
                        Row(modifier = Modifier.padding(16.dp)) {
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onClick = { visible = false }) {
                                Text(stringResource(R.string.back))
                            }
                            Spacer(Modifier.width(16.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                onClick = {
                                    val newCalculation = Calculation(
                                        when (level) {
                                            0 -> "${(primaryLevelValue + 1)}ap"
                                            1 -> "${(middleLevelValue + 1)}am"
                                            2 -> "${(secondaryLevelValue + 1)}as_" +
                                                    if (secondaryLevelValue == 0) when (secondaryLevelBranchValue) {
                                                        0 -> "sci"
                                                        else -> "lit"
                                                    }
                                                    else when (secondaryLevelBranchValue) {
                                                        0 -> when (secondaryLevelSecondBranchValue) {
                                                            0 -> "sci"
                                                            1 -> "math"
                                                            2 -> "math_tech"
                                                            else -> "mng"
                                                        }
                                                        else -> when (secondaryLevelSecondBranchValue) {
                                                            0 -> "lang"
                                                            else -> "philo"
                                                        }
                                                    }
                                            else ->
                                                if (examValue == 0) "bem"
                                                else "bac_" + when (examBranchValue) {
                                                    0 -> "sci"
                                                    1 -> "math"
                                                    2 -> "math_tech"
                                                    3 -> "mng"
                                                    4 -> "lang"
                                                    else -> "philo"
                                                }
                                        }, calculationName
                                    )
                                    scope.launch {
                                        val intent =
                                            Intent(context, CalculationActivity::class.java)
                                        intent.putExtra("CalculationObject", newCalculation)
                                        intent.putExtra(
                                            "CalculationObjectIndex",
                                            dataStore.calculations.first().count()
                                        )
                                        dataStore.addItem(newCalculation)
                                        context.startActivity(intent)
                                    }
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        visible = false
                                        selectedIndex = -1
                                    }, 300)
                                }) {
                                Text(stringResource(R.string.save))
                            }
                        }
                        Spacer(Modifier.height(navigationBarHeight))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = calculationName,
                                onValueChange = { value -> calculationName = value },
                                trailingIcon = {
                                    AnimatedVisibility(calculationName != "") {
                                        IconButton(onClick = { calculationName = "" }) {
                                            Icon(Icons.Default.Close, null)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(stringResource(R.string.level_placeholder)) },
                                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words)
                            )
                        }
                    }
                    items(types) { item ->
                        val index = types.indexOf(item)
                        var expanded by remember { mutableStateOf(false) }
                        var secondExpanded by remember { mutableStateOf(false) }
                        var thirdExpanded by remember { mutableStateOf(false) }
                        Column(Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .selectable(
                                        selected = level == index,
                                        onClick = { level = index }),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = level == index,
                                    onClick = { level = index },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(item)
                            }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { e -> expanded = e; if (e) level = index },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp, 8.dp)
                            ) {
                                CompositionLocalProvider(
                                    LocalTextInputService provides null
                                ) {
                                    TextField(
                                        value = when (index) {
                                            0 -> levels[primaryLevelValue]
                                            1 -> levels[middleLevelValue]
                                            2 -> levels[secondaryLevelValue]
                                            else -> examLevels[examValue]
                                        },
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                    )
                                }
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    when (index) {
                                        0 -> levels.subList(0, 5)
                                        1 -> levels.subList(0, 4)
                                        2 -> levels.subList(0, 3)
                                        else -> examLevels
                                    }.forEachIndexed { i, option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                expanded = false
                                                when (index) {
                                                    0 -> primaryLevelValue = i
                                                    1 -> middleLevelValue = i
                                                    2 -> secondaryLevelValue = i
                                                    else -> examValue = i
                                                }
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(visible = (index == 2) or ((index == 3) and (examValue == 1))) {
                                ExposedDropdownMenuBox(
                                    expanded = secondExpanded,
                                    onExpandedChange = { e ->
                                        secondExpanded = e; if (e) level = index
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp, 8.dp)
                                ) {
                                    CompositionLocalProvider(
                                        LocalTextInputService provides null
                                    ) {
                                        TextField(
                                            value = when (index) {
                                                2 -> branches[secondaryLevelBranchValue]
                                                else -> secondaryBranches[examBranchValue]
                                            },
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = secondExpanded
                                                )
                                            },
                                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                        )
                                    }
                                    ExposedDropdownMenu(
                                        expanded = secondExpanded,
                                        onDismissRequest = { secondExpanded = false }
                                    ) {
                                        when (index) {
                                            2 -> branches
                                            else -> secondaryBranches
                                        }.forEachIndexed { i, option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    secondExpanded = false
                                                    when (index) {
                                                        2 -> secondaryLevelBranchValue = i
                                                        3 -> examBranchValue = i
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(visible = (index == 2) and (secondaryLevelValue >= 1)) {
                                ExposedDropdownMenuBox(
                                    expanded = thirdExpanded,
                                    onExpandedChange = { e ->
                                        thirdExpanded = e; if (e) level = index
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp, 8.dp)
                                ) {
                                    CompositionLocalProvider(
                                        LocalTextInputService provides null
                                    ) {
                                        TextField(
                                            value = when (secondaryLevelBranchValue) {
                                                0 -> secondaryBranches.subList(
                                                    0,
                                                    4
                                                )[secondaryLevelSecondBranchValue]

                                                else -> secondaryBranches.subList(
                                                    4,
                                                    6
                                                )[secondaryLevelSecondBranchValue]
                                            },
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = thirdExpanded
                                                )
                                            },
                                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                        )
                                    }
                                    ExposedDropdownMenu(
                                        expanded = thirdExpanded,
                                        onDismissRequest = { thirdExpanded = false }
                                    ) {
                                        when (secondaryLevelBranchValue) {
                                            0 -> secondaryBranches.subList(0, 4)
                                            else -> secondaryBranches.subList(4, 6)
                                        }.forEachIndexed { i, option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    thirdExpanded = false
                                                    secondaryLevelSecondBranchValue = i
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Dialog(
            visible = clearListAlertShown,
            onDismissRequest = { clearListAlertShown = false },
            title = stringResource(R.string.clear_list)) {
            Text(stringResource(R.string.clear_list_message))
            DialogButtons(
                cancelListener = { clearListAlertShown = false },
                okListener = {
                    clearListAlertShown = false
                    scope.launch { dataStore.clearList() }
                }
            )
        }
        Dialog(
            visible = qrCodeAlertShown,
            onDismissRequest = { qrCodeAlertShown = false },
            title = null) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedIndex > -1 && selectedIndex < calculations.count()) {
                    Box(
                        Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .width(IntrinsicSize.Max)
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Image(
                                bitmap = calculations[selectedIndex].generateQrCode(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.qr_code_alert_description))
            }
            DialogButtons(
                cancelListener = {
                    val imagesFolder = File(context.cacheDir, "images")
                    val uri: Uri?
                    try {
                        imagesFolder.mkdirs()
                        val file = File(imagesFolder, "shared_image.png")
                        val stream = FileOutputStream(file)
                        calculations[selectedIndex].generateQrCode(backgroundColor = android.graphics.Color.WHITE)
                            .asAndroidBitmap()
                            .compress(Bitmap.CompressFormat.JPEG, 90, stream)
                        stream.flush()
                        stream.close()
                        uri = FileProvider.getUriForFile(
                            context,
                            "com.mydomain.fileprovider",
                            file
                        )
                        context.startActivity(
                            Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                type = "image/png"
                            }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                cancelText = stringResource(R.string.share),
                okListener = { qrCodeAlertShown = false }
            )
        }
        var newName by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }
        Dialog(
            visible = clearListAlertShown,
            onDismissRequest = { clearListAlertShown = false },
            title = stringResource(R.string.rename)) {
            OutlinedTextField(
                value = newName,
                onValueChange = { value -> newName = value; error = false },
                singleLine = true,
                label = { Text(stringResource(R.string.new_name)) },
                isError = error,
            )
            DialogButtons(
                cancelListener = { renameAlertShown = false },
                okListener = {
                    if (newName == "") error = true
                    else {
                        scope.launch { dataStore.renameItem(selectedIndex, newName) }
                        renameAlertShown = false
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App()
}