package com.am.catapp.ui

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.am.catapp.CatListAdapter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.am.catapp.R
import com.am.catapp.databinding.FragmentSavedBinding
import com.am.catapp.utils.CatSerialization
import com.am.catapp.utils.Constants
import com.am.catapp.viewmodels.CatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var catsViewModel: CatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        catsViewModel = ViewModelProvider(this)[CatsViewModel::class.java]

        // Inflate the layout and initialize the RecyclerView and it's adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CatListAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        val signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
            onSignInResult(res)
        }

        catsViewModel.currentUser.observe(viewLifecycleOwner) {
            binding.username.text = catsViewModel.currentUser.value?.email ?: getString(R.string.username_text)

            if (catsViewModel.currentUser.value != null) {     // user is logged in
                binding.login.text = getString(R.string.logout)

                binding.login.setOnClickListener {
                    // sign out
                    this.context?.let { it1 ->
                        AuthUI.getInstance()
                            .signOut(it1)
                    }?.addOnCompleteListener {
                        catsViewModel.currentUser.value = FirebaseAuth.getInstance().currentUser
                        Toast.makeText(this.context, getString(R.string.log_out_text), Toast.LENGTH_SHORT).show()

                        catsViewModel.getUserCats()
                    }
                }
            } else {                                            // user isn't logged in
                binding.login.text = getString(R.string.login)

                binding.login.setOnClickListener {
                    // Choose authentication providers
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build()
                    )
                    // Create and launch sign-in intent
                    val signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.Theme_CatApp)
                        .build()
                    signInLauncher.launch(signInIntent)
                }
            }
        }

        catsViewModel.getUserCats()

        val serializer = CatSerialization()
        // Passing data and redirection to details screen
        adapter.setListener(object : CatListAdapter.Listener {
            override fun onClick(position: Int) {
                val cat = catsViewModel.savedBreeds.value?.get(position)
                val bundle = Bundle().apply {
                    putByteArray(
                        Constants.CAT_OBJ_KEY,
                        cat?.let { serializer.serializeCat(it) })
                }
                view?.findNavController()?.navigate(R.id.navigation_details_saved, bundle)
            }
        })

        catsViewModel.savedBreeds.observe(viewLifecycleOwner) { cats ->
            adapter.setData(cats)
        }

        catsViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide the loading indicator based on the loading state
            binding.progressContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return root
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Toast.makeText(this.context, getString(R.string.log_in_text), Toast.LENGTH_SHORT).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Toast.makeText(this.context, getString(R.string.log_in_failed_text), Toast.LENGTH_SHORT).show()
        }
        catsViewModel.currentUser.value = FirebaseAuth.getInstance().currentUser
        catsViewModel.getUserCats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}