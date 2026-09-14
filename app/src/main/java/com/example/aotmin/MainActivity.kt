package com.example.aotmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) { AotScreen() }
            }
        }
    }
}

@Composable
private fun AotScreen() {
    var taps by remember { mutableIntStateOf(0) }
    var facts by remember { mutableStateOf<List<Fact>>(emptyList()) }

    LaunchedEffect(Unit) {
        facts = loadFacts()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Stable AOT: R8 + D8", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { taps++ }) { Text("Tapped $taps") }
        for (fact in facts) {
            Text("• ${fact.title}", style = MaterialTheme.typography.titleSmall)
            Text(
                "  ${fact.detail}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
