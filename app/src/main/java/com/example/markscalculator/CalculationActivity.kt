package com.example.markscalculator

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.markscalculator.ui.theme.AppTheme
import java.math.RoundingMode

class CalculationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CalculationView()
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationView() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val calculation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.findActivity()?.intent?.getSerializableExtra(
            "CalculationObject",
            Calculation::class.java
        )!!
    } else {
        @Suppress("DEPRECATION") context.findActivity()?.intent?.getSerializableExtra("CalculationObject")!! as Calculation
    }
    val calculationIndex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.findActivity()?.intent?.getSerializableExtra(
            "CalculationObjectIndex",
            java.lang.Integer::class.java
        )!!.toInt()
    } else {
        @Suppress("DEPRECATION") context.findActivity()?.intent?.getSerializableExtra("CalculationObjectIndex")!! as Int
    }
    var result by remember { mutableDoubleStateOf(0.0) }
    var total by remember { mutableDoubleStateOf(0.0) }
    var factors by remember { mutableDoubleStateOf(0.0) }
    val subjects = calculation.subjects
    val data = HashMap<String, HashMap<String, MutableState<String>>>()
    val marks = HashMap<String, MutableState<Double>>()
    val density = LocalDensity.current
    //val statusBarHeight = with(density) { WindowInsets.systemBars.getTop(this).toDp() }
    val navigationBarHeight = with(density) { WindowInsets.systemBars.getBottom(this).toDp() }
    for (item in subjects) {
        marks[item] = remember { mutableDoubleStateOf(0.0) }
    }
    val dataStore = Calculations(context)
    var sportChecked by remember { mutableStateOf(calculation.sport) }
    var artChecked by remember { mutableStateOf(calculation.art) }
    var tamazightChecked by remember { mutableStateOf(calculation.tamazight) }
    var qrCodeAlertShown by remember { mutableStateOf(false) }
    for (item in subjects) {
        val subItem = HashMap<String, MutableState<String>>()
        if ((calculation.level == "bem") or (calculation.level.startsWith("bac"))) {
            subItem["Mark"] = remember { mutableStateOf(if (calculation.data[item]!!["Mark"]!! == 0.0) "" else calculation.data[item]!!["Mark"]!!.toString()) }
            LaunchedEffect(subItem["Mark"]!!.value, sportChecked, artChecked, tamazightChecked) {
                calculation.data[item]!!["Mark"] = try { subItem["Mark"]!!.value.toDouble() } catch (e: Exception) { 0.0 }
                calculation.sport = sportChecked
                calculation.art = artChecked
                calculation.tamazight = tamazightChecked
                result = calculation.calculate()
                factors = calculation.factors
                total = calculation.total
                dataStore.replaceItem(calculationIndex, calculation)
            }
        } else {
            subItem["First Test"] = remember { mutableStateOf(if (calculation.data[item]!!["First Test"]!! == 0.0) "" else calculation.data[item]!!["First Test"]!!.toString()) }
            subItem["Second Test"] = remember { mutableStateOf(if (calculation.data[item]!!["Second Test"]!! == 0.0) "" else calculation.data[item]!!["Second Test"]!!.toString()) }
            subItem["Exam"] = remember { mutableStateOf(if (calculation.data[item]!!["Exam"]!! == 0.0) "" else calculation.data[item]!!["Exam"]!!.toString()) }
            subItem["CC"] = remember { mutableStateOf(if (calculation.data[item]!!["CC"]!! == 0.0) "" else calculation.data[item]!!["CC"]!!.toString()) }
            LaunchedEffect(subItem["First Test"]!!.value, subItem["Second Test"]!!.value, subItem["Exam"]!!.value, subItem["CC"]!!.value, sportChecked, artChecked, tamazightChecked) {
                calculation.data[item]!!["First Test"] = try { subItem["First Test"]!!.value.toDouble() } catch (e: Exception) { 0.0 }
                calculation.data[item]!!["Second Test"] = try { subItem["Second Test"]!!.value.toDouble() } catch (e: Exception) { 0.0 }
                calculation.data[item]!!["Exam"] = try { subItem["Exam"]!!.value.toDouble() } catch (e: Exception) { 0.0 }
                calculation.data[item]!!["CC"] = try { subItem["CC"]!!.value.toDouble() } catch (e: Exception) { 0.0 }
                calculation.sport = sportChecked
                calculation.art = artChecked
                calculation.tamazight = tamazightChecked
                result = calculation.calculate()
                factors = calculation.factors
                total = calculation.total
                marks[item]!!.value = calculation.data[item]!!["Mark"]!!
                dataStore.replaceItem(calculationIndex, calculation)
            }
        }
        data[item] = subItem
    }
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
    val subjectsTranslated = listOf(
        stringResource(R.string.arabic),
        stringResource(R.string.math),
        stringResource(R.string.french),
        stringResource(R.string.english),
        stringResource(R.string.physics),
        stringResource(R.string.sciences),
        stringResource(R.string.hist_geo),
        stringResource(R.string.islamic),
        stringResource(R.string.philosophy),
        stringResource(R.string.art),
        stringResource(R.string.tamazight),
        stringResource(R.string.sport),
        stringResource(R.string.technology),
        stringResource(R.string.informatic),
        stringResource(R.string.economy),
        stringResource(R.string.law),
        stringResource(R.string.management),
        stringResource(R.string._3rd_language),
    )
    fun levelStringSplit(level: String): List<String> {
        with(listOf("ap", "am", "as").indexOf(level.substring(1..2))) {
            return if (level.startsWith("bac") or level.startsWith("bem"))
                listOf(examLevels[listOf("bem", "bac").indexOf(level.substring(0..2))],
                        secondaryBranches[listOf("sci", "math", "math_tech", "mng", "lang", "philo").indexOf(level.substring(4))])
            else
                (mutableListOf("${levels[level.first().digitToInt() - 1]} ${types[this]}")).apply {
                    if (this@with == 2) {
                        add(
                            if (level.first().digitToInt() == 1)
                                branches[listOf("sci", "lit").indexOf(level.substring(4))]
                            else
                                secondaryBranches[listOf(
                                    "sci",
                                    "math",
                                    "math_tech",
                                    "mng",
                                    "lang",
                                    "philo"
                                ).indexOf(level.substring(4))]
                        )
                    }
                }
        }
    }
    AppTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()) {
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Row {
                                    Text(if (calculation.name == "") stringResource(R.string.untitled) else calculation.name)
                                    Spacer(Modifier.width(4.dp))
                                    for (item in levelStringSplit(calculation.level)) {
                                        Spacer(Modifier.width(4.dp))
                                        Badge(containerColor = colorScheme.primaryContainer) {
                                            Text(item, modifier = Modifier.padding(2.dp))
                                        }
                                    }
                                }
                            },
                            actions = {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip{ Text("Share") }
                                    },
                                    state = rememberTooltipState()
                                ) {
                                    IconButton(
                                        onClick = {
                                            for (item in subjects) {
                                                data[item]!!["First Test"]!!.value = ""
                                                data[item]!!["Second Test"]!!.value = ""
                                                data[item]!!["CC"]!!.value = ""
                                                data[item]!!["Exam"]!!.value = ""
                                            }
                                        }) {
                                        Icon(Icons.Rounded.Refresh, null)
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { (context as Activity).finish() }) {
                                    Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item { Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = if ((calculation.level == "bem") or (calculation.level.startsWith("bac"))) Modifier
                                    .padding(start = 4.dp)
                                    .fillMaxWidth()
                                    .weight(.5f)
                                else Modifier
                                    .padding(start = 4.dp)
                                    .width(IntrinsicSize.Max)
                            ) {
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .height(60.dp)
                                        .background(
                                            colorScheme.inverseOnSurface,
                                            RoundedCornerShape(topStart = 16.dp)
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(R.string.subject),
                                        style = typography.titleMedium,
                                        textAlign = TextAlign.Center,
                                        color = colorScheme.onBackground, modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                                Spacer(Modifier.height(4.dp))
                                for (item in subjects) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                colorScheme.inverseOnSurface,
                                                RoundedCornerShape(
                                                    bottomStart = if (item == "Sport") 16.dp else 0.dp
                                                )
                                            )
                                            .height(60.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        when (item) {
                                            "Art" -> {
                                                Checkbox(
                                                    checked = artChecked,
                                                    onCheckedChange = { checked ->
                                                        artChecked = checked
                                                    })
                                                Text(
                                                    text = stringResource(R.string.art),
                                                    color = colorScheme.onBackground
                                                )
                                                Spacer(Modifier.width(4.dp))
                                            }
                                            "Tamazight" -> {
                                                Checkbox(
                                                    checked = tamazightChecked,
                                                    onCheckedChange = { checked ->
                                                        tamazightChecked = checked
                                                    })
                                                Text(
                                                    text = stringResource(R.string.tamazight),
                                                    color = colorScheme.onBackground
                                                )
                                                Spacer(Modifier.width(4.dp))
                                            }
                                            "Sport" -> {
                                                Checkbox(
                                                    checked = sportChecked,
                                                    onCheckedChange = { checked ->
                                                        sportChecked = checked
                                                    })
                                                Text(
                                                    text = stringResource(R.string.sport),
                                                    color = colorScheme.onBackground
                                                )
                                                Spacer(Modifier.width(4.dp))
                                            }
                                            else -> {
                                                Text(
                                                    text = subjectsTranslated[
                                                        listOf(
                                                            "Arabic",
                                                            "Math",
                                                            "French",
                                                            "English",
                                                            "Physics",
                                                            "Sciences",
                                                            "Hist. Geo",
                                                            "Islamic",
                                                            "Philosophy",
                                                            "Art",
                                                            "Tamazight",
                                                            "Sport",
                                                            "Technology",
                                                            "Informatic",
                                                            "Economy",
                                                            "Law",
                                                            "Management",
                                                            "3rd language"
                                                        )
                                                            .indexOf(item)],
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center,
                                                    color = colorScheme.onBackground
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(
                                            colorScheme.inverseOnSurface,
                                            RoundedCornerShape(topEnd = 16.dp)
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if ((calculation.level.startsWith("bac")) or (calculation.level == "bem")) {
                                        Text(
                                            text = stringResource(R.string.mark),
                                            style = typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = colorScheme.onBackground, modifier = Modifier
                                                .weight(1f)
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(R.string.test),
                                            style = typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = colorScheme.onBackground, modifier = Modifier
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.exam),
                                            style = typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            color = colorScheme.onBackground, modifier = Modifier
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.cont_ctrl),
                                            style = typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            color = colorScheme.onBackground,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                        Text(
                                            text = stringResource(R.string.mark),
                                            style = typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            color = colorScheme.onBackground, modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                for (item in subjects) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                colorScheme.inverseOnSurface,
                                                RoundedCornerShape(
                                                    bottomEnd = if (item == "Sport") 16.dp else 0.dp
                                                )
                                            )
                                            .height(60.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        if ((calculation.level.startsWith("bac")) or (calculation.level == "bem")) {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                value = data[item]!!["Mark"]!!.value,
                                                singleLine = true,
                                                onValueChange = { value: String ->
                                                    data[item]!!["Mark"]!!.value =
                                                        if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } > 20
                                                        ) "20"
                                                        else if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } < 0
                                                        ) "0"
                                                        else value                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                        }
                                        else {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                value = data[item]!!["First Test"]!!.value,
                                                singleLine = true,
                                                onValueChange = { value: String ->
                                                    data[item]!!["First Test"]!!.value =
                                                        if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } > 20
                                                        ) "20"
                                                        else if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } < 0
                                                        ) "0"
                                                        else value
                                                    data[item]!!["Second Test"]!!.value =
                                                        if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } > 20
                                                        ) "20"
                                                        else if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } < 0
                                                        ) "0"
                                                        else value
                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                value = data[item]!!["Exam"]!!.value,
                                                singleLine = true,
                                                onValueChange = { value: String ->
                                                    data[item]!!["Exam"]!!.value =
                                                        if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } > 20
                                                        ) "20"
                                                        else if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } < 0
                                                        ) "0"
                                                        else value
                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                value = data[item]!!["CC"]!!.value,
                                                singleLine = true,
                                                onValueChange = { value: String ->
                                                    data[item]!!["CC"]!!.value =
                                                        if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } > 20
                                                        ) "20"
                                                        else if (try {
                                                                value.toDouble()
                                                            } catch (e: Exception) {
                                                                0.0
                                                            } < 0
                                                        ) "0"
                                                        else value
                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(4.dp)
                                                    .weight(1f),
                                                text = String.format("%.2f", marks[item]!!.value),
                                                textAlign = TextAlign.Center,
                                                color = colorScheme.onSurface
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorScheme.primary
                                .copy(alpha = .08f)
                                .compositeOver(colorScheme.surface.copy())
                        )
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.width(16.dp))
                        Card(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                                contentColor = colorScheme.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.total_mark),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = total.toBigDecimal().setScale(2, RoundingMode.FLOOR).toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Card(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.primaryContainer,
                                contentColor = colorScheme.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.final_mark),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = result.toBigDecimal().setScale(2, RoundingMode.FLOOR).toString(),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Card(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent,
                                contentColor = colorScheme.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.total_factors),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = factors.toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                    Spacer(modifier = Modifier.height(navigationBarHeight))
                }
            }
            if(qrCodeAlertShown) {
                AlertDialog(
                    onDismissRequest = { qrCodeAlertShown = false },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .width(IntrinsicSize.Max)
                            ) {
                                Box(modifier = Modifier.padding(2.dp)) {
                                    Image(
                                        bitmap = calculation.generateQrCode(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(id = R.string.qr_code_alert_description))
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { qrCodeAlertShown = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CalculationView()
}