package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.AppViewModel

/**
 * RegisterCountrySelector
 * Displays selected country with official currency code, symbol, dial code,
 * and live chips showing all supported wallets & payment methods for that country.
 * Tapping opens a searchable picker dialog containing all 61 countries.
 */
@Composable
fun RegisterCountrySelector(
    viewModel: AppViewModel,
    borderColor: Color = Color(0xFF7C3AED),
    accentColor: Color = Color(0xFFFF2A6D)
) {
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val currentCountryData = remember(viewModel.registerCountry) {
        CountryPaymentMaster.getCountry(viewModel.registerCountry)
            ?: CountryPaymentMaster.allCountries.first()
    }

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) CountryPaymentMaster.allCountries
        else CountryPaymentMaster.allCountries.filter {
            it.countryName.contains(searchQuery, ignoreCase = true) ||
            it.currencyCode.contains(searchQuery, ignoreCase = true) ||
            it.phoneCode.contains(searchQuery, ignoreCase = true) ||
            it.isoCode.contains(searchQuery, ignoreCase = true)
        }
    }

    // Country Selector Card with Live Wallets & Currency
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, borderColor.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Country & Local Currency *",
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Change Country",
                    color = borderColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currentCountryData.flag, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentCountryData.countryName,
                            color = Color(0xFF1E202C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        val currSymbol = CountryPaymentMaster.getCurrencySymbol(currentCountryData.currencyCode)
                        Text(
                            text = "Currency: ${currentCountryData.currencyCode} ($currSymbol) • Dial Code: ${currentCountryData.phoneCode}",
                            color = borderColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Country",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Wallets preview for selected country
            Text(
                text = "Wallets & Payment Methods for ${currentCountryData.countryName}:",
                color = Color(0xFF6B7280),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                currentCountryData.paymentMethods.forEach { method ->
                    Surface(
                        color = Color(0xFFFFF0F5),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFFF85A6).copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFFD81B60),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = method.methodName,
                                color = Color(0xFFD81B60),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                searchQuery = "" 
            },
            title = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Country / Region",
                            color = Color(0xFF1E202C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { showDialog = false; searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF6B7280))
                        }
                    }
                    Text(
                        text = "All 61 countries supported with their official currency and local payment methods.",
                        color = Color(0xFF6B7280),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search country, currency, or dial code...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = borderColor,
                            unfocusedBorderColor = Color(0xFFFF85A6),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredCountries) { country ->
                        val isSelected = country.countryName.equals(viewModel.registerCountry, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.registerCountry = country.countryName
                                    viewModel.registerCurrency = country.currencyCode
                                    viewModel.selectCountryData(country)
                                    showDialog = false
                                    searchQuery = ""
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFFF0F5) else Color.White
                            ),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFFE91E63) else Color(0xFFEEEEEE)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(country.flag, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = country.countryName,
                                                color = Color(0xFF1E202C),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            val sym = CountryPaymentMaster.getCurrencySymbol(country.currencyCode)
                                            Text(
                                                text = "Currency: ${country.currencyCode} ($sym) • Phone: ${country.phoneCode}",
                                                color = Color(0xFF6B7280),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFFE91E63),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Wallets: " + country.paymentMethods.joinToString(", ") { it.methodName },
                                    color = Color(0xFF888888),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    }
}
