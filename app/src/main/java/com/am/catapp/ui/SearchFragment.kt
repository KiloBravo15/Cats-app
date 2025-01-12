package com.am.catapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.catapp.CatListAdapter
import com.am.catapp.R
import com.am.catapp.databinding.FragmentSearchBinding
import com.am.catapp.utils.CatSerialization
import com.am.catapp.utils.Constants
import com.am.catapp.viewmodels.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val catsViewModel = ViewModelProvider(this)[CatsViewModel::class.java]

        // Inflate the layout and initialize the RecyclerView and its adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CatListAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        val serializer = CatSerialization()
        // Passing data and redirection to details screen
        adapter.setListener(object : CatListAdapter.Listener {
            override fun onClick(position: Int) {
                val cat = catsViewModel.searchedBreeds.value?.get(position)
                val bundle = Bundle().apply {
                    putByteArray(
                        Constants.CAT_OBJ_KEY,
                        cat?.let { serializer.serializeCat(it) })
                }
                view?.findNavController()?.navigate(R.id.navigation_details_search, bundle)
            }
        })

        // Showing recycler view or message if list is empty
        catsViewModel.searchedBreeds.observe(viewLifecycleOwner) { cats ->
            if (cats.isNotEmpty()) {
                adapter.setData(cats)
                binding.iconTextView.visibility = View.GONE
            } else {
                adapter.setData(cats)
                binding.iconTextView.text =
                    getString(R.string.no_results_text, binding.searchView.query)
                binding.iconTextView.visibility = View.VISIBLE
            }
        }

        catsViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide the loading indicator based on the loading state
            binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Clear the screen
                binding.iconTextView.visibility = View.GONE
                // Perform the search
                catsViewModel.loadBreedsByName(query)
                // Close the keyboard
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}