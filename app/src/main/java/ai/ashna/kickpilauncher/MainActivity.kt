package ai.ashna.kickpilauncher

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090B16)
private val Panel = Color(0xFF171A2B)
private val Purple = Color(0xFF8B5CF6)
private val Pink = Color(0xFFEC4899)
private val Cyan = Color(0xFF22D3EE)

data class AppItem(val name: String, val category: String, val symbol: String, val colors: List<Color>)

private val apps = listOf(
    AppItem("Stream", "Streaming", "▶", listOf(Pink, Purple)),
    AppItem("Cinema", "Streaming", "C", listOf(Color(0xFFF97316), Pink)),
    AppItem("Arcade", "Games", "✦", listOf(Purple, Cyan)),
    AppItem("Racing", "Games", "R", listOf(Color(0xFFEF4444), Color(0xFFF59E0B))),
    AppItem("Sports", "Sports", "S", listOf(Color(0xFF10B981), Cyan)),
    AppItem("Weather", "Tools", "☼", listOf(Cyan, Color(0xFF3B82F6))),
    AppItem("Files", "Tools", "▣", listOf(Color(0xFFF59E0B), Color(0xFFF97316))),
    AppItem("Kids", "Kids", "★", listOf(Color(0xFF34D399), Color(0xFFA3E635)))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent { KickPiTheme { Launcher() } }
    }
}

@Composable
fun Launcher() {
    val context = LocalContext.current
    var category by remember { mutableStateOf("Favorites") }
    var layout by remember { mutableStateOf("Square") }
    var showSettings by remember { mutableStateOf(false) }
    var showPin by remember { mutableStateOf(false) }
    val visible = if (category == "Favorites") apps.take(6) else apps.filter { it.category == category }

    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Bg, Color(0xFF11152A), Color(0xFF24143D))))) {
        Column(Modifier.fillMaxSize().padding(34.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Good evening", color = Color.White.copy(alpha = .65f), fontSize = 18.sp)
                    Text("KickPi", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                }
                Text("21:48", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(22.dp))
                Text("Wi‑Fi  •  72%", color = Color.White.copy(alpha = .7f), fontSize = 15.sp)
                Spacer(Modifier.width(22.dp))
                Button(onClick = { showSettings = true }, colors = ButtonDefaults.buttonColors(containerColor = Panel)) { Text("⚙ Settings") }
            }
            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxSize()) {
                CategoryRail(category) { category = it }
                Spacer(Modifier.width(28.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (category == "Favorites") "Your apps" else category, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(if (layout == "Compact") 5 else 4), horizontalArrangement = Arrangement.spacedBy(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        items(visible) { item -> AppTile(item, layout) { if (item.category == "Kids") showPin = true else launch(context, item.name) } }
                    }
                    Spacer(Modifier.height(22.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Square", "Wide", "Compact").forEach { mode ->
                            FilterChip(selected = layout == mode, onClick = { layout = mode }, label = { Text("$mode layout") })
                        }
                    }
                }
            }
        }
        if (showSettings) SettingsPanel(onClose = { showSettings = false })
        if (showPin) PinDialog(onClose = { showPin = false })
    }
}

@Composable
fun CategoryRail(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf("Favorites", "Streaming", "Games", "Sports", "Tools", "Misc", "Kids")
    Column(Modifier.width(150.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        categories.forEach { category ->
            Text(category, color = if (selected == category) Color.White else Color.White.copy(alpha = .55f), fontSize = 16.sp, modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(if (selected == category) Purple.copy(alpha = .65f) else Color.Transparent).clickable { onSelect(category) }.padding(horizontal = 16.dp, vertical = 12.dp))
        }
    }
}

@Composable
fun AppTile(item: AppItem, layout: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val height = if (layout == "Wide") 118.dp else 145.dp
    Column(Modifier.height(height).clip(RoundedCornerShape(23.dp)).background(Brush.linearGradient(item.colors)).border(if (focused) 3.dp else 0.dp, Color.White, RoundedCornerShape(23.dp)).onFocusChanged { focused = it.isFocused }.focusable().clickable { onClick() }.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Text(item.symbol, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(item.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            if (item.category == "Kids") Text("🔒", fontSize = 15.sp)
        }
    }
}

@Composable
fun SettingsPanel(onClose: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .55f)), contentAlignment = Alignment.CenterEnd) {
        Column(Modifier.fillMaxHeight().width(430.dp).background(Panel).padding(30.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Launcher settings", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("✕", color = Color.White, modifier = Modifier.clickable { onClose() })
            }
            Text("Personalize", color = Cyan, fontWeight = FontWeight.Bold)
            SettingRow("Wallpaper", "Aurora gradient  •  Change")
            SettingRow("Layout", "Square tiles  •  Switch")
            Text("Protection", color = Pink, fontWeight = FontWeight.Bold)
            SettingRow("Launcher PIN", "Enabled  •  Change code")
            SettingRow("Auto-lock", "15 minutes  •  Change")
            SettingRow("Kids apps", "4 protected apps")
            Text("System settings", color = Color.White, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = .08f)).clickable { onClose() }.padding(16.dp))
            Spacer(Modifier.weight(1f))
            Text("KickPi KP1  •  Android 14", color = Color.White.copy(alpha = .45f), fontSize = 13.sp)
        }
    }
}

@Composable
fun SettingRow(title: String, value: String) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .06f)).padding(15.dp)) {
        Text(title, color = Color.White, fontSize = 16.sp)
        Text(value, color = Color.White.copy(alpha = .55f), fontSize = 13.sp)
    }
}

@Composable
fun PinDialog(onClose: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .65f)), contentAlignment = Alignment.Center) {
        Column(Modifier.width(350.dp).clip(RoundedCornerShape(24.dp)).background(Panel).padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(15.dp)) {
            Text("Kids mode locked", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text("Enter your launcher PIN to continue", color = Color.White.copy(alpha = .6f))
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("PIN") })
            Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Unlock") }
            TextButton(onClick = onClose) { Text("Cancel") }
        }
    }
}

private fun launch(context: Context, label: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    if (intent != null) context.startActivity(intent)
}

@Composable
fun KickPiTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(primary = Purple, secondary = Cyan, background = Bg), content = content)
}
