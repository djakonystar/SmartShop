package uz.texnopos.elektrolife.ui.client.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentClientDetailBinding

class ClientDetailFragment : Fragment(R.layout.fragment_client_detail) {
    private lateinit var binding: FragmentClientDetailBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var adapter: ClientDetailAdapter
    private lateinit var navController: NavController
    private val args: ClientDetailFragmentArgs by navArgs()
    private lateinit var clientStr: String
    private lateinit var client: Client

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientDetailBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        clientStr = args.client
        client = Gson().fromJson(clientStr, Client::class.java)
        adapter = ClientDetailAdapter(requireActivity().supportFragmentManager, lifecycle, client)

        abBinding.apply {
            btnHome.onClick {
                navController.popBackStack()
            }
            tvTitle.text = client.name
        }

        binding.apply {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = context?.getString(R.string.purchases)
                    }
                    else -> {
                        tab.text = context?.getString(R.string.payments)
                    }
                }
            }.attach()
        }
    }
}
