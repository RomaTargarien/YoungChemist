package com.example.youngchemist.ui.screen.main.subjects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Answer
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.Task
import com.example.youngchemist.model.Test
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import javax.inject.Inject

var array: ByteArray? = null
@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel() {

    private val _subjectsState: MutableLiveData<ResourceNetwork<List<Subject>>> = MutableLiveData()
    val subjectsState: LiveData<ResourceNetwork<List<Subject>>> = _subjectsState

    private val _imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    fun navigateToLecturesListScreen(title: String) {
        router.navigateTo(Screens.lecturesListScreen(title))
    }

    fun navigateToTestScreen() {
        router.navigateTo(Screens.testScreen())
    }

    init {
        getAllSubjects()
        val answers = arrayListOf<Answer>(
            Answer("a1",false),
            Answer("a2",false),
            Answer("a3",true),
            Answer("a4",false),
            Answer("На нашем сайте Вы с легкостью можете найти и почитать произведение как отечественных," +
                    " так и зарубежных поэтов-классиков. С помощью магии слов великие творцы старались передать все чувства,",false)
        )
        val task = Task("q1",answers,"https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/metan.png?alt=media&token=b2873f55-8b1c-4a5e-856b-d996193e0935")
        val answers2 = arrayListOf<Answer>(
            Answer("a1",false),
            Answer("a2",false),
            Answer("a3",true),
            Answer("a4",false)
        )
        val task2 = Task("q1",answers2,multipleAnswersAvailable = true)
        val test = Test(0,0,"Первый тест", arrayListOf(task,task2,task2))
        viewModelScope.launch {
            //fireStoreRepository.saveTest(test)
        }
    }

    private fun getAllSubjects() {
        viewModelScope.launch {
            if (databaseRepository.getAllSubjects().isEmpty()) {
                _subjectsState.postValue(ResourceNetwork.Loading())
                val subjects = fireStoreRepository.getAllSubjects()
                if (subjects is ResourceNetwork.Success) {
                    subjects.data?.let { subjectsList ->
                        subjectsList.forEach { subject ->
                            val bitmap = getBitmapFromURL(subject.icon_url)
                            bitmap.data?.let {
                                subject.iconByteArray = BitmapUtils.convertBitmapToByteArray(it)
                            }
                        }
                        Log.d("TAG",subjectsList.toString())
                        _subjectsState.postValue(ResourceNetwork.Success(subjectsList))
                        databaseRepository.insertNewSubjects(subjectsList)
                    }
                } else {
                    _subjectsState.postValue(subjects)
                }
            } else {
                val subjects = databaseRepository.getAllSubjects()
                _subjectsState.postValue(ResourceNetwork.Success(subjects))
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

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG","onCleared")
    }
}