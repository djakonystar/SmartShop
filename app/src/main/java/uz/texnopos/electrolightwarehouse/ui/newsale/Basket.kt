package uz.texnopos.electrolightwarehouse.ui.newsale

import uz.texnopos.electrolightwarehouse.data.model.Product

class Basket {
    private var mutableProducts: MutableList<Product> = mutableListOf()
    val products: List<Product> get() = mutableProducts

    fun addProduct(product: Product, onComplete: (product: Product) -> Unit) {
        mutableProducts.forEachIndexed { index, p ->
            if (p.category_id == product.category_id) {
                mutableProducts[index].count++
                onComplete.invoke(mutableProducts[index])
                return
            }
        }
        product.count = 1
        mutableProducts.add(product)
        onComplete.invoke(product)
    }

    fun removeProduct(product: Product, onComplete: (product: Product) -> Unit, onRemoved: (product: Product)-> Unit) {
        mutableProducts.forEachIndexed { index, p ->
            if (p.category_id == product.category_id) {
                if (p.count == 1) {
                    mutableProducts.remove(p)
                    onRemoved.invoke(product)
                } else {
                    mutableProducts[index].count--
                    onComplete.invoke(mutableProducts[index])
                }
                return
            }
        }
    }

    fun setProduct(product: Product, count: Int, totalPrice:String) {
        product.count = count
        product.product_cost_price = totalPrice
        mutableProducts.forEachIndexed { index, p ->
            if (p.category_id == product.category_id) {
                p.count = count
                p.product_cost_price = totalPrice
                return
            }
        }
        mutableProducts.add(product)
    }
}