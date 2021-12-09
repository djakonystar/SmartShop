package uz.texnopos.electrolightwarehouse.data.model

data class Product(
    var category_id:String,
    var product_name:String,
    var product_cost_price:String,
    var product_brand:String,
    var product_image:String,
    var count:Int,
    var price_wholesale:Long,
    var price_min:Long,
    var price_max:Long
)