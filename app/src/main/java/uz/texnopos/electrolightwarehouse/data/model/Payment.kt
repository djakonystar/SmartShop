package uz.texnopos.electrolightwarehouse.data.model

data class Payment(
    val id:Int,
    val card:Double,
    val cash:Double,
    val debt:Double,
    val price:Double,
    val term: String,
    val description:String,
    val is_deleted:Boolean,
    val created_at:String
)
