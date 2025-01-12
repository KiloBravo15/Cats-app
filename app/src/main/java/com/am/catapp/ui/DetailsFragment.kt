package com.am.catapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.am.catapp.R
import com.bumptech.glide.Glide
import com.am.catapp.databinding.FragmentDetailsBinding
import com.am.catapp.models.Cat
import com.am.catapp.utils.CatSerialization
import com.am.catapp.utils.Constants
import com.am.catapp.viewmodels.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var catsViewModel: CatsViewModel

    private var cat: Cat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serializer = CatSerialization()
        cat =
            serializer.deserializeCat(requireArguments().getByteArray(Constants.CAT_OBJ_KEY))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        catsViewModel = ViewModelProvider(this)[CatsViewModel::class.java]

        // Set up the toolbar
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Show the back button
            setDisplayShowHomeEnabled(true) // Show the back button as an icon
            setDisplayShowTitleEnabled(false) // Don't show the title
        }
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // Handle back button click
        }

        // Redirect to website on click
        binding.linkButton.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cat?.breed?.wikiUrl))
            startActivity(viewIntent)
        }

        cat?.let { catsViewModel.runIfCatSaved(it.id,
            { setBookmarkCatSaved() }, { setBookmarkCatNotSaved() }) }

        setContent()

        return root
    }

    private fun setBookmarkCatSaved() {
        binding.bookmark.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.purple))
        binding.bookmark.visibility = View.VISIBLE
        // Remove the comic from saved
        binding.bookmark.setOnClickListener {
            cat?.let { it1 -> catsViewModel.removeCat(it1.id) }
            setBookmarkCatNotSaved()
        }
    }

    private fun setBookmarkCatNotSaved() {
        binding.bookmark.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.grey))
        binding.bookmark.visibility = View.VISIBLE
        // Save the comic
        binding.bookmark.setOnClickListener {
            cat?.let { it1 -> catsViewModel.addCat(it1) }
            setBookmarkCatSaved()
        }
    }

    private fun setContent() {
        binding.catName.text = cat?.breed?.name
        binding.catTemperament.text = cat?.breed?.temperament
        binding.catDescription.text = cat?.breed?.description
        if (cat?.imageUrl != null) {
            Glide.with(binding.root.context)
                .load(cat?.imageUrl)
                .into(binding.catImage)
        } else if (cat?.breed?.imageUrl != null) {
            Glide.with(binding.root.context)
                .load(cat?.breed?.imageUrl)
                .into(binding.catImage)
        }
    }
}
