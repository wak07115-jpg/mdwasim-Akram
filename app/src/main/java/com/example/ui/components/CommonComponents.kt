package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserRole
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalKartTopBar(
  title: String = "LocalKart",
  currentRole: UserRole,
  onRoleSwitch: (UserRole) -> Unit,
  cartCount: Int = 0,
  onCartClick: () -> Unit = {},
  showBack: Boolean = false,
  onBackClick: () -> Unit = {}
) {
  var showRoleDialog by remember { mutableStateOf(false) }

  TopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = NavyPrimary,
      titleContentColor = CardWhite,
      navigationIconContentColor = CardWhite,
      actionIconContentColor = CardWhite
    ),
    navigationIcon = {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("topbar_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
      } else {
        Box(
          modifier = Modifier
            .padding(start = 12.dp)
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(BlueAccent, PurpleAccent))),
          contentAlignment = Alignment.Center
        ) {
          Text("LK", color = CardWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
      }
    },
    title = {
      Column {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = CardWhite,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = when (currentRole) {
            UserRole.CUSTOMER -> "Customer Mode"
            UserRole.SELLER -> "Seller Portal"
            UserRole.ADMIN -> "Admin Console"
          },
          fontSize = 11.sp,
          color = CardWhite.copy(alpha = 0.7f),
          fontWeight = FontWeight.Medium
        )
      }
    },
    actions = {
      // Role Switcher Pill
      Surface(
        onClick = { showRoleDialog = true },
        shape = RoundedCornerShape(20.dp),
        color = CardWhite.copy(alpha = 0.15f),
        modifier = Modifier
          .padding(end = 6.dp)
          .testTag("role_switcher_pill")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = when (currentRole) {
              UserRole.CUSTOMER -> "🛍️ Buyer"
              UserRole.SELLER -> "🏪 Seller"
              UserRole.ADMIN -> "👑 Admin"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CardWhite
          )
          Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = "Switch Role",
            modifier = Modifier.size(16.dp),
            tint = CardWhite
          )
        }
      }

      // Cart Badge (visible in customer mode)
      if (currentRole == UserRole.CUSTOMER) {
        IconButton(
          onClick = onCartClick,
          modifier = Modifier.testTag("topbar_cart_button")
        ) {
          BadgedBox(
            badge = {
              if (cartCount > 0) {
                Badge(
                  containerColor = OrangeAccent,
                  contentColor = CardWhite
                ) {
                  Text(cartCount.toString(), fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = CardWhite)
          }
        }
      }
    }
  )

  if (showRoleDialog) {
    RolePickerDialog(
      currentRole = currentRole,
      onSelect = {
        onRoleSwitch(it)
        showRoleDialog = false
      },
      onDismiss = { showRoleDialog = false }
    )
  }
}

@Composable
fun RolePickerDialog(
  currentRole: UserRole,
  onSelect: (UserRole) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = CardWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("role_picker_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "Switch Role / Persona",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = NavyPrimary
        )
        Text(
          "Experience LocalKart from any perspective:",
          fontSize = 13.sp,
          color = MutedGray,
          modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        RoleOptionItem(
          role = UserRole.CUSTOMER,
          title = "Customer (Aarav Patel)",
          subtitle = "Browse shops, add to cart, apply coupons, place orders",
          emoji = "🛍️",
          isSelected = currentRole == UserRole.CUSTOMER,
          onClick = { onSelect(UserRole.CUSTOMER) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        RoleOptionItem(
          role = UserRole.SELLER,
          title = "Seller (Rajesh Sharma)",
          subtitle = "Sharma General Store — manage stock, fulfill orders",
          emoji = "🏪",
          isSelected = currentRole == UserRole.SELLER,
          onClick = { onSelect(UserRole.SELLER) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        RoleOptionItem(
          role = UserRole.ADMIN,
          title = "Platform Admin",
          subtitle = "Approve sellers, configure commissions, view platform GMV",
          emoji = "👑",
          isSelected = currentRole == UserRole.ADMIN,
          onClick = { onSelect(UserRole.ADMIN) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel", color = NavyPrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
fun RoleOptionItem(
  role: UserRole,
  title: String,
  subtitle: String,
  emoji: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) BlueAccent.copy(alpha = 0.08f) else BgLight,
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueAccent) else androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(emoji, fontSize = 28.sp)
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Text(subtitle, fontSize = 11.sp, color = MutedGray, lineHeight = 15.sp)
      }
      if (isSelected) {
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(BlueAccent),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Check, contentDescription = "Active", tint = CardWhite, modifier = Modifier.size(14.dp))
        }
      }
    }
  }
}

@Composable
fun ProductGridCard(
  product: ProductEntity,
  shopName: String,
  onProductClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val discountPercent = if (product.mrp > product.price) {
    (((product.mrp - product.price) / product.mrp) * 100).toInt()
  } else 0

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .clickable { onProductClick() }
      .testTag("product_card_${product.id}")
  ) {
    Column {
      // Product Image Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFF5F3FF)))),
        contentAlignment = Alignment.Center
      ) {
        Text(product.emoji, fontSize = 54.sp)

        if (discountPercent > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(OrangeAccent)
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              "$discountPercent% OFF",
              color = CardWhite,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Details
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = product.name,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          minLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "🏪 $shopName",
          fontSize = 11.sp,
          color = MutedGray,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "₹${product.price.toInt()}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = NavyPrimary
          )
          if (product.mrp > product.price) {
            Text(
              text = "₹${product.mrp.toInt()}",
              fontSize = 11.sp,
              color = MutedGray,
              textDecoration = TextDecoration.LineThrough
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onAddToCart,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
          contentPadding = PaddingValues(vertical = 8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_to_cart_btn_${product.id}")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun ShopItemCard(
  shop: ShopEntity,
  productCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bannerGradient = when (shop.banner) {
    "b2" -> Brush.linearGradient(listOf(OrangeAccent, Color(0xFFFF3D7F)))
    "b3" -> Brush.linearGradient(listOf(GreenSuccess, Color(0xFF0EA5E9)))
    "b4" -> Brush.linearGradient(listOf(PurpleAccent, Color(0xFFEC4899)))
    else -> Brush.linearGradient(listOf(BlueAccent, PurpleAccent))
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .clickable { onClick() }
      .testTag("shop_card_${shop.id}")
  ) {
    Column {
      // Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(75.dp)
          .background(bannerGradient)
      ) {
        // Logo
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 12.dp)
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .border(2.dp, CardWhite, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(shop.emoji, fontSize = 22.sp)
        }

        if (shop.isFeatured) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(CardWhite.copy(alpha = 0.9f))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text("★ Featured", color = PurpleAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Body
      Column(modifier = Modifier.padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)) {
        Text(
          text = shop.name,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = TextDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "${shop.city} • $productCount items",
          fontSize = 11.sp,
          color = MutedGray
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(Icons.Default.Star, contentDescription = "Rating", tint = AmberWarn, modifier = Modifier.size(13.dp))
            Text(
              text = if (shop.rating > 0) String.format("%.1f", shop.rating) else "New",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (shop.isOpen) GreenSuccess.copy(alpha = 0.1f) else RedDanger.copy(alpha = 0.1f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (shop.isOpen) "Open" else "Closed",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (shop.isOpen) GreenSuccess else RedDanger
            )
          }
        }
      }
    }
  }
}

@Composable
fun QuantityStepper(
  qty: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(BgLight)
      .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
  ) {
    IconButton(onClick = onDecrement, modifier = Modifier.size(36.dp)) {
      Icon(Icons.Default.Remove, contentDescription = "Minus", modifier = Modifier.size(16.dp), tint = NavyPrimary)
    }
    Text(
      text = qty.toString(),
      fontWeight = FontWeight.ExtraBold,
      fontSize = 14.sp,
      color = TextDark,
      modifier = Modifier.padding(horizontal = 10.dp)
    )
    IconButton(onClick = onIncrement, modifier = Modifier.size(36.dp)) {
      Icon(Icons.Default.Add, contentDescription = "Plus", modifier = Modifier.size(16.dp), tint = NavyPrimary)
    }
  }
}

@Composable
fun OrderStatusBadge(status: String) {
  val (color, label) = when (status) {
    "pending" -> AmberWarn to "Order Placed"
    "confirmed" -> PurpleAccent to "Confirmed"
    "packed" -> BlueAccent to "Packed"
    "shipped" -> BlueAccent to "Shipped"
    "out_for_delivery" -> PurpleAccent to "Out for Delivery"
    "delivered" -> GreenSuccess to "Delivered"
    "cancelled" -> RedDanger to "Cancelled"
    else -> MutedGray to status
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(color.copy(alpha = 0.12f))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
  }
}

@Composable
fun EmptyStateCard(
  emoji: String,
  title: String,
  description: String,
  actionText: String? = null,
  onAction: (() -> Unit)? = null
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(emoji, fontSize = 56.sp)
    Spacer(modifier = Modifier.height(12.dp))
    Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = NavyPrimary)
    Spacer(modifier = Modifier.height(4.dp))
    Text(description, fontSize = 13.sp, color = MutedGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    if (actionText != null && onAction != null) {
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onAction,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
      ) {
        Text(actionText, fontWeight = FontWeight.Bold)
      }
    }
  }
}
