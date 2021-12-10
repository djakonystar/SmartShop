package uz.texnopos.electrolightwarehouse.data.model

data class Payment(
    val id:Int,
    val card: Long,
    val cash: Long,
    val debt: Long,
    val price:Double,
    val term: String,
    val description:String,
    val is_deleted:Boolean,
    val created_at:String
)
