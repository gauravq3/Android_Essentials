package com.master.androidessentials.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.androidessentials.R
import com.master.androidessentials.databinding.FragmentHomeBinding
import com.master.androidessentials.databinding.ItemViewBinding
import com.master.androidessentials.ui.base.BaseFragment
import com.master.androidessentials.adapters.GenericAdapter
import com.master.androidessentials.models.userslist.User
import com.master.androidessentials.networking.ApiResponse
import com.master.androidessentials.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
     val viewmodel: SharedViewModel by activityViewModels()
    lateinit var mAdapter: GenericAdapter<User, ItemViewBinding>
    lateinit var postItems: List<User>

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postItems = emptyList<User>()

        mAdapter = GenericAdapter(postItems,
            { inflater -> ItemViewBinding.inflate(inflater) },
            { binding, item ->
                binding.titleText.text = "${item.firstName} -${item.email}"
            }
        )
        binding.rviewPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            adapter = mAdapter

        }

        viewmodel.allPosts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApiResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    mAdapter.updateList(result.data.users)
                }
                is ApiResponse.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.something_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ApiResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        mAdapter.setItemClickListener { item ->
            findNavController().navigate(R.id.action_homeFragment_to_DetailsFragment)
        }
        viewmodel.fetchAllPosts()
//

    }

}