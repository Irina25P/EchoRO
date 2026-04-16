package com.example.echoro.ui.screens.landingpage

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.NavyBlue

@Composable
fun LandingButton(text: String, icon: ImageVector?, isOutlined: Boolean, onClick: () -> Unit) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }
    }
}
