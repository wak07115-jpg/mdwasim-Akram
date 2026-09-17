package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ShopEntity
import com.example.ui.theme.BgLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardWhite
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LineBorder
import com.example.ui.theme.MutedGray
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.LocalKartViewModel

/**
 * A dedicated Jetpack Compose screen for the 'Add Product' form.
 * Directly integrates with [LocalKartViewModel] to persist products into the Room database.
 */
@Composable
fun AddProductScreen(
  viewModel: LocalKartViewModel,
  onNavigateBack: () -> Unit = {},
  onProductAdded: () -> Unit = onNavigateBack,
  modifier: Modifier = Modifier
) {
  val shops by viewModel.shops.collectAsState()
  val categories by viewModel.categories.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      Surface(
        color = CardWhite,
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("add_product_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Products",
              tint = NavyPrimary
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Add New Product",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              color = NavyPrimary
            )
            Text(
              text = "Create and save item to Room catalog",
              fontSize = 12.sp,
              color = MutedGray
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(BgLight)
        .padding(innerPadding)
    ) {
      AddProductForm(
        shops = shops,
        categories = categories,
        onSaveProduct = { name, price, description, shopId, categoryId, emoji, mrp, stock, sku ->
          val associatedShop = shops.find { it.id == shopId }
          val sellerId = associatedShop?.sellerId ?: "s1"
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
          onProductAdded()
        },
        onCancel = onNavigateBack,
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

/**
 * Reusable Add Product Form composable with dedicated fields for:
 * - name: String (Product title)
 * - price: Double (Selling price)
 * - description: String (Details, specs, ingredients)
 * - shopId: String (Direct text input + quick shop selector)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductForm(
  shops: List<ShopEntity>,
  categories: List<CategoryEntity>,
  onSaveProduct: (
    name: String,
    price: Double,
    description: String,
    shopId: String,
    categoryId: String,
    emoji: String,
    mrp: Double,
    stock: Int,
    sku: String
  ) -> Unit,
  onCancel: () -> Unit = {},
  initialShopId: String? = null,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  // Form Fields State
  var name by remember { mutableStateOf("") }
  var priceText by remember { mutableStateOf("") }
  var mrpText by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var shopId by remember {
    mutableStateOf(initialShopId ?: shops.firstOrNull()?.id ?: "shop_1")
  }
  var selectedCategoryId by remember {
    mutableStateOf(categories.firstOrNull()?.id ?: "cat_grocery")
  }
  var emoji by remember { mutableStateOf("📦") }
  var stockText by remember { mutableStateOf("20") }
  var sku by remember { mutableStateOf("") }

  // Error States
  var nameError by remember { mutableStateOf<String?>(null) }
  var priceError by remember { mutableStateOf<String?>(null) }
  var descriptionError by remember { mutableStateOf<String?>(null) }
  var shopIdError by remember { mutableStateOf<String?>(null) }

  var categoryDropdownExpanded by remember { mutableStateOf(false) }

  val popularEmojis = listOf(
    "📦", "🍚", "🫘", "🛢️", "🍬", "🍵", "👚", "👖", "🥻", "👔", "🎧",
    "⌚", "🔋", "🧴", "🧼", "🥨", "🍫", "🍪", "🥔", "🍯", "🥖", "🥐", "📱", "💻"
  )

  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Informational Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = BlueAccent.copy(alpha = 0.08f)),
      border = androidx.compose.foundation.BorderStroke(1.dp, BlueAccent.copy(alpha = 0.2f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = BlueAccent,
          modifier = Modifier.size(22.dp)
        )
        Column {
          Text(
            text = "Room Database Catalog Record",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = NavyPrimary
          )
          Text(
            text = "Enter product details to insert into the local Room database table.",
            fontSize = 12.sp,
            color = TextDark.copy(alpha = 0.8f)
          )
        }
      }
    }

    // Section 1: Basic Information
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Basic Information",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = NavyPrimary
        )

        // Emoji Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Product Avatar Icon: $emoji",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            popularEmojis.forEach { em ->
              val isSelected = emoji == em
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) OrangeAccent.copy(alpha = 0.15f) else BgLight)
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) OrangeAccent else LineBorder,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable { emoji = em },
                contentAlignment = Alignment.Center
              ) {
                Text(em, fontSize = 20.sp)
              }
            }
          }
        }

        // Product Name Field
        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            if (it.isNotBlank()) nameError = null
          },
          label = { Text("Product Name *") },
          placeholder = { Text("e.g., Organic Basmati Rice 5kg") },
          leadingIcon = {
            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = BlueAccent)
          },
          isError = nameError != null,
          supportingText = nameError?.let { { Text(it, color = RedDanger) } },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
          keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueAccent,
            unfocusedBorderColor = LineBorder
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_product_name")
        )

        // Product Description Field
        OutlinedTextField(
          value = description,
          onValueChange = {
            description = it
            if (it.isNotBlank()) descriptionError = null
          },
          label = { Text("Product Description *") },
          placeholder = { Text("Detailed product description, key features, or ingredients...") },
          leadingIcon = {
            Icon(Icons.Default.Description, contentDescription = null, tint = BlueAccent)
          },
          isError = descriptionError != null,
          supportingText = descriptionError?.let { { Text(it, color = RedDanger) } },
          minLines = 3,
          maxLines = 5,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
          keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueAccent,
            unfocusedBorderColor = LineBorder
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_product_description")
        )
      }
    }

    // Section 2: Storefront / Shop ID Assignment
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Store Assignment",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = NavyPrimary
        )

        // Shop ID Text Field
        OutlinedTextField(
          value = shopId,
          onValueChange = {
            shopId = it
            if (it.isNotBlank()) shopIdError = null
          },
          label = { Text("Shop ID *") },
          placeholder = { Text("e.g., shop_1, shop_2") },
          leadingIcon = {
            Icon(Icons.Default.Store, contentDescription = null, tint = BlueAccent)
          },
          isError = shopIdError != null,
          supportingText = {
            if (shopIdError != null) {
              Text(shopIdError!!, color = RedDanger)
            } else {
              val currentShop = shops.find { it.id == shopId }
              if (currentShop != null) {
                Text("Assigned Store: ${currentShop.emoji} ${currentShop.name} (${currentShop.city})", color = GreenSuccess)
              } else {
                Text("Enter existing or new shop identifier", color = MutedGray)
              }
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
          keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueAccent,
            unfocusedBorderColor = LineBorder
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_product_shop_id")
        )

        // Quick Shop Selection Chips
        if (shops.isNotEmpty()) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "Quick select available stores:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = MutedGray
            )
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              shops.forEach { s ->
                val isSelected = shopId == s.id
                Surface(
                  onClick = {
                    shopId = s.id
                    shopIdError = null
                  },
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) BlueAccent else BgLight,
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) BlueAccent else LineBorder
                  )
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(s.emoji, fontSize = 13.sp)
                    Text(
                      text = s.name,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) CardWhite else TextDark
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Section 3: Pricing, Inventory & Category
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Pricing & Inventory",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = NavyPrimary
        )

        // Price & MRP Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Price Text Field
          OutlinedTextField(
            value = priceText,
            onValueChange = {
              priceText = it
              if (it.isNotBlank()) priceError = null
            },
            label = { Text("Price (₹) *") },
            placeholder = { Text("199") },
            leadingIcon = {
              Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = BlueAccent)
            },
            isError = priceError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("add_product_price")
          )

          // MRP Text Field
          OutlinedTextField(
            value = mrpText,
            onValueChange = { mrpText = it },
            label = { Text("MRP (₹)") },
            placeholder = { Text("249") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("add_product_mrp")
          )
        }
        if (priceError != null) {
          Text(priceError!!, color = RedDanger, fontSize = 11.sp)
        }

        // Category Selection Dropdown
        ExposedDropdownMenuBox(
          expanded = categoryDropdownExpanded,
          onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
        ) {
          val activeCategory = categories.find { it.id == selectedCategoryId }
          OutlinedTextField(
            value = activeCategory?.let { "${it.emoji} ${it.name}" } ?: "Select Category",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category Taxonomy") },
            leadingIcon = {
              Icon(Icons.Default.Category, contentDescription = null, tint = BlueAccent)
            },
            trailingIcon = {
              ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("add_product_category")
          )
          ExposedDropdownMenu(
            expanded = categoryDropdownExpanded,
            onDismissRequest = { categoryDropdownExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text("${cat.emoji} ${cat.name}") },
                onClick = {
                  selectedCategoryId = cat.id
                  categoryDropdownExpanded = false
                }
              )
            }
          }
        }

        // Stock & SKU Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = stockText,
            onValueChange = { stockText = it },
            label = { Text("Initial Stock") },
            leadingIcon = {
              Icon(Icons.Default.Inventory, contentDescription = null, tint = BlueAccent)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("add_product_stock")
          )

          OutlinedTextField(
            value = sku,
            onValueChange = { sku = it },
            label = { Text("SKU / Barcode") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BlueAccent,
              unfocusedBorderColor = LineBorder
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("add_product_sku")
          )
        }
      }
    }

    // Submit & Cancel Action Buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onCancel,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("cancel_add_product_button")
      ) {
        Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }

      Button(
        onClick = {
          var isValid = true

          if (name.isBlank()) {
            nameError = "Product name cannot be empty"
            isValid = false
          }

          val priceVal = priceText.toDoubleOrNull()
          if (priceVal == null || priceVal <= 0.0) {
            priceError = "Enter a valid positive price"
            isValid = false
          }

          if (description.isBlank()) {
            descriptionError = "Product description cannot be empty"
            isValid = false
          }

          if (shopId.isBlank()) {
            shopIdError = "Shop ID cannot be empty"
            isValid = false
          }

          if (isValid && priceVal != null) {
            val mrpVal = mrpText.toDoubleOrNull() ?: priceVal
            val stockVal = stockText.toIntOrNull() ?: 10
            onSaveProduct(
              name.trim(),
              priceVal,
              description.trim(),
              shopId.trim(),
              selectedCategoryId,
              emoji,
              mrpVal,
              stockVal,
              sku.trim()
            )
          }
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
        modifier = Modifier
          .weight(1.3f)
          .height(52.dp)
          .testTag("submit_add_product_button")
      ) {
        Icon(
          imageVector = Icons.Default.Save,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Save to Catalog",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = CardWhite
        )
      }
    }
  }
}
