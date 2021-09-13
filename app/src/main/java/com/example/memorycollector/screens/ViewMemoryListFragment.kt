package com.example.memorycollector.screens

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycollector.MemoryCollectorApplication
import com.example.memorycollector.R
import com.example.memorycollector.adapters.MemoryItemViewAdapter
import com.example.memorycollector.models.Memory
import com.example.memorycollector.models.MemoryItem
import com.example.memorycollector.models.MemoryViewModel
import java.text.DateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewMemoryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewMemoryListFragment : Fragment(), MemoryItemViewAdapter.OnItemClickListener {
    private val model: MemoryViewModel by activityViewModels() {
        MemoryViewModel.MemoryViewModelFactory((activity?.application as MemoryCollectorApplication).repository)
    }

    private var memoryItemList = mutableListOf<MemoryItem>(MemoryItem("Beach", Date().toString(), "", ""))

    val args: ViewMemoryListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        model.addMemory(Memory("Jack Erskine", "Pain",Date(), -43.5191808, 172.57267199999998, ""))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (args.memoryIds != null) {
            //filter memories to only show ones with their IDs in memoryIds
        }
        
        return inflater.inflate(R.layout.fragment_view_memory_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val geocoder: Geocoder = Geocoder(activity)

        val memoryRecyclerView = view.findViewById<RecyclerView>(R.id.memoryRecyclerView)
        memoryRecyclerView.adapter = MemoryItemViewAdapter(memoryItemList, this)
        memoryRecyclerView.layoutManager = LinearLayoutManager(view.context)
        memoryRecyclerView.setHasFixedSize(true)

        model.memory.observe(
                viewLifecycleOwner,
                Observer<List<Memory>> { memories ->
                    memoryItemList.clear()
                    for (memory in memories) {
                        var address = ""
                        val matches = memory?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }
                        if (matches?.size!! > 0) {
                            address = matches?.get(0).getAddressLine(0)
                        }

                        val ds: DateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

                        val dateString = ds.format(memory?.date)

                        memoryItemList.add(MemoryItem(memory.name, dateString, memory.mediaString, address))
                    }

                    memoryRecyclerView.adapter?.notifyDataSetChanged()

                }
        )
    }

    override fun onItemClick(position: Int) {
        val action = ViewMemoryListFragmentDirections.actionViewMemoryListFragmentToViewMemoryFragment(position)
        this.view?.findNavController()?.navigate(action)
    }
}