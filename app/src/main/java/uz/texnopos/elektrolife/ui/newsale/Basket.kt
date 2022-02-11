package uz.texnopos.elektrolife.ui.newsale


import android.util.Log
import uz.texnopos.elektrolife.data.model.newsale.Product

class Basket {

    companion object {
        var mutableProducts: MutableList<Product> = mutableListOf()
        val products: List<Product> get() = mutableProducts

        fun addProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.productId == product.productId) {
                    if (mutableProducts[index].count < product.remained)
                        mutableProducts[index].count++
                    onComplete.invoke(mutableProducts[index])
                    return
                }
            }
            product.count = 1
            mutableProducts.add(product)
            onComplete.invoke(product)
        }

        fun minusProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.productId == product.productId) {
                    if (mutableProducts[index].count > 1)
                        mutableProducts[index].count--
                    onComplete.invoke(mutableProducts[index])
                    return
                }
            }
            product.count = 1
            mutableProducts.add(product)
            onComplete.invoke(product)
        }

        fun setProduct(product: Product, count: Int, totalPrice: Long) {
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

        fun deleteProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.productId == product.productId) {
                    mutableProducts[index].count
                    onComplete.invoke(mutableProducts[index])
                    return
                }
            }
            product.count = 0
            mutableProducts.remove(product)
            onComplete.invoke(product)
        }
    }
}