package com.example.domain

import com.example.domain.models.Language

class LanguageConverter {
    companion object {
        fun convertLangToCode(language: Language): Language {
            return when (language.value) {
                "English", "Анлийский", "Англійська" -> Language("Default")
                "Russian", "Русский", "Російська" -> Language("RU")
                "Ukrainian", "Украинский", "Українська" -> Language("UK")
                else -> Language("Default")
            }
        }

        fun convertCodeToLang(langCode: Language): Language {
            return when (langCode.value) {
                "Default" -> Language("English")
                "RU" -> Language("Русский")
                "UK" -> Language("Українська")
                else -> Language("Default")
            }
        }
    }
}