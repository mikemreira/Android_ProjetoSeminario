package pt.isel.projetoeseminario

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pt.isel.projetoeseminario.ui.home.HomeScreen
import pt.isel.projetoeseminario.ui.registos.RegistosScreen
import pt.isel.projetoeseminario.ui.theme.ProjetoESeminarioTheme
import pt.isel.projetoeseminario.ui.useroperations.login.LogInScreen
import pt.isel.projetoeseminario.ui.useroperations.navigationdrawer.MenuItem
import pt.isel.projetoeseminario.ui.useroperations.profile.PerfilScreen
import pt.isel.projetoeseminario.ui.useroperations.signup.SignUpScreen
import pt.isel.projetoeseminario.viewModels.RegistoViewModel
import pt.isel.projetoeseminario.viewModels.UserViewModel


class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    private fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                adapter.enableForegroundDispatch(
                    this, pendingIntent, nfcIntentFilter, null
                )
            }
        }
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { nfcIntent ->
            handleNfcIntent(nfcIntent)
        }
    }

    private fun handleNfcIntent(intent: Intent?) {
        Log.d("TAG_PROJETOSEMINARIO", "Entered Handle ${intent?.action}")
        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            // Get the NDEF messages from the intent
            Log.d("TAG_PROJETOSEMINARIO", "Inside IF")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                val tagId = tag.id
                val hexString = tagId.joinToString(separator = "") { byte -> "%02X".format(byte) }
                Log.d("TAG_PROJETOSEMINARIO", "NFC Tag ID detected: $hexString")
                Toast.makeText(this, "NFC Tag ID: $hexString", Toast.LENGTH_LONG).show()
            }
        } else if (intent != null && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.d("TAG_PROJETOSEMINARIO", "ENTERED ELSE.")

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    val ndefMessage = ndef.cachedNdefMessage
                    if (ndefMessage != null) {
                        for (record in ndefMessage.records) {
                            val payload = String(record.payload)
                            Log.d("TAG_PROJETOSEMINARIO", "NFC Tag detected: $payload")
                            Toast.makeText(this, "NFC Tag: $payload", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.d("TAG_PROJETOSEMINARIO", "NFC Tag detected, but not an NDEF tag.")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel: UserViewModel by viewModels()
        val registoViewModel: RegistoViewModel by viewModels()
        val sharedPreferences: SharedPreferences = application.getSharedPreferences("users", Context.MODE_PRIVATE)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.let { it }

        setContent {
            LaunchedEffect(Unit) {
                userViewModel.getUserDetails(sharedPreferences.getString("user_token", null)?: "")
                Log.d("TOKEN", sharedPreferences.getString("user_token", null) ?: "NULL")
            }

            ProjetoESeminarioTheme {
                val items = listOf(
                    MenuItem(
                        id = "home",
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    MenuItem(
                        id = "registos",
                        title = "Registos",
                        selectedIcon = Icons.Filled.FileCopy,
                        unselectedIcon = Icons.Outlined.FileCopy
                    ),
                    MenuItem(
                        id = "perfil",
                        title = "Perfil",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person
                    ),
                    MenuItem(
                        id = "logout",
                        title = "Log Out",
                        selectedIcon = Icons.AutoMirrored.Filled.Logout,
                        unselectedIcon = Icons.AutoMirrored.Outlined.Logout
                    )
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    var selectedItemIndex by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    val navController = rememberNavController()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStackEntry?.destination?.route

                    ModalNavigationDrawer(
                        drawerContent = {
                            ModalDrawerSheet {
                                items.forEachIndexed { index, item ->  
                                    Spacer(modifier = Modifier.height(16.dp))
                                    if (item.title == "Log Out") Spacer(modifier = Modifier.weight(1f))
                                    NavigationDrawerItem(
                                        label = { Text(text = item.title) },
                                        selected = index == selectedItemIndex,
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.id)
                                            if (item.title == "Log Out") userViewModel.logout()
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        },
                                        badge = {
                                            item.badgeCount?.let {
                                                Text(text = item.badgeCount.toString())
                                            }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        },
                        drawerState = drawerState
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("Acesso a Estaleiros") },
                                    navigationIcon = {
                                        if (currentRoute != "logout" && currentRoute != "signup") @Composable {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    drawerState.open()
                                                }
                                            }) {
                                                if (navController.currentDestination?.route != "logout")
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = null
                                                    )
                                            }
                                        } else @Composable {  }
                                    }
                                )
                            }
                        ) {
                            NavHost(navController = navController, startDestination = if (sharedPreferences.getString("user_token", null) == null) "logout" else "home", modifier = Modifier.padding(it)) {
                                composable("home") { HomeScreen(userViewModel, registoViewModel, sharedPreferences.getString("user_token", "") ?: "") {
                                    userViewModel.getUserDetails(sharedPreferences.getString("user_token", "") ?: "")
                                } }
                                composable("registos") {
                                    RegistosScreen(registoViewModel, sharedPreferences.getString("user_token", "") ?: "")
                                }
                                composable("perfil") {
                                    PerfilScreen(userViewModel, sharedPreferences.getString("user_token", "") ?: "")
                                }
                                composable("logout") { LogInScreen(registerNewUser = {
                                    navController.navigate("signup")
                                }, onLogIn = {
                                    navController.navigate("home")
                                }, userViewModel) }
                                composable("signup") { SignUpScreen(userViewModel) { navController.navigate("logout") } }
                            }
                        }
                    }
                }
            }
        }
    }
}
