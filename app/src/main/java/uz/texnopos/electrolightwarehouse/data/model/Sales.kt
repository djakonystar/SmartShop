package uz.texnopos.electrolightwarehouse.data.model

data class Sales(
    var client_id:Int,
    var client_name:String,
    var phone:String,
    var basket:List<Payment>,
    var orders:List<ProductInfo>
)
