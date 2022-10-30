package cuisine.de.lapin.simpleblockchain

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.gson.GsonBuilder
import cuisine.de.lapin.library.blockchain.model.Block
import cuisine.de.lapin.simpleblockchain.model.BabyEventType
import cuisine.de.lapin.simpleblockchain.ui.theme.SimpleBlockChainTheme
import cuisine.de.lapin.simpleblockchain.utils.toLongTimestamp
import cuisine.de.lapin.simpleblockchain.viewmodel.BlockViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: BlockViewModel by viewModels()
        setContent {
            SimpleBlockChainTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    showBlockChain(viewModel)
                    viewModel.loadBlocks()
                }
            }
        }
    }
}

@Composable
fun showBlockChain(viewModel: BlockViewModel) {
    Column {
        InputBlock(viewModel = viewModel)
        BlockList(viewModel = viewModel)
    }
}

@Composable
fun InputBlock(viewModel: BlockViewModel) {
    var comment by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val types = BabyEventType.values()
    var selectedType by remember { mutableStateOf(BabyEventType.MEAL) }

    val defaultDate = Instant.ofEpochMilli(System.currentTimeMillis())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    var date by remember { mutableStateOf(defaultDate) }
    val focusManager = LocalFocusManager.current

    Row {
        TextField(
            value = amount,
            onValueChange = { value -> amount = value },
            label = {
                Text(
                    text = stringResource(id = R.string.block_amount),
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(FocusDirection.Right)
                }),
            modifier = Modifier.weight(2f)
        )

        TextField(
            value = comment,
            onValueChange = { value -> comment = value },
            label = {
                Text(
                    text = stringResource(id = R.string.block_comment),
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.addBlock(selectedType, date.toLongTimestamp(), amount.toDouble(), comment)
                    focusManager.clearFocus()
                    comment = ""
                }),
            modifier = Modifier.weight(2f)
        )

        Column {
            Button(onClick = { expanded = !expanded }) {
                Text(selectedType.name)
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                types.forEach { type ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        selectedType = type
                    }) {
                        Text(text = type.name)
                    }
                }
            }
        }
    }
    Row {
        ShowDatePicker(date = date) {
            date = it
        }
        ShowTimePicker(date = date) {
            date = it
        }
    }

    Button(onClick = {
        focusManager.clearFocus()
        viewModel.addBlock(selectedType, date.toLongTimestamp(), amount.toDouble(), comment)
        comment = ""
    }) {
        Text(stringResource(R.string.create_block))
    }
}

@Composable
fun ShowTimePicker(date: LocalDateTime, onValueChanged: (LocalDateTime) -> Unit) {
    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hour: Int, minute: Int ->
            onValueChanged(
                date
                    .withHour(hour)
                    .withMinute(minute)
            )
        }, date.hour, date.minute, true
    )
    Button(onClick = {
        timePickerDialog.show()
    }) {
        HourText(date)
    }
}

@Composable
fun HourText(date: LocalDateTime) {
    val format: DateTimeFormatter =
        DateTimeFormatter.ofPattern(stringResource(id = R.string.hour_format))
    Text(text = date.format(format))
}

@Composable
fun ShowDatePicker(date: LocalDateTime, onValueChanged: (LocalDateTime) -> Unit) {
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year: Int, month: Int, day: Int ->
            onValueChanged(
                date.withMonth(month)
                    .withDayOfMonth(day)
                    .withYear(year)
            )

        }, date.year, date.month.value, date.dayOfMonth
    )

    Button(onClick = {
        datePickerDialog.show()
    }) {
        DateText(date)
    }
}

@Composable
fun DateText(date: LocalDateTime) {
    val format: DateTimeFormatter =
        DateTimeFormatter.ofPattern(stringResource(id = R.string.date_format))
    Text(text = date.format(format))
}

@Composable
fun BlockList(viewModel: BlockViewModel) {
    val blocks by viewModel.blockChain.observeAsState()
    val listState = rememberLazyListState()
    val gson = GsonBuilder().setPrettyPrinting().create()
    val showDialog = remember { mutableStateOf(false)  }
    var currentHash by remember { mutableStateOf("") }

    blocks?.let {
        LazyColumn(state = listState) {
            itemsIndexed(it) { _, item ->
                Row(modifier = Modifier.clickable {
                    showDialog.value = true
                    currentHash = item.hash
                }) {
                    Text(
                        text = gson.toJson(item),
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }

            }
        }
    }

    ShowDialog(showDialog, viewModel.getBlock(currentHash))

    LaunchedEffect(blocks) {
        listState.scrollToItem(blocks?.size ?: 0)
    }
}

@Composable
fun ShowDialog(openDialog: MutableState<Boolean>, block: Block? = null) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Sample Dialog")
            },
            text = {
                Text(block.toString())
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text("Close")
                }
            }
        )
    }
}