package com.devative.littledoor.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.adapter.ChipAdapter
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.databinding.ExploreFragmentBinding
import com.devative.littledoor.util.DividerItemDecoration


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class ExploreFragment : Fragment() {
    private lateinit var binding: ExploreFragmentBinding
    private val list =  ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ExploreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.add("Trending")
        list.add("Relation")
        list.add("Self Care")
        list.add("Academic")
        list.add("Career")

        binding.chipGroup.adapter = ChipAdapter(requireActivity(),list, object:
            ChipAdapter.ChipAdapterEvent {
            override fun onclick(position: Int) {

            }
        })
        binding.rvTherapist.adapter = ExplorerAdapter(requireActivity(),object:
            ExplorerAdapter.ExplorerAdapterEvent {
            override fun onclick(position: Int) {

            }
        })

        binding.rvTherapist.addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL,false))
    }

}