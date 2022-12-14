package com.example.improvevocabulary.presentation.lists.baseList

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Language
import com.example.domain.model.OnStudyWordPair
import com.example.domain.model.PendingWordPair
import com.example.domain.model.StudiedWordPair
import com.example.domain.usecase.languages.GetLanguageFromLearningUseCase
import com.example.domain.usecase.languages.GetLanguageOfLearningUseCase
import com.example.domain.usecase.onStudy.GetOnStudyWordPairsUseCase
import com.example.domain.usecase.pending.GetPendingWordPairsUseCase
import com.example.domain.usecase.studied.GetStudiedWordPairsUseCase
import com.example.improvevocabulary.models.WordPair
import com.example.improvevocabulary.presentation.test.TypeOfList
import com.example.improvevocabulary.presentation.wordsFragment.WordListInfo
import com.example.improvevocabulary.utlis.TextToSpeech
import kotlinx.coroutines.launch

open class WordListViewModel(
    val getPendingWordPairsUseCase: GetPendingWordPairsUseCase,
    val getOnStudyWordPairsUseCase: GetOnStudyWordPairsUseCase,
    val getStudiedWordPairsUseCase: GetStudiedWordPairsUseCase,
    val getLanguageFromLearningUseCase: GetLanguageFromLearningUseCase,
    val getLanguageOfLearningUseCase: GetLanguageOfLearningUseCase,
    val tts: TextToSpeech,
    ) : ViewModel() {

    var wordListInfo: MutableLiveData<WordListInfo> = MutableLiveData()
    var words = MutableLiveData<ArrayList<WordPair>>()

    var isEmptyList: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    var languageFromLearning: MutableLiveData<Language> =
        MutableLiveData()

    var languageOfLearning: MutableLiveData<Language> =
        MutableLiveData()

    var typeOfList: MutableLiveData<TypeOfList> = MutableLiveData()

    fun init() {
        viewModelScope.launch {
            languageFromLearning.value = getLanguageFromLearningUseCase.execute()
        }
        isEmptyList.value = false

        when (wordListInfo.value) {
            WordListInfo.Pending -> {
                viewModelScope.launch {
                    var temp: ArrayList<WordPair> = arrayListOf()
                    getPendingWordPairsUseCase.execute().forEach { temp.add(mapToWordPair(it)) }
                    words.value = temp
                }
            }

            WordListInfo.OnStudy -> {
                viewModelScope.launch {
                    var temp: ArrayList<WordPair> = arrayListOf()
                     getOnStudyWordPairsUseCase.execute().forEach { temp.add(mapToWordPair(it)) }
                    words.value = temp
                }
            }

            WordListInfo.Studied -> {
                viewModelScope.launch {
                    var temp: ArrayList<WordPair> = arrayListOf()
                    getStudiedWordPairsUseCase.execute().forEach { temp.add(mapToWordPair(it)) }
                    words.value = temp
                }
            }
            else -> {}
        }

    }

    private fun mapToWordPair(dataModel: OnStudyWordPair): WordPair {
        return WordPair(
            dataModel.id,
            dataModel.word,
            dataModel.translate,
            dataModel.countRightAnswers
        )
    }

    private fun mapToWordPair(dataModel: PendingWordPair): WordPair {
        return WordPair(
            dataModel.id,
            dataModel.word,
            dataModel.translate
        )
    }

    private fun mapToWordPair(dataModel: StudiedWordPair): WordPair {
        return WordPair(
            dataModel.id,
            dataModel.word,
            dataModel.translate
        )
    }
}