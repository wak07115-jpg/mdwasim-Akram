package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.AmberWarn
import com.example.ui.theme.BgLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MutedGray
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.LocalKartViewModel

/**
 * Screen displaying the list of marketplace products loaded from the Room database.
 * Includes an interactive Floating Action Button to launch the 'Add Product' form.
 */
@Composable
fun ProductListScreen(
  viewModel: LocalKartViewModel,
  modifier: Modifier = Modifier,
  onProductClick: ((ProductEntity) -> Unit)? = null,
  onNavigateToAddProduct: (() -> Unit)? = null
) {
  val products by viewModel.products.collectAsState()
  val shops by viewModel.shops.collectAsState()
  val categories by viewModel.categories.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedShopId by remember { mutableStateOf("all") }
  var showAddProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

  val filteredProducts = remember(products, searchQuery, selectedShopId) {
    products.filter { product ->
      val matchesSearch = searchQuery.isBlank() ||
        product.name.contains(searchQuery, ignoreCase = true) ||
        product.description.contains(searchQuery, ignoreCase = true)
      val matchesShop = selectedShopId == "all" || product.shopId == selectedShopId
      matchesSearch && matchesShop
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = {
          if (onNavigateToAddProduct != null) {
            onNavigateToAddProduct()
          } else {
            showAddProductDialog = true
          }
        },
        containerColor = OrangeAccent,
        contentColor = CardWhite,
        icon = { Icon(Icons.Default.Add, contentDescription = "Add Product Icon") },
        text = { Text("Add Product", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("add_product_fab")
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(BgLight)
        .padding(innerPadding)
    ) {
      // Header Section
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Brush.verticalGradient(listOf(NavyPrimary, Color(0xFF1E293B))))
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Marketplace Catalog",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = CardWhite
              )
              Text(
                text = "${products.size} total items stored in Room DB",
                fontSize = 12.sp,
                color = CardWhite.copy(alpha = 0.75f)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CardWhite.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = CardWhite, modifier = Modifier.size(16.dp))
                Text(
                  text = "${filteredProducts.size} Shown",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = CardWhite
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Live Search Bar
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search products by name or description...", fontSize = 13.sp, color = MutedGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BlueAccent) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = MutedGray, modifier = Modifier.size(16.dp))
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = CardWhite,
              unfocusedContainerColor = CardWhite,
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("product_search_input")
          )
        }
      }

      // Store Filter Pill Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          onClick = { selectedShopId = "all" },
          shape = RoundedCornerShape(10.dp),
          color = if (selectedShopId == "all") BlueAccent else CardWhite,
          border = if (selectedShopId == "all") null else androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
        ) {
          Text(
            text = "All Stores (${products.size})",
            fontSize = 12.sp,
            fontWeight = if (selectedShopId == "all") FontWeight.Bold else FontWeight.Medium,
            color = if (selectedShopId == "all") CardWhite else TextDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }

        shops.forEach { shop ->
          val shopProductCount = products.count { it.shopId == shop.id }
          val isSelected = selectedShopId == shop.id
          Surface(
            onClick = { selectedShopId = shop.id },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) BlueAccent else CardWhite,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(shop.emoji, fontSize = 13.sp)
              Text(
                text = "${shop.name} ($shopProductCount)",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CardWhite else TextDark
              )
            }
          }
        }
      }

      // Products List
      if (filteredProducts.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          EmptyStateCard(
            emoji = "📦",
            title = "No Products Found",
            description = if (searchQuery.isNotBlank()) "No items matched '$searchQuery'." else "There are no products in this selection.",
            actionText = "Add New Product",
            onAction = { showAddProductDialog = true }
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("product_list_container"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredProducts, key = { it.id }) { product ->
            val shop = shops.find { it.id == product.shopId }
            val category = categories.find { it.id == product.categoryId }

            ProductListItemCard(
              product = product,
              shopName = shop?.name ?: "Local Shop",
              categoryName = category?.name ?: "General",
              onCardClick = { onProductClick?.invoke(product) },
              onEditClick = { editingProduct = product },
              onDeleteClick = { viewModel.deleteProduct(product.id) }
            )
          }
        }
      }
    }
  }

  // Add / Edit Product Form Dialog
  if (showAddProductDialog || editingProduct != null) {
    AddProductFormDialog(
      initialProduct = editingProduct,
      shops = shops,
      categories = categories,
      onDismiss = {
        showAddProductDialog = false
        editingProduct = null
      },
      onSave = { name, price, description, shopId, categoryId, emoji, mrp, stock, sku ->
        if (editingProduct != null) {
          viewModel.saveProduct(
            id = editingProduct?.id,
            name = name,
            categoryId = categoryId,
            emoji = emoji,
            price = price,
            mrp = mrp,
            stock = stock,
            description = description,
            sku = sku,
            status = if (stock == 0) "outofstock" else "active"
          )
        } else {
          val shop = shops.find { it.id == shopId }
          val sellerId = shop?.sellerId ?: "s1"
          viewModel.addProduct(
            name = name,
            price = price,
            description = description,
            shopId = shopId,
            categoryId = categoryId,
            emoji = emoji,
            mrp = mrp,
            stock = stock,
            sellerId = sellerId
          )
        }
        showAddProductDialog = false
        editingProduct = null
      }
    )
  }
}

/**
 * Clean Material 3 Card representing a single product in the list.
 */
@Composable
fun ProductListItemCard(
  product: ProductEntity,
  shopName: String,
  categoryName: String,
  onCardClick: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("product_list_item_${product.id}")
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Product Emoji Thumbnail
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
        contentAlignment = Alignment.Center
      ) {
        Text(product.emoji, fontSize = 30.sp)
      }

      // Details
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = product.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Text(
            text = "₹${product.price.toInt()}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = NavyPrimary,
            modifier = Modifier.padding(start = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = product.description,
          fontSize = 12.sp,
          color = MutedGray,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Metadata badges
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BlueAccent.copy(alpha = 0.09f)
          ) {
            Text(
              text = "🏪 $shopName",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = BlueAccent,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (product.stock > 0) GreenSuccess.copy(alpha = 0.1f) else RedDanger.copy(alpha = 0.1f)
          ) {
            Text(
              text = if (product.stock > 0) "${product.stock} in stock" else "Out of stock",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (product.stock > 0) GreenSuccess else RedDanger,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Action Buttons
      Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        IconButton(
          onClick = onEditClick,
          modifier = Modifier.size(36.dp).testTag("edit_product_${product.id}")
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BlueAccent, modifier = Modifier.size(18.dp))
        }

        IconButton(
          onClick = onDeleteClick,
          modifier = Modifier.size(36.dp).testTag("delete_product_${product.id}")
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedDanger, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}

/**
 * Interactive 'Add Product' / 'Edit Product' modal dialog form.
 * Includes fields for Name, Price, Description, Shop ID, Category, Emoji, Stock, and MRP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductFormDialog(
  initialProduct: ProductEntity? = null,
  shops: List<ShopEntity>,
  categories: List<CategoryEntity>,
  onDismiss: () -> Unit,
  onSave: (name: String, price: Double, description: String, shopId: String, categoryId: String, emoji: String, mrp: Double, stock: Int, sku: String) -> Unit
) {
  var name by remember { mutableStateOf(initialProduct?.name ?: "") }
  var priceStr by remember { mutableStateOf(initialProduct?.price?.toInt()?.toString() ?: "") }
  var mrpStr by remember { mutableStateOf(initialProduct?.mrp?.toInt()?.toString() ?: "") }
  var description by remember { mutableStateOf(initialProduct?.description ?: "") }
  var selectedShopId by remember { mutableStateOf(initialProduct?.shopId ?: shops.firstOrNull()?.id ?: "shop_1") }
  var selectedCategoryId by remember { mutableStateOf(initialProduct?.categoryId ?: categories.firstOrNull()?.id ?: "cat_grocery") }
  var emoji by remember { mutableStateOf(initialProduct?.emoji ?: "📦") }
  var stockStr by remember { mutableStateOf(initialProduct?.stock?.toString() ?: "15") }
  var sku by remember { mutableStateOf(initialProduct?.sku ?: "") }

  var nameError by remember { mutableStateOf<String?>(null) }
  var priceError by remember { mutableStateOf<String?>(null) }

  var shopExpanded by remember { mutableStateOf(false) }
  var categoryExpanded by remember { mutableStateOf(false) }

  val popularEmojis = listOf(
    "📦", "🍚", "🫘", "🛢️", "🍬", "🍵", "👚", "👖", "🥻", "👔", "🎧",
    "⌚", "🔋", "🧴", "🧼", "🥨", "🍫", "🍪", "🥔", "🍯", "🥖", "🥐"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 16.dp)
        .testTag("add_product_form_dialog")
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (initialProduct != null) "Edit Product" else "Add New Product",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              color = NavyPrimary
            )
            Text(
              text = "Fill in product information for Room database",
              fontSize = 11.sp,
              color = MutedGray
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MutedGray)
          }
        }

        HorizontalDivider(color = LineBorder)

        // Emoji Selector
        Text("Product Icon: $emoji", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          popularEmojis.forEach { em ->
            Surface(
              onClick = { emoji = em },
              shape = RoundedCornerShape(10.dp),
              color = if (emoji == em) BlueAccent else BgLight,
              border = androidx.compose.foundation.BorderStroke(1.dp, if (emoji == em) BlueAccent else LineBorder)
            ) {
              Text(em, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
            }
          }
        }

        // Product Name Input
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            if (it.isNotBlank()) nameError = null
          },
          label = { Text("Product Name *") },
          isError = nameError != null,
          supportingText = nameError?.let { { Text(it, color = RedDanger) } },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("product_name_input")
        )

        // Shop Dropdown Selector
        ExposedDropdownMenuBox(
          expanded = shopExpanded,
          onExpandedChange = { shopExpanded = !shopExpanded }
        ) {
          val currentShop = shops.find { it.id == selectedShopId }
          OutlinedTextField(
            value = currentShop?.let { "${it.emoji} ${it.name}" } ?: "Select Store",
            onValueChange = {},
            readOnly = true,
            label = { Text("Store / Shop ID *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("product_shop_dropdown")
          )
          ExposedDropdownMenu(
            expanded = shopExpanded,
            onDismissRequest = { shopExpanded = false }
          ) {
            shops.forEach { shop ->
              DropdownMenuItem(
                text = { Text("${shop.emoji} ${shop.name} (${shop.city})") },
                onClick = {
                  selectedShopId = shop.id
                  shopExpanded = false
                }
              )
            }
          }
        }

        // Category Dropdown Selector
        ExposedDropdownMenuBox(
          expanded = categoryExpanded,
          onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
          val currentCategory = categories.find { it.id == selectedCategoryId }
          OutlinedTextField(
            value = currentCategory?.let { "${it.emoji} ${it.name}" } ?: "Select Category",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("product_category_dropdown")
          )
          ExposedDropdownMenu(
            expanded = categoryExpanded,
            onDismissRequest = { categoryExpanded = false }
          ) {
            categories.forEach { category ->
              DropdownMenuItem(
                text = { Text("${category.emoji} ${category.name}") },
                onClick = {
                  selectedCategoryId = category.id
                  categoryExpanded = false
                }
              )
            }
          }
        }

        // Price & MRP Row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = priceStr,
            onValueChange = {
              priceStr = it
              if (it.isNotBlank()) priceError = null
            },
            label = { Text("Price (₹) *") },
            isError = priceError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("product_price_input")
          )

          OutlinedTextField(
            value = mrpStr,
            onValueChange = { mrpStr = it },
            label = { Text("MRP (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("product_mrp_input")
          )
        }
        if (priceError != null) {
          Text(priceError!!, color = RedDanger, fontSize = 11.sp)
        }

        // Stock and SKU Row
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = stockStr,
            onValueChange = { stockStr = it },
            label = { Text("Stock Qty") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("product_stock_input")
          )

          OutlinedTextField(
            value = sku,
            onValueChange = { sku = it },
            label = { Text("SKU Code") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("product_sku_input")
          )
        }

        // Description Input
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          minLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("product_description_input")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Cancel", color = NavyPrimary, fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = {
              var hasError = false
              if (name.isBlank()) {
                nameError = "Name cannot be empty"
                hasError = true
              }
              val price = priceStr.toDoubleOrNull()
              if (price == null || price <= 0.0) {
                priceError = "Enter valid price"
                hasError = true
              }

              if (!hasError && price != null) {
                val mrp = mrpStr.toDoubleOrNull() ?: price
                val stock = stockStr.toIntOrNull() ?: 10
                onSave(
                  name.trim(),
                  price,
                  description.trim(),
                  selectedShopId,
                  selectedCategoryId,
                  emoji,
                  mrp,
                  stock,
                  sku.trim()
                )
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            modifier = Modifier
              .weight(1f)
              .testTag("save_product_button")
          ) {
            Text(
              text = if (initialProduct != null) "Update" else "Save Product",
              fontWeight = FontWeight.Bold,
              color = CardWhite
            )
          }
        }
      }
    }
  }
}
