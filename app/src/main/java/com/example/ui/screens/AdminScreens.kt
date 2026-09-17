package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmberWarn
import com.example.ui.theme.BgLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MutedGray
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.LocalKartViewModel

@Composable
fun AdminDashboardScreen(
  viewModel: LocalKartViewModel,
  onNavigateToSellers: () -> Unit,
  onNavigateToShops: () -> Unit,
  onNavigateToCategories: () -> Unit,
  onNavigateToCoupons: () -> Unit,
  onNavigateToCommissions: () -> Unit
) {
  val sellers by viewModel.sellers.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()
  val orders by viewModel.allOrders.collectAsState()
  val customers by viewModel.customers.collectAsState()

  val totalGMV = orders.filter { it.status == "delivered" }.sumOf { it.total }
  val platformCommission = totalGMV * 0.05
  val pendingSellers = sellers.filter { it.status == "pending" }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Brush.verticalGradient(listOf(NavyPrimary, NavySurface)))
          .padding(20.dp)
      ) {
        Column {
          Text("👑 Admin Console", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CardWhite)
          Text("LocalKart Platform Overview & Governance", fontSize = 12.sp, color = CardWhite.copy(alpha = 0.7f))
        }
      }
    }

    // High Level GMV & Platform Stats
    item {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          AdminStatCard(
            label = "Platform GMV Sales",
            value = "₹${totalGMV.toInt()}",
            color = GreenSuccess,
            modifier = Modifier.weight(1f)
          )
          AdminStatCard(
            label = "Commissions (5%)",
            value = "₹${platformCommission.toInt()}",
            color = BlueAccent,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          AdminStatCard(label = "Active Shops", value = "${shops.size}", color = PurpleAccent, modifier = Modifier.weight(1f))
          AdminStatCard(label = "Sellers", value = "${sellers.size}", color = NavyPrimary, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          AdminStatCard(label = "Listed Products", value = "${products.size}", color = OrangeAccent, modifier = Modifier.weight(1f))
          AdminStatCard(label = "Total Orders", value = "${orders.size}", color = NavyPrimary, modifier = Modifier.weight(1f))
        }

        // Pending Seller Approvals Alert
        if (pendingSellers.isNotEmpty()) {
          Spacer(modifier = Modifier.height(14.dp))
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RedDanger.copy(alpha = 0.08f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedDanger.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                "⚠️ ${pendingSellers.size} Pending Seller Registrations",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RedDanger
              )
              Text("Review and approve new store merchants wanting to sell", fontSize = 11.sp, color = TextDark)
              Spacer(modifier = Modifier.height(8.dp))

              pendingSellers.forEach { seller ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(seller.ownerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(seller.email, fontSize = 11.sp, color = MutedGray)
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                      onClick = { viewModel.setSellerStatus(seller.id, "approved") },
                      colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text("Approve", fontSize = 10.sp)
                    }
                    Button(
                      onClick = { viewModel.setSellerStatus(seller.id, "rejected") },
                      colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text("Reject", fontSize = 10.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Management Modules
    item {
      Text(
        "Platform Governance Modules",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = NavyPrimary,
        modifier = Modifier.padding(start = 16.dp, bottom = 10.dp)
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column {
          AdminModuleRow(
            icon = Icons.Default.People,
            title = "Manage Sellers",
            subtitle = "${sellers.size} onboarded merchants",
            onClick = onNavigateToSellers
          )
          HorizontalDivider(color = LineBorder)
          AdminModuleRow(
            icon = Icons.Default.Store,
            title = "Manage Storefronts",
            subtitle = "${shops.size} registered storefronts",
            onClick = onNavigateToShops
          )
          HorizontalDivider(color = LineBorder)
          AdminModuleRow(
            icon = Icons.Default.Category,
            title = "Manage Categories",
            subtitle = "Product taxonomy and tags",
            onClick = onNavigateToCategories
          )
          HorizontalDivider(color = LineBorder)
          AdminModuleRow(
            icon = Icons.Default.LocalOffer,
            title = "Platform Coupons & Promos",
            subtitle = "Discount rules and campaigns",
            onClick = onNavigateToCoupons
          )
          HorizontalDivider(color = LineBorder)
          AdminModuleRow(
            icon = Icons.Default.Paid,
            title = "Commissions & Platform Fees",
            subtitle = "Platform take rate settings",
            onClick = onNavigateToCommissions
          )
        }
      }
    }
  }
}

@Composable
fun AdminStatCard(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(label, fontSize = 11.sp, color = MutedGray, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}

@Composable
fun AdminModuleRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(BlueAccent.copy(alpha = 0.08f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(20.dp))
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
      Text(subtitle, fontSize = 11.sp, color = MutedGray)
    }
    Text("→", fontSize = 16.sp, color = MutedGray)
  }
}

@Composable
fun AdminSellersScreen(
  viewModel: LocalKartViewModel
) {
  val sellers by viewModel.sellers.collectAsState()
  val shops by viewModel.shops.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text("Seller Management (${sellers.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
    }

    items(sellers) { seller ->
      val shop = shops.find { it.sellerId == seller.id }
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(seller.ownerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
              Text(shop?.name ?: "No Store Linked", fontSize = 12.sp, color = BlueAccent, fontWeight = FontWeight.SemiBold)
              Text("${seller.email} • 📱 ${seller.phone}", fontSize = 11.sp, color = MutedGray)
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = when (seller.status) {
                "approved" -> GreenSuccess.copy(alpha = 0.12f)
                "pending" -> AmberWarn.copy(alpha = 0.12f)
                else -> RedDanger.copy(alpha = 0.12f)
              }
            ) {
              Text(
                seller.status.uppercase(),
                color = when (seller.status) {
                  "approved" -> GreenSuccess
                  "pending" -> AmberWarn
                  else -> RedDanger
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (seller.status == "pending") {
              Button(
                onClick = { viewModel.setSellerStatus(seller.id, "approved") },
                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Approve", fontSize = 11.sp)
              }
              Spacer(modifier = Modifier.width(6.dp))
              Button(
                onClick = { viewModel.setSellerStatus(seller.id, "rejected") },
                colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Reject", fontSize = 11.sp)
              }
            } else if (seller.status == "approved") {
              OutlinedButton(
                onClick = { viewModel.setSellerStatus(seller.id, "suspended") },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Suspend", color = RedDanger, fontSize = 11.sp)
              }
            } else {
              Button(
                onClick = { viewModel.setSellerStatus(seller.id, "approved") },
                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Reactivate", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminShopsScreen(
  viewModel: LocalKartViewModel
) {
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text("Platform Storefronts (${shops.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
    }

    items(shops) { shop ->
      val count = products.count { it.shopId == shop.id }
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
            contentAlignment = Alignment.Center
          ) {
            Text(shop.emoji, fontSize = 24.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(shop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("${shop.city} • $count products listed", fontSize = 11.sp, color = MutedGray)
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (shop.isFeatured) PurpleAccent.copy(alpha = 0.1f) else BgLight
              ) {
                Text(
                  if (shop.isFeatured) "★ Featured" else "Standard",
                  fontSize = 10.sp,
                  color = if (shop.isFeatured) PurpleAccent else MutedGray,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (shop.isOpen) GreenSuccess.copy(alpha = 0.1f) else RedDanger.copy(alpha = 0.1f)
              ) {
                Text(
                  if (shop.isOpen) "Open" else "Closed",
                  fontSize = 10.sp,
                  color = if (shop.isOpen) GreenSuccess else RedDanger,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Button(
              onClick = { viewModel.toggleShopFeatured(shop.id) },
              colors = ButtonDefaults.buttonColors(containerColor = if (shop.isFeatured) PurpleAccent else NavyPrimary),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(if (shop.isFeatured) "Unfeature" else "Feature", fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
              onClick = { viewModel.toggleShopOpen(shop.id) },
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(if (shop.isOpen) "Close" else "Open", fontSize = 10.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCategoriesScreen(
  viewModel: LocalKartViewModel
) {
  val categories by viewModel.categories.collectAsState()
  val products by viewModel.products.collectAsState()

  var showAddCatDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Categories (${categories.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
        Button(
          onClick = { showAddCatDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Category", fontSize = 12.sp)
        }
      }
    }

    items(categories) { cat ->
      val count = products.count { it.categoryId == cat.id }
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(BlueAccent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Text(cat.emoji, fontSize = 24.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("$count products associated", fontSize = 11.sp, color = MutedGray)
          }

          IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }

  if (showAddCatDialog) {
    var catName by remember { mutableStateOf("") }
    var catEmoji by remember { mutableStateOf("🏷️") }

    Dialog(onDismissRequest = { showAddCatDialog = false }) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Add New Category", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
          OutlinedTextField(
            value = catName,
            onValueChange = { catName = it },
            label = { Text("Category Name") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = catEmoji,
            onValueChange = { catEmoji = it },
            label = { Text("Emoji") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { showAddCatDialog = false }, modifier = Modifier.weight(1f)) {
              Text("Cancel")
            }
            Button(
              onClick = {
                if (catName.isNotBlank()) {
                  viewModel.addCategory(catName, catEmoji, "#2563FF")
                  showAddCatDialog = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
              modifier = Modifier.weight(1f)
            ) {
              Text("Add")
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCouponsScreen(
  viewModel: LocalKartViewModel
) {
  val coupons by viewModel.coupons.collectAsState()
  var showCreateDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Active Coupons (${coupons.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
        Button(
          onClick = { showCreateDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Create Coupon", fontSize = 12.sp)
        }
      }
    }

    items(coupons) { coupon ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(OrangeAccent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Text("🎟️", fontSize = 24.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(coupon.code, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = NavyPrimary)
            Text(
              if (coupon.type == "percent") "${coupon.value.toInt()}% Off (Max ₹${coupon.maxDiscount.toInt()})"
              else "Flat ₹${coupon.value.toInt()} Off",
              fontSize = 12.sp,
              color = GreenSuccess,
              fontWeight = FontWeight.Bold
            )
            Text(
              "Min Order ₹${coupon.minOrder.toInt()} • Used: ${coupon.used}/${coupon.usageLimit}",
              fontSize = 11.sp,
              color = MutedGray
            )
          }

          IconButton(onClick = { viewModel.deleteCoupon(coupon.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    var code by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("percent") }
    var valueStr by remember { mutableStateOf("15") }
    var minOrderStr by remember { mutableStateOf("299") }
    var maxDiscountStr by remember { mutableStateOf("150") }
    var limitStr by remember { mutableStateOf("500") }

    Dialog(onDismissRequest = { showCreateDialog = false }) {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text("Create New Promo Coupon", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)

          OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Coupon Code (e.g. FESTIVE20)") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = type == "percent", onClick = { type = "percent" }, colors = RadioButtonDefaults.colors(selectedColor = BlueAccent))
            Text("Percentage (%)", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = type == "fixed", onClick = { type = "fixed" }, colors = RadioButtonDefaults.colors(selectedColor = BlueAccent))
            Text("Fixed (₹)", fontSize = 13.sp)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = valueStr,
              onValueChange = { valueStr = it },
              label = { Text(if (type == "percent") "Discount %" else "Discount ₹") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = minOrderStr,
              onValueChange = { minOrderStr = it },
              label = { Text("Min Order (₹)") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = maxDiscountStr,
              onValueChange = { maxDiscountStr = it },
              label = { Text("Max Cap (₹)") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = limitStr,
              onValueChange = { limitStr = it },
              label = { Text("Usage Limit") },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            OutlinedButton(onClick = { showCreateDialog = false }, modifier = Modifier.weight(1f)) {
              Text("Cancel")
            }
            Button(
              onClick = {
                val value = valueStr.toDoubleOrNull() ?: 0.0
                val minOrder = minOrderStr.toDoubleOrNull() ?: 0.0
                val maxDiscount = maxDiscountStr.toDoubleOrNull() ?: 0.0
                val limit = limitStr.toIntOrNull() ?: 100
                if (code.isNotBlank() && value > 0) {
                  viewModel.createCoupon(code, type, value, minOrder, maxDiscount, limit)
                  showCreateDialog = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
              modifier = Modifier.weight(1f)
            ) {
              Text("Create")
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCommissionsScreen(
  viewModel: LocalKartViewModel
) {
  var globalRate by remember { mutableStateOf("5") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text("Platform Commission Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Standard Platform Commission Rate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
          "This fee percentage is calculated on delivered orders from all registered merchants.",
          fontSize = 12.sp,
          color = MutedGray
        )

        OutlinedTextField(
          value = globalRate,
          onValueChange = { globalRate = it },
          label = { Text("Global Commission (%)") },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = { viewModel.updateCommission(globalRate) },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Text("Save Rate", fontWeight = FontWeight.Bold)
        }
      }
    }

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Platform Reset Option", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
        Text("Restore fresh sample data for all 10 shops, 50 products, coupons, and orders.", fontSize = 12.sp, color = MutedGray)
        Spacer(modifier = Modifier.height(4.dp))
        Button(
          onClick = { viewModel.resetDemoData() },
          colors = ButtonDefaults.buttonColors(containerColor = AmberWarn),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Reset Demo Marketplace Data")
        }
      }
    }
  }
}
