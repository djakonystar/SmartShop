package uz.texnopos.elektrolife.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.texnopos.elektrolife.data.retrofit.DynamicRetrofit
import uz.texnopos.elektrolife.data.retrofit.ImageRetrofit
import uz.texnopos.elektrolife.data.retrofit.PrefixRetrofit
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.client.ClientAdapter
import uz.texnopos.elektrolife.ui.client.ClientViewModel
import uz.texnopos.elektrolife.ui.client.detail.payment.ClientPaymentAdapter
import uz.texnopos.elektrolife.ui.client.detail.sale.ClientSalesViewModel
import uz.texnopos.elektrolife.ui.company.CompanyDetailsViewModel
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.dialog.TransactionViewModel
import uz.texnopos.elektrolife.ui.finance.FinanceDetailAdapter
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import uz.texnopos.elektrolife.ui.finance.reports.ReportsViewModel
import uz.texnopos.elektrolife.ui.finance.salary.SalaryAdapter
import uz.texnopos.elektrolife.ui.finance.salary.SalaryViewModel
import uz.texnopos.elektrolife.ui.finance.salary.detail.SalaryDetailAdapter
import uz.texnopos.elektrolife.ui.finance.salary.detail.SalaryDetailViewModel
import uz.texnopos.elektrolife.ui.newcategory.NewCategoryViewModel
import uz.texnopos.elektrolife.ui.newclient.NewClientViewModel
import uz.texnopos.elektrolife.ui.newpayment.NewPaymentViewModel
import uz.texnopos.elektrolife.ui.newproduct.ImageViewModel
import uz.texnopos.elektrolife.ui.newproduct.NewProductViewModel
import uz.texnopos.elektrolife.ui.newsale.CategoryViewModel
import uz.texnopos.elektrolife.ui.newsale.NewSaleProductAdapter
import uz.texnopos.elektrolife.ui.newsale.NewSaleViewModel
import uz.texnopos.elektrolife.ui.newsale.order.OrderAdapter
import uz.texnopos.elektrolife.ui.newsale.order.OrderViewModel
import uz.texnopos.elektrolife.ui.newsale.order.ReceiptAdapter
import uz.texnopos.elektrolife.ui.payment.PaymentViewModel
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerViewModel
import uz.texnopos.elektrolife.ui.sales.SalesAdapter
import uz.texnopos.elektrolife.ui.sales.SalesViewModel
import uz.texnopos.elektrolife.ui.sales.detail.OrderReceiptAdapter
import uz.texnopos.elektrolife.ui.sales.detail.SalesDetailAdapter
import uz.texnopos.elektrolife.ui.sales.detail.SalesDetailViewModel
import uz.texnopos.elektrolife.ui.settings.dialog.PrefixViewModel
import uz.texnopos.elektrolife.ui.signin.SignInViewModel
import uz.texnopos.elektrolife.ui.warehouse.EditProductViewModel
import uz.texnopos.elektrolife.ui.warehouse.WarehouseAdapter
import uz.texnopos.elektrolife.ui.warehouse.WarehouseViewModel

val networkModule = module {
    single { DynamicRetrofit() }
    factory { DynamicRetrofit().api }
    single { ImageRetrofit().api }
    single { PrefixRetrofit().api }
}

val helperModule = module {
    single { Settings(androidApplication().applicationContext) }
}

val viewModelModule = module {
    viewModel { CategoryViewModel(get(), get()) }
    viewModel { NewPaymentViewModel(get(), get()) }
    viewModel { SalesViewModel(get(), get()) }
    viewModel { NewCategoryViewModel(get(), get()) }
    viewModel { NewProductViewModel(get(), get()) }
    viewModel { ClientViewModel(get(), get()) }
    viewModel { WarehouseViewModel(get(), get()) }
    viewModel { SignInViewModel(get(), get(), get()) }
    viewModel { NewClientViewModel(get(), get()) }
    viewModel { OrderViewModel(get(), get()) }
    viewModel { CurrencyViewModel(get(), get()) }
    viewModel { FinanceViewModel(get(), get()) }
    viewModel { ReportsViewModel(get(), get()) }
    viewModel { ClientSalesViewModel(get(), get()) }
    viewModel { TransactionViewModel(get(), get()) }
    viewModel { SalesDetailViewModel(get(), get()) }
    viewModel { NewSaleViewModel(get(), get()) }
    viewModel { SalaryViewModel(get(), get()) }
    viewModel { SalaryDetailViewModel(get(), get()) }
    viewModel { PaymentViewModel(get(), get(), get()) }
    viewModel { QrScannerViewModel(get(), get()) }
    viewModel { ImageViewModel(get()) }
    viewModel { EditProductViewModel(get(), get()) }
    viewModel { PrefixViewModel(get()) }
    viewModel { CompanyDetailsViewModel(get(), get()) }
}

val adapterModule = module {
    single { ClientAdapter() }
    single { SalesAdapter(get()) }
    single { SalesDetailAdapter(get()) }
    single { WarehouseAdapter() }
    single { NewSaleProductAdapter() }
    single { OrderAdapter(get()) }
    single { FinanceDetailAdapter() }
    single { ClientPaymentAdapter(get()) }
    single { ReceiptAdapter(get()) }
    single { OrderReceiptAdapter(get()) }
    single { SalaryAdapter(get()) }
    single { SalaryDetailAdapter(get()) }
}
