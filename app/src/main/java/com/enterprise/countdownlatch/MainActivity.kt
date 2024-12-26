package com.enterprise.countdownlatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.enterprise.countdownlatch.ui.theme.CountDownLatchTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountDownLatchTheme {

                CountDownLatchApp()

            }
        }
    }
}

@Composable
fun CountDownLatchApp() {

    val counter = rememberSaveable { mutableStateOf(5) }
    val resultText = rememberSaveable { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(color = Color.Green)){

        Scaffold(modifier = Modifier.systemBarsPadding().fillMaxSize()) { innerPadding ->

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
                    .background(color = Color.White)){


                Text(text = counter.value.toString())

                Text(text = resultText.value)

                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    onClick = {

                        handleLogic(resultText, counter)

                }) {

                    Text(text = stringResource(id = R.string.main_activity_start_button_text))

                }

            }

        }

    }

}


private fun handleLogic(
    resultText: MutableState<String>,
    counter: MutableState<Int>
) {
    GlobalScope.launch(Dispatchers.Main) {

        resultText.value = ""
        counter.value = 5


        val countDownLatchToWaitEndOfCounter = CountDownLatch(1)

        thread {

            GlobalScope.launch(Dispatchers.IO) {

                CountDown(
                    counter = counter,
                    countDownLatchToWaitEndOfCounter = countDownLatchToWaitEndOfCounter
                )

            }

            countDownLatchToWaitEndOfCounter.await()

            GlobalScope.launch(Dispatchers.Main) {

                resultText.value = "Counter Finished!"

            }

        }
    }
}

suspend fun CountDown(counter: MutableState<Int>, countDownLatchToWaitEndOfCounter: CountDownLatch) {

    delay(1000)

    while(counter.value > 0){

        GlobalScope.launch(Dispatchers.Main) {

            counter.value = counter.value - 1

        }

        delay(1000L)
    }

    countDownLatchToWaitEndOfCounter.countDown()

}