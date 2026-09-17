package com.example.data.model

object SeedData {
  val categories = listOf(
    CategoryEntity("cat_grocery", "Grocery", "🛒", "#16A34A"),
    CategoryEntity("cat_fashion", "Fashion", "👕", "#7C3AED"),
    CategoryEntity("cat_electronics", "Electronics", "📱", "#2563FF"),
    CategoryEntity("cat_beauty", "Beauty", "💄", "#EC4899"),
    CategoryEntity("cat_home", "Home & Kitchen", "🏠", "#F59E0B"),
    CategoryEntity("cat_snacks", "Snacks", "🍿", "#FF6B00"),
    CategoryEntity("cat_books", "Books & Stationery", "📚", "#0EA5E9"),
    CategoryEntity("cat_footwear", "Footwear", "👟", "#DC2626"),
    CategoryEntity("cat_pharma", "Pharmacy", "💊", "#16A34A"),
    CategoryEntity("cat_local", "Local Products", "🌾", "#84CC16")
  )

  val sellers = listOf(
    SellerEntity("s1", "Rajesh Sharma", "seller@demo.com", "demo123", "9876543210", "approved"),
    SellerEntity("s2", "Priya Verma", "priya@demo.com", "demo123", "9876543211", "approved"),
    SellerEntity("s3", "Amit Kumar", "amit@demo.com", "demo123", "9876543212", "approved"),
    SellerEntity("s4", "Sneha Patel", "sneha@demo.com", "demo123", "9876543213", "approved"),
    SellerEntity("s5", "Vikram Singh", "vikram@demo.com", "demo123", "9876543214", "approved"),
    SellerEntity("s6", "Anjali Rao", "anjali@demo.com", "demo123", "9876543215", "approved"),
    SellerEntity("s7", "Karan Mehta", "karan@demo.com", "demo123", "9876543216", "approved"),
    SellerEntity("s8", "Neha Gupta", "neha@demo.com", "demo123", "9876543217", "pending"),
    SellerEntity("s9", "Rohit Jain", "rohit@demo.com", "demo123", "9876543218", "pending"),
    SellerEntity("s10", "Divya Nair", "divya@demo.com", "demo123", "9876543219", "approved")
  )

  val shops = listOf(
    ShopEntity("sh1", "s1", "Sharma General Store", "cat_grocery", "🛒", "b1", "Your neighbourhood grocery with fresh staples and daily essentials.", "MG Road, Bengaluru", "Bengaluru", "Karnataka", "560001", 4.8f, true, true),
    ShopEntity("sh2", "s2", "Priya Fashion Hub", "cat_fashion", "👗", "b2", "Trendy ethnic and western wear for the modern woman.", "Linking Road, Mumbai", "Mumbai", "Maharashtra", "400050", 4.6f, true, true),
    ShopEntity("sh3", "s3", "TechPoint Electronics", "cat_electronics", "📱", "b3", "Gadgets, accessories and smart devices at honest prices.", "Connaught Place, Delhi", "Delhi", "Delhi", "110001", 4.7f, true, true),
    ShopEntity("sh4", "s4", "Glow Beauty Studio", "cat_beauty", "💄", "b4", "Premium skincare, makeup and self-care essentials.", "Banjara Hills, Hyderabad", "Hyderabad", "Telangana", "500034", 4.5f, true, false),
    ShopEntity("sh5", "s5", "Singh Snack Corner", "cat_snacks", "🍿", "b1", "Namkeen, bakery and munchies — straight from local makers.", "Sector 22, Chandigarh", "Chandigarh", "Chandigarh", "160022", 4.4f, true, false),
    ShopEntity("sh6", "s6", "HomeStyle Kitchen", "cat_home", "🍳", "b2", "Cookware, storage and everything your kitchen needs.", "Salt Lake, Kolkata", "Kolkata", "West Bengal", "700091", 4.3f, true, false),
    ShopEntity("sh7", "s7", "BookWorm Stationers", "cat_books", "📚", "b3", "Books, notebooks, art supplies and study essentials.", "College Street, Kolkata", "Kolkata", "West Bengal", "700073", 4.9f, true, true),
    ShopEntity("sh8", "s8", "Jain Footwear", "cat_footwear", "👟", "b4", "Comfortable footwear for every occasion.", "Johri Bazaar, Jaipur", "Jaipur", "Rajasthan", "302003", 4.2f, false, false),
    ShopEntity("sh9", "s9", "MedPlus Pharmacy", "cat_pharma", "💊", "b1", "Genuine medicines and wellness products.", "Anna Nagar, Chennai", "Chennai", "Tamil Nadu", "600040", 4.5f, false, false),
    ShopEntity("sh10", "s10", "Village Organic", "cat_local", "🌾", "b3", "Farm-fresh organic produce and regional specialties.", "Koregaon Park, Pune", "Pune", "Maharashtra", "411001", 4.9f, true, true)
  )

  val products = listOf(
    // Grocery (sh1)
    ProductEntity("p1", "s1", "sh1", "cat_grocery", "Basmati Rice 5kg", "🍚", 650.0, 750.0, 40, "Premium aged basmati rice, long grain and aromatic."),
    ProductEntity("p2", "s1", "sh1", "cat_grocery", "Toor Dal 1kg", "🫘", 160.0, 180.0, 60, "Polished toor dal, protein-rich everyday staple."),
    ProductEntity("p3", "s1", "sh1", "cat_grocery", "Sunflower Oil 1L", "🛢️", 180.0, 210.0, 35, "Refined sunflower oil, heart-healthy cooking."),
    ProductEntity("p4", "s1", "sh1", "cat_grocery", "Sugar 1kg", "🍬", 48.0, 52.0, 80, "Fine granulated white sugar."),
    ProductEntity("p5", "s1", "sh1", "cat_grocery", "Tea Powder 500g", "🍵", 240.0, 280.0, 25, "Strong Assam blend tea for perfect chai."),

    // Fashion (sh2)
    ProductEntity("p6", "s2", "sh2", "cat_fashion", "Cotton Kurti", "👚", 699.0, 1299.0, 18, "Breathable cotton kurti with elegant hand block print."),
    ProductEntity("p7", "s2", "sh2", "cat_fashion", "Denim Jeans", "👖", 899.0, 1499.0, 22, "Slim-fit denim jeans, durable stretchable fabric."),
    ProductEntity("p8", "s2", "sh2", "cat_fashion", "Silk Saree", "🥻", 2499.0, 3999.0, 8, "Traditional silk saree with intricate zari border."),
    ProductEntity("p9", "s2", "sh2", "cat_fashion", "Men's Formal Shirt", "👔", 599.0, 999.0, 30, "Pure cotton shirt with crisp formal finish."),
    ProductEntity("p10", "s2", "sh2", "cat_fashion", "Winter Jacket", "🧥", 1799.0, 2999.0, 12, "Warm padded jacket for chilly winter evenings."),

    // Electronics (sh3)
    ProductEntity("p11", "s3", "sh3", "cat_electronics", "Wireless Earbuds", "🎧", 1299.0, 2499.0, 45, "True wireless earbuds with 30hr battery and ENC."),
    ProductEntity("p12", "s3", "sh3", "cat_electronics", "Smart Fitness Watch", "⌚", 2499.0, 4999.0, 20, "AMOLED fitness smartwatch with SpO2 and heart rate monitor."),
    ProductEntity("p13", "s3", "sh3", "cat_electronics", "Power Bank 20000mAh", "🔋", 1199.0, 1799.0, 30, "Fast-charging power bank with dual USB-C ports."),
    ProductEntity("p14", "s3", "sh3", "cat_electronics", "Bluetooth Speaker", "🔊", 999.0, 1599.0, 25, "Portable waterproof speaker with rich bass."),
    ProductEntity("p15", "s3", "sh3", "cat_electronics", "Fast USB-C Cable 2m", "🔌", 199.0, 399.0, 100, "Braided nylon fast-charge cable, highly durable."),

    // Beauty (sh4)
    ProductEntity("p16", "s4", "sh4", "cat_beauty", "Hydrating Face Cream 50ml", "🧴", 349.0, 499.0, 40, "Deep hydrating face cream enriched with Vitamin E."),
    ProductEntity("p17", "s4", "sh4", "cat_beauty", "Matte Lipstick Set", "💋", 599.0, 999.0, 25, "Long-lasting matte finish lipstick collection, 4 shades."),
    ProductEntity("p18", "s4", "sh4", "cat_beauty", "Ayurvedic Hair Oil 200ml", "🫗", 249.0, 349.0, 50, "Enriched with Amla, Bhringraj and cold-pressed coconut oil."),
    ProductEntity("p19", "s4", "sh4", "cat_beauty", "Floral Bloom Perfume 100ml", "🌸", 899.0, 1499.0, 15, "Long-lasting sweet floral fragrance for daily wear."),
    ProductEntity("p20", "s4", "sh4", "cat_beauty", "Gentle Face Wash", "🧼", 199.0, 299.0, 60, "Sulfate-free foaming cleanser for radiant skin."),

    // Snacks (sh5)
    ProductEntity("p21", "s5", "sh5", "cat_snacks", "Namkeen Mixture 400g", "🥨", 120.0, 150.0, 70, "Crunchy traditional namkeen blend with roasted peanuts."),
    ProductEntity("p22", "s5", "sh5", "cat_snacks", "Artisanal Chocolate Box", "🍫", 299.0, 399.0, 40, "Assorted dark & milk chocolates with roasted hazelnuts."),
    ProductEntity("p23", "s5", "sh5", "cat_snacks", "Butter Cookies 500g", "🍪", 180.0, 220.0, 50, "Melt-in-mouth bakery butter cookies."),
    ProductEntity("p24", "s5", "sh5", "cat_snacks", "Potato Chips Salt & Pepper", "🥔", 60.0, 80.0, 80, "Handmade kettle cooked crispy potato chips."),
    ProductEntity("p25", "s5", "sh5", "cat_snacks", "Royal Dry Fruits Mix 250g", "🥜", 450.0, 599.0, 30, "Premium roasted almonds, cashews, raisins, and walnuts."),

    // Home & Kitchen (sh6)
    ProductEntity("p26", "s6", "sh6", "cat_home", "Steel Cookware Set 5pc", "🍲", 1899.0, 2999.0, 12, "Heavy gauge stainless steel induction-friendly pots & pans."),
    ProductEntity("p27", "s6", "sh6", "cat_home", "Opalware Dinner Set 24pc", "🍽️", 1299.0, 1999.0, 18, "Elegant microwave and dishwasher safe dinner set."),
    ProductEntity("p28", "s6", "sh6", "cat_home", "Insulated Water Bottle 1L", "🫙", 299.0, 499.0, 50, "Double-walled vacuum insulated flask, keeps 24h cold."),
    ProductEntity("p29", "s6", "sh6", "cat_home", "Airtight Food Containers 6pc", "🥡", 599.0, 899.0, 35, "BPA-free modular stackable kitchen containers."),
    ProductEntity("p30", "s6", "sh6", "cat_home", "Cotton Double Bed Sheet", "🛏️", 799.0, 1299.0, 22, "100% combed cotton bedsheet with 2 matching pillow covers."),

    // Books & Stationery (sh7)
    ProductEntity("p31", "s7", "sh7", "cat_books", "Bestselling Fiction Novel", "📖", 349.0, 499.0, 40, "Thrilling page-turner by acclaimed author, paperback."),
    ProductEntity("p32", "s7", "sh7", "cat_books", "Hardcover Journal Set 3pc", "📓", 299.0, 450.0, 80, "Thick bleed-proof lined notebooks with ribbon bookmark."),
    ProductEntity("p33", "s7", "sh7", "cat_books", "Gel Pen Luxury Box 10pc", "🖊️", 149.0, 249.0, 60, "Smooth-flow quick-drying waterproof gel pens."),
    ProductEntity("p34", "s7", "sh7", "cat_books", "Ergonomic Laptop Backpack 30L", "🎒", 899.0, 1499.0, 25, "Water-resistant commuter backpack with USB port."),
    ProductEntity("p35", "s7", "sh7", "cat_books", "Artist Color Pencils 36ct", "🖍️", 249.0, 399.0, 45, "Vibrant soft-core blending pencils in tin case."),

    // Footwear (sh8)
    ProductEntity("p36", "s8", "sh8", "cat_footwear", "Pro Cushion Running Shoes", "👟", 1299.0, 2499.0, 20, "Breathable mesh running trainers with responsive sole."),
    ProductEntity("p37", "s8", "sh8", "cat_footwear", "Classic Oxford Formal Shoes", "👞", 1499.0, 2499.0, 15, "Premium faux leather dress shoes with cushioned insole."),
    ProductEntity("p38", "s8", "sh8", "cat_footwear", "Comfort Leather Sandals", "🩴", 499.0, 799.0, 30, "Durable ergonomic arch support daily slip-ons."),
    ProductEntity("p39", "s8", "sh8", "cat_footwear", "Retro Casual Sneakers", "👟", 999.0, 1799.0, 25, "Vintage streetwear sneakers with vulcanized rubber sole."),
    ProductEntity("p40", "s8", "sh8", "cat_footwear", "Kids Active Sports Shoes", "👞", 599.0, 999.0, 18, "Lightweight velcro shoes for children ages 4-8."),

    // Pharmacy (sh9)
    ProductEntity("p41", "s9", "sh9", "cat_pharma", "Effervescent Vitamin C 60ct", "💊", 299.0, 399.0, 50, "Zinc & citrus immunity booster tablets."),
    ProductEntity("p42", "s9", "sh9", "cat_pharma", "Emergency First Aid Kit 80pc", "🩹", 499.0, 799.0, 20, "Comprehensive kit with bandages, gauze, antiseptic wipes."),
    ProductEntity("p43", "s9", "sh9", "cat_pharma", "Hand Sanitizer Gel 500ml", "🧴", 199.0, 299.0, 70, "70% alcohol moisturizing aloe vera sanitizer."),
    ProductEntity("p44", "s9", "sh9", "cat_pharma", "Infrared Digital Thermometer", "🌡️", 399.0, 699.0, 30, "Instant contactless forehead temperature scanner."),
    ProductEntity("p45", "s9", "sh9", "cat_pharma", "Protective Face Masks 50ct", "😷", 199.0, 349.0, 60, "3-layer certified breathable filtration masks."),

    // Local Products (sh10)
    ProductEntity("p46", "s10", "sh10", "cat_local", "Raw Forest Honey 500g", "🍯", 399.0, 549.0, 40, "Unfiltered pure mountain wildflower honey."),
    ProductEntity("p47", "s10", "sh10", "cat_local", "High-Curcumin Turmeric 250g", "🌿", 199.0, 299.0, 50, "Organic sun-dried stone-ground aromatic turmeric."),
    ProductEntity("p48", "s10", "sh10", "cat_local", "Cold Pressed Groundnut Oil 1L", "🫒", 549.0, 749.0, 25, "Traditional wood-churned unrefined pure oil."),
    ProductEntity("p49", "s10", "sh10", "cat_local", "Natural Jaggery Block 1kg", "🟫", 120.0, 160.0, 60, "Chemical-free sulfurless sugarcane jaggery."),
    ProductEntity("p50", "s10", "sh10", "cat_local", "Handloom Cotton Bath Towel", "🧵", 349.0, 499.0, 30, "Ultra-absorbent soft woven village artisan towel.")
  )

  val defaultCustomer = CustomerEntity(
    id = "c1",
    name = "Aarav Patel",
    email = "customer@demo.com",
    password = "demo123",
    phone = "9876500001",
    street = "Flat 402, Green Valley Apartments, Indiranagar",
    city = "Bengaluru",
    state = "Karnataka",
    pincode = "560001"
  )

  val coupons = listOf(
    CouponEntity("cp1", "WELCOME50", "fixed", 50.0, 299.0, 50.0, 1000, 120, System.currentTimeMillis() + 86400000L * 30),
    CouponEntity("cp2", "SAVE10", "percent", 10.0, 499.0, 200.0, 500, 45, System.currentTimeMillis() + 86400000L * 15),
    CouponEntity("cp3", "SHARMA20", "percent", 20.0, 300.0, 150.0, 200, 18, System.currentTimeMillis() + 86400000L * 7, "seller", "s1")
  )

  val initialOrders = listOf(
    OrderEntity(
      id = "ORD-1001",
      customerId = "c1",
      shopId = "sh1",
      sellerId = "s1",
      itemsJson = OrderJsonHelper.serialize(
        listOf(
          OrderItem("p1", "Basmati Rice 5kg", "🍚", 650.0, 1, "sh1", "s1"),
          OrderItem("p2", "Toor Dal 1kg", "🫘", 160.0, 2, "sh1", "s1")
        )
      ),
      subtotal = 970.0,
      delivery = 30.0,
      discount = 50.0,
      total = 950.0,
      status = "out_for_delivery",
      paymentMethod = "online",
      couponId = "cp1",
      street = "Flat 402, Green Valley Apartments, Indiranagar",
      city = "Bengaluru",
      state = "Karnataka",
      pincode = "560001",
      customerName = "Aarav Patel",
      customerPhone = "9876500001",
      createdAt = System.currentTimeMillis() - 86400000L * 2,
      updatedAt = System.currentTimeMillis() - 3600000L * 3
    ),
    OrderEntity(
      id = "ORD-1002",
      customerId = "c1",
      shopId = "sh3",
      sellerId = "s3",
      itemsJson = OrderJsonHelper.serialize(
        listOf(
          OrderItem("p11", "Wireless Earbuds", "🎧", 1299.0, 1, "sh3", "s3")
        )
      ),
      subtotal = 1299.0,
      delivery = 30.0,
      discount = 0.0,
      total = 1329.0,
      status = "delivered",
      paymentMethod = "cod",
      couponId = null,
      street = "Flat 402, Green Valley Apartments, Indiranagar",
      city = "Bengaluru",
      state = "Karnataka",
      pincode = "560001",
      customerName = "Aarav Patel",
      customerPhone = "9876500001",
      createdAt = System.currentTimeMillis() - 86400000L * 5,
      updatedAt = System.currentTimeMillis() - 86400000L * 4
    )
  )
}
