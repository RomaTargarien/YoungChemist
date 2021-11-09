package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLectureBinding
import com.google.firebase.storage.FirebaseStorage


class LectureFragment : Fragment() {

    private lateinit var binding: FragmentLectureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLectureBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = FirebaseStorage.getInstance()

        binding.ivCh4.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            Log.d("TAG",it.toString())
            val uri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                .appendQueryParameter(
                    "file",
                    "https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/carbon_red-2.glb?alt=media&token=02c2a897-c722-4e04-a63c-096129c5cd2b"
                )
                .appendQueryParameter("mode", "3d_only")
                .build()
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setData(uri)
            intent.setPackage("com.google.ar.core")
            startActivity(intent)
//            try {
//                val model = storage.getReference("carbon_red-2.glb")
//                model.downloadUrl.addOnSuccessListener {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    Log.d("TAG",it.toString())
//                    val uri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
//                        .appendQueryParameter(
//                            "file",
//                            "https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/carbon_red-2.glb?alt=media&token=02c2a897-c722-4e04-a63c-096129c5cd2b"
//                        )
//                        .appendQueryParameter("mode", "3d_only")
//                        .build()
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    intent.setData(uri)
//                    intent.setPackage("com.google.ar.core")
//                    startActivity(intent)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }
    }

}