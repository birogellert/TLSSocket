package com.birogellert.tlssocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.network.tls.tls
import io.ktor.utils.io.writeAvailable
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(modifier = Modifier.align(Alignment.Center), onClick = { connectCloseAndWrite() }) {
                    Text(text = "Crash me")
                }
            }
        }
    }

    private fun connectCloseAndWrite() {
        CoroutineScope(Dispatchers.IO).launch {

            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(ADDRESS, PORT)
                .tls(coroutineContext = Dispatchers.IO, trustManager = trustManager, randomAlgorithm = "SHA1PRNG")

            val output = socket.openWriteChannel(autoFlush = true)

            //To mitigate the disconnection, in the real scenario the server gets unavailable
            socket.dispose()

            //This is crashing. Regardless of the exception handler or any try catch, I cannot catch it
            output.writeAvailable(ByteArray(10))
        }
    }


    companion object {
        //TODO use your own address and port
        const val ADDRESS = ""
        const val PORT = 0
    }
}

