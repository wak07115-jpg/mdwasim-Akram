package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.OrderEntity
import com.example.data.model.OrderJsonHelper
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.ProductGridCard
import com.example.ui.components.QuantityStepper
import com.example.ui.components.ShopItemCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerHomeScreen(
  viewModel: LocalKartViewModel,
  onProductClick: (String) -> Unit,
  onShopClick: (String) -> Unit,
  onSeeAllShopsClick: () -> Unit
) {
  val categories by viewModel.categories.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()
  val selectedCat by viewModel.selectedCategoryId.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val customer by viewModel.customers.collectAsState()
  val currentCustomer = customer.find { it.id == viewModel.currentCustomerId.value }

  val featuredShops = shops.filter { it.isFeatured && it.isOpen }

  val filteredProducts = remember(products, selectedCat, searchQuery) {
    products.filter { p ->
      val matchesCat = selectedCat == "all" || p.categoryId == selectedCat
      val matchesQuery = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true) ||
        p.description.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery && p.status == "active"
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight)
      .testTag("customer_home_list"),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // Hero Banner & Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Brush.verticalGradient(listOf(NavyPrimary, NavySurface)))
          .padding(top = 8.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                text = "Hello, ${currentCustomer?.name?.split(" ")?.firstOrNull() ?: "Neighbor"} 👋",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = CardWhite
              )
              Text(
                text = "Discover every local shop around you",
                fontSize = 12.sp,
                color = CardWhite.copy(alpha = 0.7f)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CardWhite.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = CardWhite, modifier = Modifier.size(13.dp))
                Text(
                  text = currentCustomer?.city ?: "Bengaluru",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = CardWhite
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Generated Hero Banner Illustration
          Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_banner),
              contentDescription = "LocalKart Marketplace Banner",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Search Field
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search 50+ local products & shops...", fontSize = 13.sp, color = MutedGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BlueAccent) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
              focusedContainerColor = CardWhite,
              unfocusedContainerColor = CardWhite,
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_search_input")
          )
        }
      }
    }

    // Category Selector
    item {
      Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
          text = "Categories",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = NavyPrimary,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CategoryPill(
            emoji = "✨",
            name = "All",
            isSelected = selectedCat == "all",
            onClick = { viewModel.selectedCategoryId.value = "all" }
          )
          categories.forEach { cat ->
            CategoryPill(
              emoji = cat.emoji,
              name = cat.name,
              isSelected = selectedCat == cat.id,
              onClick = { viewModel.selectedCategoryId.value = cat.id }
            )
          }
        }
      }
    }

    // Featured Shops (if not searching)
    if (searchQuery.isBlank() && selectedCat == "all") {
      item {
        Column(modifier = Modifier.padding(top = 20.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Featured Local Shops", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)
            Text(
              "See all",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BlueAccent,
              modifier = Modifier.clickable { onSeeAllShopsClick() }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(featuredShops) { shop ->
              val count = products.count { it.shopId == shop.id && it.status == "active" }
              ShopItemCard(
                shop = shop,
                productCount = count,
                onClick = { onShopClick(shop.id) },
                modifier = Modifier.width(180.dp)
              )
            }
          }
        }
      }
    }

    // Products Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isNotBlank()) "Search Results (${filteredProducts.size})"
          else if (selectedCat != "all") categories.find { it.id == selectedCat }?.name ?: "Products"
          else "🔥 Popular Products",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = NavyPrimary
        )

        if (searchQuery.isNotBlank() || selectedCat != "all") {
          Text(
            "Clear Filter",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BlueAccent,
            modifier = Modifier.clickable {
              viewModel.searchQuery.value = ""
              viewModel.selectedCategoryId.value = "all"
            }
          )
        }
      }
    }

    if (filteredProducts.isEmpty()) {
      item {
        EmptyStateCard(
          emoji = "🔍",
          title = "No products found",
          description = "Try searching for another keyword or selecting another category"
        )
      }
    } else {
      // 2-Column Product Grid chunked in rows
      val productRows = filteredProducts.chunked(2)
      items(productRows) { rowItems ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          for (product in rowItems) {
            val shop = shops.find { it.id == product.shopId }
            ProductGridCard(
              product = product,
              shopName = shop?.name ?: "Shop",
              onProductClick = { onProductClick(product.id) },
              onAddToCart = { viewModel.addToCart(product) },
              modifier = Modifier.weight(1f)
            )
          }
          if (rowItems.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun CategoryPill(
  emoji: String,
  name: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) BlueAccent else CardWhite,
    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
    shadowElevation = if (isSelected) 2.dp else 0.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(emoji, fontSize = 16.sp)
      Text(
        text = name,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 12.sp,
        color = if (isSelected) CardWhite else TextDark
      )
    }
  }
}

@Composable
fun CustomerExploreScreen(
  viewModel: LocalKartViewModel,
  onShopClick: (String) -> Unit
) {
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()
  val categories by viewModel.categories.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        "Local Stores Directory",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = NavyPrimary
      )
      Text(
        "Explore all licensed local shops operating on LocalKart",
        fontSize = 13.sp,
        color = MutedGray,
        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
      )
    }

    items(shops) { shop ->
      val count = products.count { it.shopId == shop.id && it.status == "active" }
      val cat = categories.find { it.id == shop.category }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onShopClick(shop.id) }
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
            contentAlignment = Alignment.Center
          ) {
            Text(shop.emoji, fontSize = 28.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(shop.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
              if (shop.isFeatured) {
                Text("★", color = AmberWarn, fontSize = 13.sp)
              }
            }
            Text(
              shop.description,
              fontSize = 12.sp,
              color = MutedGray,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("📍 ${shop.city}", fontSize = 11.sp, color = MutedGray)
              Text("•", fontSize = 10.sp, color = LineBorder)
              Text("📦 $count items", fontSize = 11.sp, color = BlueAccent, fontWeight = FontWeight.SemiBold)
              Text("•", fontSize = 10.sp, color = LineBorder)
              Text(
                if (shop.isOpen) "Open" else "Closed",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (shop.isOpen) GreenSuccess else RedDanger
              )
            }
          }

          Icon(Icons.Default.ArrowForward, contentDescription = "View", tint = MutedGray, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

@Composable
fun CustomerShopDetailScreen(
  shopId: String,
  viewModel: LocalKartViewModel,
  onProductClick: (String) -> Unit,
  onBack: () -> Unit
) {
  val shops by viewModel.shops.collectAsState()
  val products by viewModel.products.collectAsState()
  val categories by viewModel.categories.collectAsState()

  val shop = shops.find { it.id == shopId }
  val shopProducts = products.filter { it.shopId == shopId && it.status == "active" }

  if (shop == null) {
    EmptyStateCard(emoji = "🏪", title = "Shop Not Found", description = "The requested shop could not be found.")
    return
  }

  val bannerGradient = when (shop.banner) {
    "b2" -> Brush.linearGradient(listOf(OrangeAccent, Color(0xFFFF3D7F)))
    "b3" -> Brush.linearGradient(listOf(GreenSuccess, Color(0xFF0EA5E9)))
    "b4" -> Brush.linearGradient(listOf(PurpleAccent, Color(0xFFEC4899)))
    else -> Brush.linearGradient(listOf(BlueAccent, PurpleAccent))
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // Shop Hero Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(NavyPrimary)
          .padding(16.dp)
      ) {
        Column {
          // Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(bannerGradient)
          )

          // Shop Logo Avatar
          Box(
            modifier = Modifier
              .padding(start = 12.dp)
              .size(64.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(CardWhite)
              .border(3.dp, CardWhite, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(shop.emoji, fontSize = 32.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(shop.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = CardWhite)
          Text(shop.description, fontSize = 13.sp, color = CardWhite.copy(alpha = 0.8f))

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
          ) {
            Surface(shape = RoundedCornerShape(8.dp), color = CardWhite.copy(alpha = 0.15f)) {
              Text("★ ${if (shop.rating > 0) String.format("%.1f", shop.rating) else "New"}", color = CardWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = CardWhite.copy(alpha = 0.15f)) {
              Text("📍 ${shop.address}", color = CardWhite, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = if (shop.isOpen) GreenSuccess.copy(alpha = 0.3f) else RedDanger.copy(alpha = 0.3f)) {
              Text(if (shop.isOpen) "🟢 Open Now" else "🔴 Closed", color = CardWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = CardWhite.copy(alpha = 0.15f)) {
              Text("📦 ${shopProducts.size} items", color = CardWhite, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
          }
        }
      }
    }

    item {
      Text(
        "Products from this Store (${shopProducts.size})",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = NavyPrimary,
        modifier = Modifier.padding(16.dp)
      )
    }

    if (shopProducts.isEmpty()) {
      item {
        EmptyStateCard(emoji = "📦", title = "No products yet", description = "This shop has not listed products yet.")
      }
    } else {
      val rows = shopProducts.chunked(2)
      items(rows) { rowItems ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          for (product in rowItems) {
            ProductGridCard(
              product = product,
              shopName = shop.name,
              onProductClick = { onProductClick(product.id) },
              onAddToCart = { viewModel.addToCart(product) },
              modifier = Modifier.weight(1f)
            )
          }
          if (rowItems.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun CustomerProductDetailScreen(
  productId: String,
  viewModel: LocalKartViewModel,
  onShopClick: (String) -> Unit,
  onBuyNowClick: () -> Unit,
  onBack: () -> Unit
) {
  val products by viewModel.products.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val categories by viewModel.categories.collectAsState()

  val product = products.find { it.id == productId }
  val shop = shops.find { it.id == product?.shopId }
  val category = categories.find { it.id == product?.categoryId }

  var selectedQty by remember { mutableIntStateOf(1) }

  if (product == null) {
    EmptyStateCard(emoji = "📦", title = "Product Not Found", description = "The product could not be located.")
    return
  }

  val discountPercent = if (product.mrp > product.price) {
    (((product.mrp - product.price) / product.mrp) * 100).toInt()
  } else 0

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(BgLight)
        .verticalScroll(rememberScrollState())
        .padding(bottom = 80.dp)
    ) {
      // Big Hero Emoji Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
        contentAlignment = Alignment.Center
      ) {
        Text(product.emoji, fontSize = 110.sp)

        if (discountPercent > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(16.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(OrangeAccent)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("$discountPercent% OFF", color = CardWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }

      Column(modifier = Modifier.padding(16.dp)) {
        // Badges
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (category != null) {
            Surface(shape = RoundedCornerShape(8.dp), color = BlueAccent.copy(alpha = 0.1f)) {
              Text("${category.emoji} ${category.name}", color = BlueAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (product.stock > 0) GreenSuccess.copy(alpha = 0.1f) else RedDanger.copy(alpha = 0.1f)
          ) {
            Text(
              if (product.stock > 0) "✓ In Stock (${product.stock})" else "Out of Stock",
              color = if (product.stock > 0) GreenSuccess else RedDanger,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(product.name, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = NavyPrimary)

        Spacer(modifier = Modifier.height(6.dp))

        // Price Row
        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("₹${product.price.toInt()}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = NavyPrimary)
          if (product.mrp > product.price) {
            Text("₹${product.mrp.toInt()}", fontSize = 14.sp, color = MutedGray, textDecoration = TextDecoration.LineThrough)
            Text("Save ₹${(product.mrp - product.price).toInt()}", color = GreenSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sold by Shop Card
        if (shop != null) {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onShopClick(shop.id) }
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(BlueAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
              ) {
                Text(shop.emoji, fontSize = 22.sp)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(shop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text("📍 ${shop.city} • ${if (shop.isOpen) "Open Now" else "Closed"}", fontSize = 11.sp, color = MutedGray)
              }
              Icon(Icons.Default.ArrowForward, contentDescription = "View Shop", tint = MutedGray, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Product Description", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(product.description, fontSize = 13.sp, color = TextDark.copy(alpha = 0.8f), lineHeight = 19.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Delivery Info Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CardWhite),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text("🚚", fontSize = 24.sp)
            Column {
              Text("Fast Local Delivery", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
              Text("Estimated delivery in 1-2 hours from ${shop?.name ?: "local store"}", fontSize = 11.sp, color = MutedGray)
            }
          }
        }
      }
    }

    // Bottom Sticky Action Bar
    Surface(
      shadowElevation = 8.dp,
      color = CardWhite,
      border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuantityStepper(
          qty = selectedQty,
          onIncrement = { selectedQty++ },
          onDecrement = { if (selectedQty > 1) selectedQty-- }
        )

        Button(
          onClick = { viewModel.addToCart(product, selectedQty) },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("detail_add_to_cart_btn")
        ) {
          Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Button(
          onClick = {
            viewModel.addToCart(product, selectedQty)
            onBuyNowClick()
          },
          colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("detail_buy_now_btn")
        ) {
          Text("Buy Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
fun CustomerCartScreen(
  viewModel: LocalKartViewModel,
  onProceedToCheckout: () -> Unit,
  onStartShopping: () -> Unit
) {
  val cartItems by viewModel.cartItemsWithProducts.collectAsState()

  if (cartItems.isEmpty()) {
    EmptyStateCard(
      emoji = "🛒",
      title = "Your cart is empty",
      description = "Browse our neighborhood shops and add items to your cart.",
      actionText = "Start Shopping",
      onAction = onStartShopping
    )
    return
  }

  // Group by shop
  val groupedByShop = remember(cartItems) {
    cartItems.groupBy { it.cartItem.shopId }
  }

  val totalSubtotal = cartItems.sumOf { it.product.price * it.cartItem.qty }
  val totalDelivery = groupedByShop.size * 30.0
  val grandTotal = totalSubtotal + totalDelivery

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        "Shopping Cart (${cartItems.sumOf { it.cartItem.qty }} items)",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = NavyPrimary
      )
    }

    // Shop Groups
    groupedByShop.forEach { (shopId, itemsInShop) ->
      item {
        val shop = itemsInShop.first().shop
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardWhite),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            // Shop Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(NavyPrimary)
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(shop?.emoji ?: "🏪", fontSize = 18.sp)
              Text(
                shop?.name ?: "Local Shop",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CardWhite,
                modifier = Modifier.weight(1f)
              )
              Text("Delivery ₹30", color = CardWhite.copy(alpha = 0.7f), fontSize = 11.sp)
            }

            // Products in this Shop
            itemsInShop.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
                  contentAlignment = Alignment.Center
                ) {
                  Text(item.product.emoji, fontSize = 26.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    item.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    "₹${item.product.price.toInt()} each",
                    fontSize = 11.sp,
                    color = MutedGray
                  )
                  Text(
                    "₹${(item.product.price * item.cartItem.qty).toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = NavyPrimary
                  )
                }

                // Controls
                Column(horizontalAlignment = Alignment.End) {
                  QuantityStepper(
                    qty = item.cartItem.qty,
                    onIncrement = { viewModel.updateCartQty(item.cartItem.id, item.cartItem.qty + 1) },
                    onDecrement = { viewModel.updateCartQty(item.cartItem.id, item.cartItem.qty - 1) }
                  )
                  TextButton(
                    onClick = { viewModel.removeCartItem(item.cartItem.id) },
                    contentPadding = PaddingValues(0.dp)
                  ) {
                    Text("Remove", color = RedDanger, fontSize = 11.sp)
                  }
                }
              }
              HorizontalDivider(color = LineBorder, thickness = 0.8.dp)
            }
          }
        }
      }
    }

    // Bill Summary
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Bill Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Item Subtotal", fontSize = 13.sp, color = MutedGray)
            Text("₹${totalSubtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee (${groupedByShop.size} shops)", fontSize = 13.sp, color = MutedGray)
            Text("₹${totalDelivery.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = LineBorder)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("To Pay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NavyPrimary)
            Text("₹${grandTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = BlueAccent)
          }
        }
      }
    }

    // Checkout CTA
    item {
      Button(
        onClick = onProceedToCheckout,
        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("cart_proceed_to_checkout_btn")
      ) {
        Text("Proceed to Checkout • ₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
    }
  }
}

@Composable
fun CustomerCheckoutScreen(
  viewModel: LocalKartViewModel,
  onOrderSuccess: (String) -> Unit,
  onBack: () -> Unit
) {
  val cartItems by viewModel.cartItemsWithProducts.collectAsState()
  val customers by viewModel.customers.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val couponError by viewModel.couponError.collectAsState()

  val currentCustomer = customers.find { it.id == viewModel.currentCustomerId.value }

  var customerName by remember { mutableStateOf(currentCustomer?.name ?: "") }
  var customerPhone by remember { mutableStateOf(currentCustomer?.phone ?: "") }
  var street by remember { mutableStateOf(currentCustomer?.street ?: "") }
  var city by remember { mutableStateOf(currentCustomer?.city ?: "Bengaluru") }
  var state by remember { mutableStateOf(currentCustomer?.state ?: "Karnataka") }
  var pincode by remember { mutableStateOf(currentCustomer?.pincode ?: "560001") }

  var paymentMethod by remember { mutableStateOf("cod") }
  var couponCodeInput by remember { mutableStateOf("") }

  val groupedByShop = remember(cartItems) { cartItems.groupBy { it.cartItem.shopId } }
  val totalSubtotal = cartItems.sumOf { it.product.price * it.cartItem.qty }
  val totalDelivery = groupedByShop.size * 30.0

  val couponDiscount = remember(appliedCoupon, totalSubtotal) {
    val cp = appliedCoupon ?: return@remember 0.0
    if (cp.type == "percent") {
      (totalSubtotal * cp.value / 100).coerceAtMost(cp.maxDiscount)
    } else {
      cp.value.coerceAtMost(totalSubtotal)
    }
  }

  val finalTotal = (totalSubtotal + totalDelivery - couponDiscount).coerceAtLeast(0.0)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text("Complete Your Order", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
    }

    // Delivery Address Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(18.dp))
            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Full Name") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = customerPhone,
            onValueChange = { customerPhone = it },
            label = { Text("Phone Number") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Flat / House / Street Address") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

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
        }
      }
    }

    // Payment Method Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { paymentMethod = "cod" }
              .padding(vertical = 4.dp)
          ) {
            RadioButton(
              selected = paymentMethod == "cod",
              onClick = { paymentMethod = "cod" },
              colors = RadioButtonDefaults.colors(selectedColor = BlueAccent)
            )
            Text("💵 Cash on Delivery (COD)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { paymentMethod = "online" }
              .padding(vertical = 4.dp)
          ) {
            RadioButton(
              selected = paymentMethod == "online",
              onClick = { paymentMethod = "online" },
              colors = RadioButtonDefaults.colors(selectedColor = BlueAccent)
            )
            Text("💳 UPI / Card / NetBanking", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
          }
        }
      }
    }

    // Coupon Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(18.dp))
            Text("Apply Promo Coupon", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (appliedCoupon != null) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(GreenSuccess.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("✓ ${appliedCoupon!!.code}", fontWeight = FontWeight.Bold, color = GreenSuccess, fontSize = 14.sp)
                Text("You save ₹${couponDiscount.toInt()}", color = GreenSuccess, fontSize = 12.sp)
              }
              TextButton(onClick = { viewModel.removeAppliedCoupon() }) {
                Text("Remove", color = RedDanger, fontWeight = FontWeight.Bold)
              }
            }
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = couponCodeInput,
                onValueChange = { couponCodeInput = it },
                placeholder = { Text("e.g. WELCOME50, SAVE10", fontSize = 12.sp) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              )
              Button(
                onClick = { viewModel.applyCouponCode(couponCodeInput, totalSubtotal) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Apply")
              }
            }
            if (couponError != null) {
              Text(couponError!!, color = RedDanger, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
          }
        }
      }
    }

    // Price Breakdown
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Items Total", fontSize = 13.sp, color = MutedGray)
            Text("₹${totalSubtotal.toInt()}", fontSize = 13.sp, color = TextDark)
          }
          Spacer(modifier = Modifier.height(6.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee", fontSize = 13.sp, color = MutedGray)
            Text("₹${totalDelivery.toInt()}", fontSize = 13.sp, color = TextDark)
          }

          if (couponDiscount > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Coupon Discount", fontSize = 13.sp, color = GreenSuccess)
              Text("-₹${couponDiscount.toInt()}", fontSize = 13.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = LineBorder)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Grand Total", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NavyPrimary)
            Text("₹${finalTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = BlueAccent)
          }
        }
      }
    }

    // Place Order Button
    item {
      Button(
        onClick = {
          if (customerName.isBlank() || street.isBlank() || city.isBlank() || pincode.isBlank()) {
            viewModel.showToast("Please fill in full delivery address")
            return@Button
          }
          viewModel.placeOrder(
            street = street,
            city = city,
            state = state,
            pincode = pincode,
            customerName = customerName,
            customerPhone = customerPhone,
            paymentMethod = paymentMethod,
            onSuccess = onOrderSuccess
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("checkout_place_order_btn")
      ) {
        Text("Place Order • ₹${finalTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }
  }
}

@Composable
fun CustomerOrdersScreen(
  viewModel: LocalKartViewModel,
  onOrderClick: (String) -> Unit
) {
  val orders by viewModel.allOrders.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val customerOrders = orders.filter { it.customerId == viewModel.currentCustomerId.value }

  if (customerOrders.isEmpty()) {
    EmptyStateCard(
      emoji = "📦",
      title = "No orders yet",
      description = "Your placed orders will appear here for live tracking."
    )
    return
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("Order History (${customerOrders.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
    }

    items(customerOrders) { order ->
      val shop = shops.find { it.id == order.shopId }
      val items = OrderJsonHelper.deserialize(order.itemsJson)
      val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOrderClick(order.id) }
          .testTag("order_item_${order.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(order.id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
              Text("🏪 ${shop?.name ?: "Shop"}", fontSize = 12.sp, color = MutedGray)
            }
            OrderStatusBadge(status = order.status)
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = LineBorder)

          // Items Preview
          items.take(2).forEach { itm ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${itm.emoji} ${itm.name} x ${itm.qty}", fontSize = 12.sp, color = TextDark)
              Text("₹${(itm.price * itm.qty).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            }
          }
          if (items.size > 2) {
            Text("+ ${items.size - 2} more items", fontSize = 11.sp, color = BlueAccent)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(dateStr, fontSize = 11.sp, color = MutedGray)
            Text("Total: ₹${order.total.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = NavyPrimary)
          }
        }
      }
    }
  }
}

@Composable
fun CustomerOrderDetailScreen(
  orderId: String,
  viewModel: LocalKartViewModel,
  onBack: () -> Unit
) {
  val orders by viewModel.allOrders.collectAsState()
  val shops by viewModel.shops.collectAsState()

  val order = orders.find { it.id == orderId }
  if (order == null) {
    EmptyStateCard(emoji = "📦", title = "Order Not Found", description = "Could not locate this order.")
    return
  }

  val shop = shops.find { it.id == order.shopId }
  val items = OrderJsonHelper.deserialize(order.itemsJson)

  val steps = listOf(
    "pending" to "Order Placed",
    "confirmed" to "Order Confirmed",
    "packed" to "Items Packed",
    "shipped" to "Dispatched",
    "out_for_delivery" to "Out for Delivery",
    "delivered" to "Delivered 🎉"
  )

  val currentStepIdx = when (order.status) {
    "pending" -> 0
    "confirmed" -> 1
    "packed" -> 2
    "shipped" -> 3
    "out_for_delivery" -> 4
    "delivered" -> 5
    else -> -1
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(order.id, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NavyPrimary)
          Text("Store: ${shop?.name ?: "Local Shop"}", fontSize = 12.sp, color = MutedGray)
        }
        OrderStatusBadge(status = order.status)
      }
    }

    // Order Live Timeline Tracker
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Live Delivery Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(14.dp))

          steps.forEachIndexed { index, (key, label) ->
            val isDone = index <= currentStepIdx
            val isCurrent = index == currentStepIdx

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(26.dp)
                  .clip(CircleShape)
                  .background(if (isDone) GreenSuccess else LineBorder),
                contentAlignment = Alignment.Center
              ) {
                if (isDone) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = CardWhite, modifier = Modifier.size(16.dp))
                } else {
                  Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MutedGray))
                }
              }

              Text(
                label,
                fontWeight = if (isCurrent) FontWeight.Bold else if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                color = if (isCurrent) NavyPrimary else if (isDone) TextDark else MutedGray
              )
            }

            if (index < steps.size - 1) {
              Box(
                modifier = Modifier
                  .padding(start = 12.dp)
                  .width(2.dp)
                  .height(18.dp)
                  .background(if (index < currentStepIdx) GreenSuccess else LineBorder)
              )
            }
          }
        }
      }
    }

    // Items Breakdown
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Ordered Items", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          items.forEach { itm ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(itm.emoji, fontSize = 20.sp)
                Column {
                  Text(itm.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
                  Text("Qty: ${itm.qty} • ₹${itm.price.toInt()} each", fontSize = 11.sp, color = MutedGray)
                }
              }
              Text("₹${(itm.price * itm.qty).toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
            }
            HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 6.dp))
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", fontSize = 12.sp, color = MutedGray)
            Text("₹${order.subtotal.toInt()}", fontSize = 12.sp, color = TextDark)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery", fontSize = 12.sp, color = MutedGray)
            Text("₹${order.delivery.toInt()}", fontSize = 12.sp, color = TextDark)
          }
          if (order.discount > 0) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Coupon Discount", fontSize = 12.sp, color = GreenSuccess)
              Text("-₹${order.discount.toInt()}", fontSize = 12.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
            }
          }
          HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
            Text("₹${order.total.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BlueAccent)
          }
        }
      }
    }

    // Delivery Info
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Delivery Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
          Spacer(modifier = Modifier.height(6.dp))
          Text("${order.customerName} • ${order.customerPhone}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextDark)
          Text("${order.street}, ${order.city}, ${order.state} - ${order.pincode}", fontSize = 12.sp, color = MutedGray)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Payment Method: ${if (order.paymentMethod == "cod") "Cash on Delivery" else "Online Paid"}", fontSize = 12.sp, color = BlueAccent)
        }
      }
    }
  }
}

@Composable
fun CustomerProfileScreen(
  viewModel: LocalKartViewModel
) {
  val customers by viewModel.customers.collectAsState()
  val orders by viewModel.allOrders.collectAsState()
  val currentCustomer = customers.find { it.id == viewModel.currentCustomerId.value }
  val customerOrders = orders.filter { it.customerId == viewModel.currentCustomerId.value }
  val totalSpent = customerOrders.sumOf { it.total }

  var showEditAddressDialog by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(BgLight)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Customer Info Card
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = NavyPrimary),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(BlueAccent, PurpleAccent))),
          contentAlignment = Alignment.Center
        ) {
          Text(
            currentCustomer?.name?.firstOrNull()?.toString() ?: "A",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = CardWhite
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(currentCustomer?.name ?: "Aarav Patel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CardWhite)
        Text(currentCustomer?.email ?: "customer@demo.com", fontSize = 12.sp, color = CardWhite.copy(alpha = 0.7f))
        Text("📱 ${currentCustomer?.phone ?: "9876500001"}", fontSize = 12.sp, color = CardWhite.copy(alpha = 0.7f))
      }
    }

    // Stats
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Total Orders", fontSize = 11.sp, color = MutedGray, fontWeight = FontWeight.Bold)
          Text("${customerOrders.size}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = NavyPrimary)
        }
      }
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.weight(1f)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Total Spent", fontSize = 11.sp, color = MutedGray, fontWeight = FontWeight.Bold)
          Text("₹${totalSpent.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = GreenSuccess)
        }
      }
    }

    // Menu Options
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditAddressDialog = true }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = BlueAccent)
          Column(modifier = Modifier.weight(1f)) {
            Text("Saved Delivery Address", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(currentCustomer?.street ?: "Manage address", fontSize = 11.sp, color = MutedGray, maxLines = 1)
          }
          Icon(Icons.Default.Edit, contentDescription = null, tint = MutedGray, modifier = Modifier.size(18.dp))
        }

        HorizontalDivider(color = LineBorder)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.resetDemoData() }
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = AmberWarn)
          Column(modifier = Modifier.weight(1f)) {
            Text("Reset Demo Marketplace Data", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Re-seed all 10 shops, 50 items, coupons and orders", fontSize = 11.sp, color = MutedGray)
          }
        }
      }
    }
  }

  if (showEditAddressDialog && currentCustomer != null) {
    EditAddressDialog(
      customer = currentCustomer,
      onSave = { name, phone, street, city, state, pin ->
        viewModel.updateCustomerAddress(name, phone, street, city, state, pin)
        showEditAddressDialog = false
      },
      onDismiss = { showEditAddressDialog = false }
    )
  }
}

@Composable
fun EditAddressDialog(
  customer: com.example.data.model.CustomerEntity,
  onSave: (String, String, String, String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(customer.name) }
  var phone by remember { mutableStateOf(customer.phone) }
  var street by remember { mutableStateOf(customer.street) }
  var city by remember { mutableStateOf(customer.city) }
  var state by remember { mutableStateOf(customer.state) }
  var pincode by remember { mutableStateOf(customer.pincode) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Edit Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyPrimary)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Address / Flat") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f))
          OutlinedTextField(value = pincode, onValueChange = { pincode = it }, label = { Text("Pincode") }, modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
            Text("Cancel")
          }
          Button(
            onClick = { onSave(name, phone, street, city, state, pincode) },
            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
            modifier = Modifier.weight(1f)
          ) {
            Text("Save")
          }
        }
      }
    }
  }
}
