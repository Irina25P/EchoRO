package com.example.echoro.ui.screens.generatevoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.echoro.R
import com.example.echoro.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoRoTopBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_echoro),
                    contentDescription = "Logo",
                    modifier = Modifier.height(40.dp)
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyBlue
        ),
        modifier = modifier
    )
}