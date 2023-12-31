package com.example.kelineyt.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelineyt.R
import com.example.kelineyt.activities.ShoppingActivity
import com.example.kelineyt.adapters.ProductColorsAdapter
import com.example.kelineyt.adapters.ProductImagesAdapter
import com.example.kelineyt.adapters.ProductSizesAdapter
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.databinding.FragmentProductDetailsBinding
import com.example.kelineyt.util.Resource
import com.example.kelineyt.util.hideBottomNavigationView
import com.example.kelineyt.viewmodel.ProductDetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment: Fragment(R.layout.fragment_product_details) {
    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val imageAdapter by lazy { ProductImagesAdapter() }
    private val sizesAdapter by lazy { ProductSizesAdapter() }
    private val colorsAdapter by lazy { ProductColorsAdapter() }
    private val viewModel by viewModels<ProductDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupImageRv()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }

        imageAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }

        sizesAdapter.onItemClick = {
            viewModel.setSelectionSize(it)
        }

        colorsAdapter.onItemClick = {
            viewModel.setSelectionColor(it)
        }

        binding.imageClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(product)
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectionColor.collectLatest {
                    colorsAdapter.setSelection(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectionSize.collectLatest {
                    sizesAdapter.setSelection(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCart.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation()
                            viewModel.showSnackbar("Berhasil menambahkan barang")
                        }

                        is Resource.Error -> {
                            binding.buttonAddToCart.stopAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.snackbar.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupImageRv() {
        binding.apply {
            viewPagerProductImages.adapter = imageAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}