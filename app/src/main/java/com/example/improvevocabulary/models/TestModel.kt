package com.example.improvevocabulary.models

import com.example.domain.model.Language

class TestModel(
    var id: Int,
    var question: String,
    var answers: List<String>,
    var rightAnswerPosition: Int,
    var questionLang: Language
)