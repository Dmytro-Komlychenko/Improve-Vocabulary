package com.example.improvevocabulary.presentation.lists.baseList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.usecase.languages.GetLanguageFromLearningUseCase
import com.example.domain.usecase.onStudy.GetOnStudyWordPairsUseCase
import com.example.domain.usecase.onStudy.SaveOnStudyWordPairUseCase
import com.example.domain.usecase.pending.GetPendingWordPairsUseCase
import com.example.domain.usecase.pending.SavePendingWordPairUseCase
import com.example.domain.usecase.studied.GetStudiedWordPairsUseCase
import com.example.domain.usecase.studied.SaveStudiedWordPairUseCase

class WordListViewModelFactory(
    private val getPendingWordPairsUseCase: GetPendingWordPairsUseCase,
    private val getOnStudyWordPairsUseCase: GetOnStudyWordPairsUseCase,
    private val getStudiedWordPairsUseCase: GetStudiedWordPairsUseCase,
    private val getLanguageFromLearningUseCase: GetLanguageFromLearningUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WordListViewModel(
            getPendingWordPairsUseCase = getPendingWordPairsUseCase,
            getOnStudyWordPairsUseCase = getOnStudyWordPairsUseCase,
            getStudiedWordPairsUseCase = getStudiedWordPairsUseCase,
            getLanguageFromLearningUseCase = getLanguageFromLearningUseCase
        ) as T
    }
}
