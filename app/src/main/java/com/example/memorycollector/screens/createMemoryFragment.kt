package com.example.memorycollector.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.memorycollector.MemoryCollectorApplication
import com.example.memorycollector.R
import com.example.memorycollector.models.Memory
import com.example.memorycollector.models.MemoryViewModel
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val MEDIA_OUTPUT_URI = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [createMemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class createMemoryFragment : Fragment() {
    lateinit var mediaOutputUri: String
    lateinit var name: String
    lateinit var description: String
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var dateString: String
    lateinit var timeString: String

    private val viewModel: MemoryViewModel by activityViewModels {
        MemoryViewModel.MemoryViewModelFactory((activity?.application as MemoryCollectorApplication).repository)
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_memory, container, false)
    }

    @SuppressLint("WrongThread")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFragmentArguments(view)

        // Set navigation to DisplayImageFragment
        view.findViewById<ImageView>(R.id.frameLayout).setOnClickListener {
            val action = createMemoryFragmentDirections.actionCreateMemoryFragmentToDisplayImage(
                    mediaOutputUri
            )
            this.view?.findNavController()?.navigate(action)
        }

        // Set set Location Button
        view.findViewById<Button>(R.id.setLocationButton)?.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.memoryNameInput).text.toString()
            val description = view.findViewById<EditText>(R.id.memoryDescriptionInput).text.toString()
            val action = createMemoryFragmentDirections.actionCreateMemoryFragmentToSetMemoryLocationFragment(
                    name, description, longitude, latitude, mediaOutputUri, timeString, dateString
            )
            view.findNavController()
                    .navigate(action)
        }

        // Set save Memory Button
        view.findViewById<Button>(R.id.saveMemoryButton)?.setOnClickListener {
            if (createMemory()) {
                view.findNavController()
                        .navigate(R.id.action_createMemoryFragment_to_viewMemoryListFragment)
            }
        }

        view.findViewById<EditText>(R.id.editMemoryDate).setOnClickListener {
            loadDatePickerDialog(view)
        }

        view.findViewById<EditText>(R.id.editMemoryTime).setOnClickListener {
            loadTimePickerDialog(view)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadDatePickerDialog(view: View) {
        var lblDate = view.findViewById<EditText>(R.id.editMemoryDate)

        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = context?.let {
            DatePickerDialog(it, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                lblDate.setText("" + year + "-" + monthOfYear + "-" + dayOfMonth)
                dateString = "" + year + "-" + monthOfYear + "-" + dayOfMonth
            }, mYear, mMonth, mDay)
        }
        if (datePickerDialog != null) {
            datePickerDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadTimePickerDialog(view: View) {
        var lblTime = view.findViewById<EditText>(R.id.editMemoryTime)

        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = context?.let {
            TimePickerDialog(it, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                lblTime.setText(SimpleDateFormat("HH:mm").format(calendar.time) + ":00")
                timeString = SimpleDateFormat("HH:mm").format(calendar.time) + ":00"
            }, mHour, mMinute, true)
        }
        if (timePickerDialog != null) {
            timePickerDialog.show()
        }
    }

    private fun loadFragmentArguments(view: View) {
        mediaOutputUri = arguments?.getString("mediaOutputUri")!!
        view.findViewById<ImageView>(R.id.frameLayout).setImageURI(Uri.parse(mediaOutputUri))

        name = arguments?.getString("name")!!
        view.findViewById<EditText>(R.id.memoryNameInput).setText(name)

        description = arguments?.getString("description")!!
        view.findViewById<EditText>(R.id.memoryDescriptionInput).setText(name)

        latitude = arguments?.getString("latitude")!!
        longitude = arguments?.getString("longitude")!!

        dateString = arguments?.getString("dateString")!!
        view.findViewById<EditText>(R.id.editMemoryDate).setText(dateString)

        timeString = arguments?.getString("timeString")!!
        view.findViewById<EditText>(R.id.editMemoryTime).setText(timeString)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMemory(): Boolean {
        val nameText = view?.findViewById<EditText>(R.id.memoryNameInput)
        if (nameText?.text.toString() == "") {
            Toast.makeText(requireContext(), "Please set the title of the memory", Toast.LENGTH_LONG).show()
            return false
        }
        val descriptionText = view?.findViewById<EditText>(R.id.memoryDescriptionInput)

        var date: Date
        val dateString = view?.findViewById<EditText>(R.id.editMemoryDate)?.text.toString()
        val timeString = view?.findViewById<EditText>(R.id.editMemoryTime)?.text.toString()
        if (dateString == "" && timeString == "") {

            date = Date()
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            date = dateFormat.parse(dateString + " " + timeString)
        }

        println("FLAG12345" + date.toString())
        if (latitude == "" && longitude == "") {
            Toast.makeText(requireContext(), "Please set the location of the memory", Toast.LENGTH_LONG).show()
            return false
        }
        val latitudeDouble = latitude.toDouble()
        val longitudeDouble = longitude.toDouble()

        val memory = Memory(
                nameText?.text.toString(),
                descriptionText?.text.toString(),
                date,
                latitudeDouble,
                longitudeDouble,
                mediaOutputUri
        )
        viewModel.addMemory(memory)
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment createMemoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            createMemoryFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_OUTPUT_URI, param1)
                }
            }
    }
}