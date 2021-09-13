package com.example.memorycollector.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.memorycollector.MemoryCollectorApplication
import com.example.memorycollector.R
import com.example.memorycollector.models.MemoryViewModel
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import java.text.DateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewMemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewMemoryFragment : Fragment() {
    private val model: MemoryViewModel by activityViewModels() {
        MemoryViewModel.MemoryViewModelFactory((activity?.application as MemoryCollectorApplication).repository)
    }

    val args: ViewMemoryFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_memory, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val geocoder: Geocoder = Geocoder(activity)

        val title: TextView = view.findViewById(R.id.text_view_title)
        val description: TextView = view.findViewById(R.id.text_view_description)
        val date: TextView = view.findViewById(R.id.text_view_date)
        val location: TextView = view.findViewById(R.id.text_view_location)
        val image: ImageView = view.findViewById(R.id.image_view)
        val position = args.position

        val memory = model.memory.value?.get(position)
        val matches = memory?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }


        title.text = memory?.name
        description.text = memory?.description


        val ds: DateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);

        val dateString = ds.format(memory?.date)

        date.text = dateString
        if (matches?.size!! > 0) {
            val address = matches?.get(0)
            location.text = address.getAddressLine(0)
        }


        image.setImageURI(Uri.parse(memory?.mediaString))

        image.setOnClickListener {
            val action = memory?.mediaString?.let { it1 ->
                ViewMemoryFragmentDirections.actionViewMemoryFragmentToDisplayImage(
                        it1
                )
            }
            if (action != null) {
                this.view?.findNavController()?.navigate(action)
            }
        }


        view.findViewById<Button>(R.id.button_delete).setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder?.setMessage(R.string.delete_memory_description)
                .setTitle(R.string.delete_memory_title)
            builder.apply {
                setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            if (memory != null) {
                                model.deleteMemory(memory)
                            }
                            val action = ViewMemoryFragmentDirections.actionViewMemoryFragmentToViewMemoryListFragment(null)
                            val toast = Toast.makeText(it.context, R.string.memory_delete_success, Toast.LENGTH_SHORT)
                            toast.show()
                            it?.findNavController()?.navigate(action)
                        })
                setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
            }
            // Create the AlertDialog
            builder.show()
        }

        view.findViewById<Button>(R.id.button_edit).setOnClickListener {
            val action = ViewMemoryFragmentDirections.actionViewMemoryFragmentToEditMemoryFragment(args.position)
            this.view?.findNavController()?.navigate(action)
        }

        view.findViewById<Button>(R.id.button_share).setOnClickListener {
            Log.d("TAG", "reached share click")
            // Share on facebook
            val imageUri =  Uri.parse(memory.mediaString)
            var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Log.d("TAG", "reached first if")
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri))
            } else {
                Log.d("TAG", "reached second if")

                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            }



            val photo = SharePhoto.Builder()
                    .setBitmap(bitmap!!)
                    .setImageUrl(imageUri)
                    .build()

            val content = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()

            val content2 = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                    .build()

//            val content3 = Share
            //ShareDialog.show(this, content)

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT,  "My memory: " + memory.name + "\n" + memory.date)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = "image/jpeg"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(shareIntent)



        }
    }

}