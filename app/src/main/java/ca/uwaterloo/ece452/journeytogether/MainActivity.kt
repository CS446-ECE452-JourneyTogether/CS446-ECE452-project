package ca.uwaterloo.ece452.journeytogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uwaterloo.ece452.journeytogether.ui.theme.JourneyTogetherTheme

class MainActivity : ComponentActivity() {
    // Android's equivalent of main() function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JourneyTogetherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

// Composable annotation allows a function to be put into setContent{...} in onCreate(...)
// Note that Composable functions do not return anything!
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(color = Color.Green) {
        Text(
            text = "Hello $name!",
            modifier = modifier.padding(25.dp)
        )
    }
}

// lets you preview what the element looks like in Android Studio
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JourneyTogetherTheme {
        Greeting("JourneyTogether")
    }
}