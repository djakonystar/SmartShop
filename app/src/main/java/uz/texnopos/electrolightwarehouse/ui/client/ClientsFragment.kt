package uz.texnopos.electrolightwarehouse.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.dialPhone
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.ClientResponse
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentClientsBinding

class ClientsFragment : Fragment(R.layout.fragment_clients) {
    private lateinit var binding: FragmentClientsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val adapter: ClientsAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientsBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.clients)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        val list = listOf(
            ClientResponse(
                name = "Xakimov Jaqsibay",
                phone = "913941113",
                balance = -3000000,
                type = 0,
                comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            ),
            ClientResponse(
                name = "Sharapat Kalabaev",
                phone = "913021226",
                balance = -12000000,
                tin = 325632548,
                address = "Garezsizlik koshesi, 80/4",
                type = 1,
                comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            ),
            ClientResponse(
                name = "Embergenov Alibek",
                phone = "900957117",
                balance = 75000,
                type = 0,
                comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        )

        adapter.models = list

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                // TODO: Get clients from Backend
            }

            recyclerView.adapter = adapter

            etSearch.addTextChangedListener { text ->
                adapter.models = list.filter {
                    it.name.contains(text.toString(), true)
                            || it.phone.contains(text.toString())
                }
            }

            adapter.setOnItemClickListener { client ->
                val clientDetailDialogFragment = ClientDetailDialogFragment(client)
                clientDetailDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "ClientDetail"
                )
            }

            adapter.setOnPhoneClickListener { phone ->
                phone.dialPhone(requireActivity())
            }

            adapter.setOnPaymentClickListener { client ->
                Snackbar.make(recyclerView, "Payment clicked", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }
}
