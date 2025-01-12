package com.am.catapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.catapp.CatListAdapter
import com.am.catapp.R
import com.am.catapp.databinding.FragmentHomeBinding
import com.am.catapp.utils.CatSerialization
import com.am.catapp.utils.Constants
import com.am.catapp.viewmodels.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var catsViewModel: CatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        catsViewModel = ViewModelProvider(this)[CatsViewModel::class.java]

        // Inflate the layout and initialize the RecyclerView and it's adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CatListAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        val serializer = CatSerialization()
        // Passing data and redirection to details screen
        adapter.setListener(object : CatListAdapter.Listener {
            override fun onClick(position: Int) {
                val cat = catsViewModel.randomCatImages.value?.get(position)
                val bundle = Bundle().apply {
                    putByteArray(
                        Constants.CAT_OBJ_KEY,
                        cat?.let { serializer.serializeCat(it) })
                }
                view?.findNavController()?.navigate(R.id.navigation_details_home, bundle)
            }
        })

        catsViewModel.randomCatImages.observe(viewLifecycleOwner) { cats ->
            adapter.setData(cats)
        }

        catsViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide the loading indicator based on the loading state
            binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}