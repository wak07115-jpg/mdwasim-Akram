package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

object OrderJsonHelper {
  fun serialize(items: List<OrderItem>): String {
    val array = JSONArray()
    for (item in items) {
      val obj = JSONObject().apply {
        put("productId", item.productId)
        put("name", item.name)
        put("emoji", item.emoji)
        put("price", item.price)
        put("qty", item.qty)
        put("shopId", item.shopId)
        put("sellerId", item.sellerId)
      }
      array.put(obj)
    }
    return array.toString()
  }

  fun deserialize(json: String): List<OrderItem> {
    val list = mutableListOf<OrderItem>()
    if (json.isBlank()) return list
    try {
      val array = JSONArray(json)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          OrderItem(
            productId = obj.optString("productId"),
            name = obj.optString("name"),
            emoji = obj.optString("emoji", "📦"),
            price = obj.optDouble("price", 0.0),
            qty = obj.optInt("qty", 1),
            shopId = obj.optString("shopId"),
            sellerId = obj.optString("sellerId")
          )
        )
      }
    } catch (_: Exception) {
    }
    return list
  }
}
