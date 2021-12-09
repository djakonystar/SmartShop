package uz.texnopos.electrolightwarehouse.ui.newsale

import uz.texnopos.electrolightwarehouse.data.model.Product

class Basket {
    private var mutableProducts: MutableList<Product> = mutableListOf()
    val products: List<Product> get() = mutableProducts

    fun addProduct(product: Product, onComplete: (product: Product) -> Unit) {
        mutableProducts.forEachIndexed { index, p ->
            if (p.productId == product.productId) {
                mutableProducts[index].count
                onComplete.invoke(mutableProducts[index])
                return
            }
        }
        product.count = 1
        mutableProducts.add(product)
        onComplete.invoke(product)
    }
    fun setProduct(product: Product, count: Int, totalPrice:Int) {
        product.count = count
        product.salePrice = totalPrice
        mutableProducts.forEachIndexed { _, p ->
            if (p.productId == product.productId) {
                p.count = count
                p.salePrice = totalPrice
                return
            }
        }
        mutableProducts.add(product)
    }
}