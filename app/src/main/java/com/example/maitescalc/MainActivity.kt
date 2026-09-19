package com.example.maitescalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.maitescalc.ui.navigation.MainNavigation
import com.example.maitescalc.ui.theme.MaitesCalcTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MaitesCalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isSignedIn by remember {
                        mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                    }

                    MainNavigation(
                        isSignedIn = isSignedIn,
                        onSignedIn = { isSignedIn = true },
                        onSignOut = { isSignedIn = false }
                    )
                }
            }
        }
    }
}
