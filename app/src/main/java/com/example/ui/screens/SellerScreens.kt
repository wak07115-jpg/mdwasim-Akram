package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderJsonHelper
import com.example.data.model.ProductEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderStatusBadge
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
fun SellerDashboardScreen(
  viewModel: LocalKartViewModel,
  onNavigateToProducts: () -> Unit,
  onNavigateToOrders: () -> Unit,
  onNavigateToShop: () -> Unit
) {
  val sellers by viewModel.sellers.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()
  val orders by viewModel.allOrders.collectAsState()

  val seller = sellers.find { it.id == viewModel.currentSellerId.value }
  val shop = shops.find { it.sellerId == viewModel.currentSellerId.value }
  val sellerProducts = products.filter { it.sellerId == viewModel.currentSellerId.value }
  val sellerOrders = orders.filter { it.sellerId == viewModel.currentSellerId.value }

  val pendingOrders = sellerOrders.filter { it.status == "pending" || it.status == "confirmed" }
  val totalDeliveredSales = sellerOrders.filter { it.status == "delivered" }.sumOf { it.total }
  val lowStockCount = sellerProducts.count { it.stock in 1..5 }

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
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                shop?.name ?: "My Store",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CardWhite
              )
              Text(
                "Owner: ${seller?.ownerName ?: "Rajesh Sharma"}",
                fontSize = 12.sp,
                color = CardWhite.copy(alpha = 0.7f)
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (shop?.isOpen == true) GreenSuccess.copy(alpha = 0.2f) else RedDanger.copy(alpha = 0.2f)
            ) {
              Text(
                if (shop?.isOpen == true) "🟢 Store Open" else "🔴 Store Closed",
                color = CardWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }

    // Stats Grid
    item {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          SellerStatCard(
            label = "Total Orders",
            value = "${sellerOrders.size}",
            color = BlueAccent,
            modifier = Modifier.weight(1f)
          )
          SellerStatCard(
            label = "Pending Orders",
            value = "${pendingOrders.size}",
            color = if (pendingOrders.isNotEmpty()) AmberWarn else NavyPrimary,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
          SellerStatCard(
            label = "Total Sales",
            value = "₹${totalDeliveredSales.toInt()}",
            color = GreenSuccess,
            modifier = Modifier.weight(1f)
          )
          SellerStatCard(
            label = "Products Listed",
            value = "${sellerProducts.size}",
            color = PurpleAccent,
            modifier = Modifier.weight(1f)
          )
        }

        if (lowStockCount > 0) {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AmberWarn.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarn.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("⚠️", fontSize = 16.sp)
              Text(
                "$lowStockCount items are running low on stock (<5 left)",
                fontSize = 12.sp,
                color = TextDark,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }

    // Quick Actions
    item {
      Text(
        "Manage Store",
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
      ) {
        Column {
          SellerActionRow(
            emoji = "📦",
            title = "Products Catalog",
            subtitle = "${sellerProducts.size} items listed in your store",
            onClick = onNavigateToProducts
          )
          HorizontalDivider(color = LineBorder)
          SellerActionRow(
            emoji = "📋",
            title = "Order Management",
            subtitle = "${pendingOrders.size} pending fulfillment",
            onClick = onNavigateToOrders
          )
          HorizontalDivider(color = LineBorder)
          SellerActionRow(
            emoji = "🏪",
            title = "Shop Settings & Profile",
            subtitle = "Update store hours, address, and info",
            onClick = onNavigateToShop
          )
        }
      }
    }
  }
}

@Composable
fun SellerStatCard(
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
fun SellerActionRow(
  emoji: String,
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
      Text(emoji, fontSize = 20.sp)
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
      Text(subtitle, fontSize = 11.sp, color = MutedGray)
    }
    Text("→", fontSize = 16.sp, color = MutedGray)
  }
}

@Composable
fun SellerProductsScreen(
  viewModel: LocalKartViewModel
) {
  val products by viewModel.products.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val sellerProducts = products.filter { it.sellerId == viewModel.currentSellerId.value }

  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(BgLight),
      contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp, start = 16.dp, end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("My Products (${sellerProducts.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
          Button(
            onClick = { showAddDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Item", fontSize = 12.sp)
          }
        }
      }

      if (sellerProducts.isEmpty()) {
        item {
          EmptyStateCard(
            emoji = "📦",
            title = "No products yet",
            description = "Add your first product to start accepting orders.",
            actionText = "Add Product",
            onAction = { showAddDialog = true }
          )
        }
      } else {
        items(sellerProducts) { product ->
          val cat = categories.find { it.id == product.categoryId }
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
                  .size(50.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
                contentAlignment = Alignment.Center
              ) {
                Text(product.emoji, fontSize = 26.sp)
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark, maxLines = 1)
                Text("₹${product.price.toInt()} • MRP ₹${product.mrp.toInt()}", fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 2.dp)) {
                  Text(
                    "Stock: ${product.stock}",
                    fontSize = 11.sp,
                    color = if (product.stock <= 5) AmberWarn else MutedGray,
                    fontWeight = FontWeight.Bold
                  )
                  Text("•", fontSize = 10.sp, color = LineBorder)
                  Text(cat?.name ?: "Category", fontSize = 11.sp, color = MutedGray)
                }
              }

              IconButton(onClick = { editingProduct = product }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BlueAccent, modifier = Modifier.size(18.dp))
              }

              IconButton(onClick = { viewModel.deleteProduct(product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }
  }

  if (showAddDialog || editingProduct != null) {
    AddEditProductDialog(
      product = editingProduct,
      categories = categories,
      onSave = { name, catId, emoji, price, mrp, stock, desc, sku, status ->
        viewModel.saveProduct(editingProduct?.id, name, catId, emoji, price, mrp, stock, desc, sku, status)
        showAddDialog = false
        editingProduct = null
      },
      onDismiss = {
        showAddDialog = false
        editingProduct = null
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
  product: ProductEntity?,
  categories: List<com.example.data.model.CategoryEntity>,
  onSave: (String, String, String, Double, Double, Int, String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(product?.name ?: "") }
  var categoryId by remember { mutableStateOf(product?.categoryId ?: categories.firstOrNull()?.id ?: "cat_grocery") }
  var emoji by remember { mutableStateOf(product?.emoji ?: "📦") }
  var priceStr by remember { mutableStateOf(product?.price?.toInt()?.toString() ?: "") }
  var mrpStr by remember { mutableStateOf(product?.mrp?.toInt()?.toString() ?: "") }
  var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "20") }
  var description by remember { mutableStateOf(product?.description ?: "") }
  var sku by remember { mutableStateOf(product?.sku ?: "") }
  var status by remember { mutableStateOf(product?.status ?: "active") }

  val commonEmojis = listOf("📦", "🛒", "🍚", "🫘", "🛢️", "🍬", "🍵", "👚", "👖", "🥻", "👔", "🧥", "🎧", "⌚", "🔋", "🔊", "🔌", "🧴", "💋", "🫗", "🌸", "🧼", "🥨", "🍫", "🍪", "🥔", "🥜", "🍲", "🍽️", "🫙", "📖", "📓", "🖊️", "🎒", "👟", "👞", "🩴", "💊", "🩹", "🍯", "🌿", "🫒", "🧵")

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          if (product != null) "Edit Product" else "Add New Product",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = NavyPrimary
        )

        // Emoji Picker
        Text("Selected Icon: $emoji", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedGray)
        Row(
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          commonEmojis.forEach { em ->
            Surface(
              onClick = { emoji = em },
              shape = RoundedCornerShape(8.dp),
              color = if (emoji == em) BlueAccent else BgLight,
              border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
            ) {
              Text(em, fontSize = 20.sp, modifier = Modifier.padding(6.dp))
            }
          }
        }

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Product Title") },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Category dropdown
        var catExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
          expanded = catExpanded,
          onExpandedChange = { catExpanded = !catExpanded }
        ) {
          OutlinedTextField(
            value = categories.find { it.id == categoryId }?.name ?: "Select",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = catExpanded,
            onDismissRequest = { catExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text("${cat.emoji} ${cat.name}") },
                onClick = {
                  categoryId = cat.id
                  catExpanded = false
                }
              )
            }
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text("Price (₹)") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = mrpStr,
            onValueChange = { mrpStr = it },
            label = { Text("MRP (₹)") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = stockStr,
            onValueChange = { stockStr = it },
            label = { Text("Stock Qty") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = sku,
            onValueChange = { sku = it },
            label = { Text("SKU (Optional)") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          minLines = 2,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
        ) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
            Text("Cancel")
          }
          Button(
            onClick = {
              val price = priceStr.toDoubleOrNull() ?: 0.0
              val mrp = mrpStr.toDoubleOrNull() ?: price
              val stock = stockStr.toIntOrNull() ?: 0
              if (name.isNotBlank() && price > 0) {
                onSave(name, categoryId, emoji, price, mrp, stock, description, sku, status)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            modifier = Modifier.weight(1f)
          ) {
            Text(if (product != null) "Update" else "Add")
          }
        }
      }
    }
  }
}

@Composable
fun SellerOrdersScreen(
  viewModel: LocalKartViewModel
) {
  val orders by viewModel.allOrders.collectAsState()
  val sellerOrders = orders.filter { it.sellerId == viewModel.currentSellerId.value }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        "Incoming Store Orders (${sellerOrders.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = NavyPrimary
      )
    }

    if (sellerOrders.isEmpty()) {
      item {
        EmptyStateCard(
          emoji = "📋",
          title = "No orders received yet",
          description = "When customers purchase items from your store, they will appear here for fulfillment."
        )
      }
    } else {
      items(sellerOrders) { order ->
        val items = OrderJsonHelper.deserialize(order.itemsJson)

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardWhite),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(order.id, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                Text(order.customerName, fontSize = 12.sp, color = MutedGray)
              }
              OrderStatusBadge(status = order.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = LineBorder)

            // Items
            items.forEach { itm ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("${itm.emoji} ${itm.name} x ${itm.qty}", fontSize = 12.sp, color = TextDark)
                Text("₹${(itm.price * itm.qty).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BgLight,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("📍 Delivery to: ${order.street}, ${order.city}", fontSize = 11.sp, color = MutedGray)
                Text("📱 ${order.customerPhone} • Payment: ${order.paymentMethod.uppercase()}", fontSize = 11.sp, color = MutedGray)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fulfillment Actions Pipeline
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Total: ₹${order.total.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = NavyPrimary)

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                when (order.status) {
                  "pending" -> {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "confirmed") },
                      colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Accept", fontSize = 11.sp)
                    }
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "cancelled") },
                      colors = ButtonDefaults.buttonColors(containerColor = RedDanger),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Reject", fontSize = 11.sp)
                    }
                  }
                  "confirmed" -> {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "packed") },
                      colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Mark Packed", fontSize = 11.sp)
                    }
                  }
                  "packed" -> {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "shipped") },
                      colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Mark Shipped", fontSize = 11.sp)
                    }
                  }
                  "shipped" -> {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "out_for_delivery") },
                      colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Out for Delivery", fontSize = 11.sp)
                    }
                  }
                  "out_for_delivery" -> {
                    Button(
                      onClick = { viewModel.updateOrderStatus(order.id, "delivered") },
                      colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text("Mark Delivered", fontSize = 11.sp)
                    }
                  }
                  else -> {}
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SellerShopEditScreen(
  viewModel: LocalKartViewModel
) {
  val shops by viewModel.shops.collectAsState()
  val shop = shops.find { it.sellerId == viewModel.currentSellerId.value }

  var name by remember(shop) { mutableStateOf(shop?.name ?: "") }
  var description by remember(shop) { mutableStateOf(shop?.description ?: "") }
  var address by remember(shop) { mutableStateOf(shop?.address ?: "") }
  var city by remember(shop) { mutableStateOf(shop?.city ?: "") }
  var state by remember(shop) { mutableStateOf(shop?.state ?: "") }
  var pincode by remember(shop) { mutableStateOf(shop?.pincode ?: "") }
  var isOpen by remember(shop) { mutableStateOf(shop?.isOpen ?: true) }

  if (shop == null) {
    EmptyStateCard(emoji = "🏪", title = "Shop Not Found", description = "Could not find your seller shop.")
    return
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text("Store Profile & Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Open/Closed Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Store Operating Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(if (isOpen) "Currently accepting orders" else "Store paused", fontSize = 12.sp, color = MutedGray)
          }
          Switch(
            checked = isOpen,
            onCheckedChange = { isOpen = it },
            colors = SwitchDefaults.colors(checkedThumbColor = GreenSuccess)
          )
        }

        HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 4.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Shop Name") },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Physical Address / Street") },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = pincode,
            onValueChange = { pincode = it },
            label = { Text("Pincode") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = {
            viewModel.updateSellerShop(
              shopId = shop.id,
              name = name,
              description = description,
              address = address,
              city = city,
              state = state,
              pincode = pincode,
              isOpen = isOpen
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Text("Save Store Settings", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
