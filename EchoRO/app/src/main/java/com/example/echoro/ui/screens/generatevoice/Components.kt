package com.example.echoro.ui.screens.generatevoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echoro.ui.theme.DisabledText
import com.example.echoro.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoRoDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isEnabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && isEnabled,
        onExpandedChange = { if (isEnabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                if (!isEnabled) {
                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = DisabledText)
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = DisabledText,
                disabledBorderColor = Color.LightGray,
                disabledLabelColor = DisabledText
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = isEnabled,
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded && isEnabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EchoRoPrimaryButton(text: String, onClick: () -> Unit, isEnabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NavyBlue,
            disabledContainerColor = Color.LightGray
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}