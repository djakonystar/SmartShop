package uz.texnopos.elektrolife.ui.newsale


import uz.texnopos.elektrolife.data.model.newsale.Product

class Basket {

    companion object {
        var mutableProducts: MutableList<Product> = mutableListOf()
        val products: List<Product> get() = mutableProducts

        fun addProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.id == product.id) {
                    if (mutableProducts[index].count < product.warehouse.count)
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
                if (p.id == product.id) {
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

        fun setProduct(product: Product, count: Int, totalPrice: Double) {
            product.count = count
            product.salePrice = totalPrice
            mutableProducts.forEachIndexed { _, p ->
                if (p.id == product.id) {
                    p.count = count
                    p.salePrice = totalPrice
                    return
                }
            }
            mutableProducts.add(product)
        }

        fun deleteProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.id == product.id) {
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