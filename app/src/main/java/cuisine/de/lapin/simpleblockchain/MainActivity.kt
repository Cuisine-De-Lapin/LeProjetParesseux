package cuisine.de.lapin.simpleblockchain

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.gson.GsonBuilder
import cuisine.de.lapin.simpleblockchain.ui.theme.SimpleBlockChainTheme
import cuisine.de.lapin.simpleblockchain.viewmodel.BlockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: BlockViewModel by viewModels()
        viewModel.initBlockChain()
        setContent {
            SimpleBlockChainTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    showBlockChain(viewModel)
                }
            }
        }
    }
}

@Composable
fun showBlockChain(viewModel: BlockViewModel) {
    Column {
        Button(onClick = {
            viewModel.createBlock("${viewModel.blockChain.value?.size ?: 0}")
        }) {
            Text("Create Block")
        }
        showBlockList(viewModel = viewModel)
    }
}

@Composable
fun showBlockList(viewModel: BlockViewModel) {
    val blocks by viewModel.blockChain.observeAsState()
    val listState = rememberLazyListState()
    val gson = GsonBuilder().setPrettyPrinting().create()

    blocks?.let {
        LazyColumn(state = listState) {
            itemsIndexed(it) { _, item ->
                Row {
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

    LaunchedEffect(blocks) {
        listState.scrollToItem(blocks?.size ?: 0)
    }
}
