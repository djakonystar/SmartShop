package uz.texnopos.elektrolife.ui.newsale


import uz.texnopos.elektrolife.data.model.newsale.Product

class Basket {

    companion object {
        private var mutableProducts: MutableList<Product> = mutableListOf()
        val products: List<Product> = mutableProducts

        fun addProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.id == product.id) {
                    if ((mutableProducts[index].count as Int) < product.warehouse?.count ?: 0.0)
                        // increment
                        mutableProducts[index].count
                    onComplete.invoke(mutableProducts[index])
                    return
                }
            }
            product.count = 1.0
            mutableProducts.add(product)
            onComplete.invoke(product)
        }

        fun minusProduct(product: Product, onComplete: (product: Product) -> Unit) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.id == product.id) {
                    if (mutableProducts[index].count as Int > 1)
                        // decrement
                        mutableProducts[index].count
                    onComplete.invoke(mutableProducts[index])
                    return
                }
            }
            product.count = 1.0
            mutableProducts.add(product)
            onComplete.invoke(product)
        }

        fun setProduct(product: Product, count: Double, salePrice: Double) {
            product.count = count
            product.salePrice = salePrice
            mutableProducts.forEach { p ->
                if (p.id == product.id) {
                    p.count = count
                    p.salePrice = salePrice
                    return
                }
            }
            mutableProducts.add(product)
        }

        fun deleteProduct(product: Product) {
            mutableProducts.forEachIndexed { index, p ->
                if (p.id == product.id) {
                    mutableProducts.removeAt(index)
                    return
                }
            }
        }

        fun clear() {
            mutableProducts.clear()
        }
    }
}