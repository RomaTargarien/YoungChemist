package com.example.youngchemist.ui.screen.main.subjects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Subject
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.convertToByteArray
import com.example.youngchemist.ui.util.safeCall
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _subjectsState: MutableLiveData<ResourceNetwork<List<Subject>>> = MutableLiveData()
    val subjectsState: LiveData<ResourceNetwork<List<Subject>>> = _subjectsState

    private val _imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    fun navigateToLecturesListScreen(title: String) {
        router.navigateTo(Screens.lecturesListScreen(title))
    }

    init {
        getAllSubjects()
    }

    private fun getAllSubjects() {
        viewModelScope.launch {
            val subjects = fireStoreRepository.getAllSubjects()
            if (subjects is ResourceNetwork.Success) {
                val bitmap = getBitmapFromURL(subjects.data?.get(0)?.iconUrl)
               if (bitmap is ResourceNetwork.Success) {
                   val array = bitmap.data?.convertToByteArray()
                   val bitmapFromArray = BitmapFactory.decodeByteArray(array,0,array!!.size)
                   Log.d("TAG",bitmapFromArray.toString())
                   _imageBitmap.postValue(bitmapFromArray)
                   _subjectsState.postValue(subjects)
               }
            }
        }
    }

    suspend fun getBitmapFromURL(src: String?) = withContext(Dispatchers.IO) {
        safeCall {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            ResourceNetwork.Success(BitmapFactory.decodeStream(input))
        }
    }
}