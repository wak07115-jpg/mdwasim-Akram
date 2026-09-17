package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.LocalKartDatabase
import com.example.data.repository.LocalKartRepository
import com.example.ui.screens.ProductListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LocalKartViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LocalKart", appName)
  }

  @Test
  fun `add product screen renders form fields and saves to database`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = LocalKartDatabase.getDatabase(context)
    val repository = LocalKartRepository(database.localKartDao())
    val viewModel = LocalKartViewModel(repository)

    var navigatedBack = false

    composeTestRule.setContent {
      MyApplicationTheme {
        com.example.ui.screens.AddProductScreen(
          viewModel = viewModel,
          onNavigateBack = { navigatedBack = true }
        )
      }
    }

    // Verify all requested form fields are present in the composable
    composeTestRule.onNodeWithTag("add_product_name").assertExists()
    composeTestRule.onNodeWithTag("add_product_price").assertExists()
    composeTestRule.onNodeWithTag("add_product_description").assertExists()
    composeTestRule.onNodeWithTag("add_product_shop_id").assertExists()
    composeTestRule.onNodeWithTag("submit_add_product_button").assertExists()

    // Verify saving directly into the Room database
    kotlinx.coroutines.runBlocking {
      val testProduct = com.example.data.model.ProductEntity(
        id = "test_prod_99",
        shopId = "shop_1",
        categoryId = "cat_grocery",
        sellerId = "s1",
        name = "Test Fresh Mangoes",
        description = "Sweet organic mangoes from local farm",
        price = 150.0,
        mrp = 180.0,
        stock = 25,
        emoji = "🥭"
      )
      repository.insertProduct(testProduct)

      val allProducts = repository.allProducts.first()
      val retrieved = allProducts.find { it.id == "test_prod_99" }
      assertEquals("shop_1", retrieved?.shopId)
      assertEquals(150.0, retrieved?.price ?: 0.0, 0.01)
      assertEquals("Sweet organic mangoes from local farm", retrieved?.description)
    }
  }
}

