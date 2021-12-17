package com.example.youngchemist.ui.screen.main.subjects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.*
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.util.*
import javax.inject.Inject

var array: ByteArray? = null
@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _subjectsState: MutableLiveData<ResourceNetwork<List<Subject>>> = MutableLiveData()
    val subjectsState: LiveData<ResourceNetwork<List<Subject>>> = _subjectsState

    private val _imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    private val _userState: MutableLiveData<Resource<String>> = MutableLiveData()
    val userState: LiveData<Resource<String>> = _userState

    fun navigateToLecturesListScreen(subject: Subject) {
        router.navigateTo(Screens.lecturesListScreen(subject))
    }

    fun navigateToTestScreen() {
        //router.navigateTo(Screens.rootTestScreen(""))
    }

    init {
        getAllSubjects()
        viewModelScope.launch {
            userPreferences.userStateFlow.filterNotNull().collect {
                Log.d("TAG",it.toString())
                _userState.postValue(it)
            }
        }
        val data = arrayListOf<String>()
        data.add("<html lang=\"en\"> <head>     <meta charset=\"UTF-8\">     <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">     <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> </head> <body>     <div>         <p align = \"center\";\">             Метан         </p>         <p align = \"center\";\">          <img src=\"https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/metan.png?alt=media&token=b2873f55-8b1c-4a5e-856b-d996193e0935\"                 data-token=\"https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/metan.glb?alt=media&token=e83f8e86-e127-4dca-a220-827ae27de555\"                 alt=\"\\\"                  id=\"image\"                 width=\"300\"                  height=\"300\"                 onclick=\"onClickHandler(this)\">                    </p>     </div> </body> <script lang=\"javascript\" type=\"text/javascript\">     function onClickHandler(element) {         let token = element.dataset.token;         androidImage.get3DImageUrl(token)     } </script> </html>")
        data.add("<!DOCTYPE html> <html lang=\"en\"> <head>     <meta charset=\"UTF-8\">     <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">     <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> </head> <body>     <p  align=\"center\">         Малорастворим в воде, почти в два раза легче воздуха. Метан нетоксичен,          но при высокой концентрации в воздухе обладает слабым наркотическим действием.           Имеются данные, что метан при хроническом воздействии малых концентраций в воздухе неблагоприятно влияет            на центральную нервную систему.             Наркотическое действие метана CH4 ослабляется его малой              растворимостью в воде и крови и химической инертностью. Класс токсичности — четвёртый.     </p>           </body> </html>")
//        val lecture = Lecture(
//            UUID.randomUUID().toString(),
//            "vessels",
//            "Лекция 1",
//            "Применение метана и его структура",
//            data
//        )
//        val answers = arrayListOf<Answer>(
//            Answer("a1",false,0),
//            Answer("a2",false,1),
//            Answer("a3",true,2),
//            Answer("a4",false,3),
//            Answer("На нашем сайте Вы с легкостью можете найти и почитать произведение как отечественных," +
//                    " так и зарубежных поэтов-классиков. С помощью магии слов великие творцы старались передать все чувства,",false)
//        )
//        val task = Task("q1",answers,"https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/metan.png?alt=media&token=b2873f55-8b1c-4a5e-856b-d996193e0935")
//        val answers2 = arrayListOf<Answer>(
//            Answer("a1",false,0),
//            Answer("a2",false,1),
//            Answer("a3",true,2),
//            Answer("a4",false,3)
//        )
//        val task2 = Task("q1",answers2,multipleAnswersAvailable = true)
//        val test = Test(
//            UUID.randomUUID().toString(),
//            "Первый тест",
//            arrayListOf(task,task,task,task2,task,task2),
//            30000
//        )
//        //lecture.test = test
//        viewModelScope.launch {
//            //fireStoreRepository.saveLecture(lecture)
//            //fireStoreRepository.saveTest(test)
//        }
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