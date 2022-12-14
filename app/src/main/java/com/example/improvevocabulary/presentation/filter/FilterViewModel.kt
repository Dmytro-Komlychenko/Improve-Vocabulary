package com.example.improvevocabulary.presentation.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.FilterBy
import com.example.domain.model.PressedSortButton
import com.example.domain.usecase.filter.GetFilterByUseCase
import com.example.domain.usecase.filter.SaveFilterByUseCase

class FilterViewModel(
    private val getFilterByUseCase: GetFilterByUseCase,
    private val saveFilterByUseCase: SaveFilterByUseCase
) : ViewModel() {

    val pressedSortButton = MutableLiveData<PressedSortButton>()


    fun saveFilterBy(filterBy: PressedSortButton) {
        saveFilterByUseCase.execute(FilterBy(filterBy))
    }

    fun load() {
        pressedSortButton.value = getFilterByUseCase.execute().value
    }
}