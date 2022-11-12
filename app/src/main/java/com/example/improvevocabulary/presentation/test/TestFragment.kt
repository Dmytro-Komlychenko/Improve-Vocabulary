package com.example.improvevocabulary.presentation.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.improvevocabulary.R
import com.example.improvevocabulary.databinding.FragmentTestBinding
import com.example.improvevocabulary.presentation.tests.TypeOfTestInfo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import soup.neumorphism.NeumorphCardView


class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val viewModel: TestViewModel by activityViewModels()
    var testIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTestBinding.inflate(inflater, container, false)
        viewModel.tests.observe(viewLifecycleOwner) {
            if (viewModel.tests.value!!.isNotEmpty()) showTest()
        }
        viewModel.initTests()

        if (viewModel.typeOfTestInfo == TypeOfTestInfo.Practice) {
            binding.pbProgress.visibility = View.INVISIBLE
            binding.tvQuestionNumber.visibility = View.INVISIBLE
            binding.tvSlash.visibility = View.INVISIBLE
            binding.tvQuestionsCount.visibility = View.INVISIBLE
        }

        return binding.root
    }


    private fun showTest() = with(binding) {

        viewModel.tts.setLanguage(viewModel.tests.value!![testIndex].questionLang)
        viewModel.tts.setText(viewModel.tests.value!![testIndex].question)
        Log.i("tts", "Question language: " + viewModel.tests.value!![testIndex].questionLang + "\n"
         + "Question: " + viewModel.tests.value!![testIndex].question)
        viewModel.tts.onInit(android.speech.tts.TextToSpeech.SUCCESS)

        pbProgress.max = viewModel.tests.value!!.count() * 40
        tvQuestionNumber.text = (testIndex + 1).toString()
        tvQuestionsCount.text = viewModel.tests.value!!.count().toString()
        tvQuestion.text = viewModel.tests.value!![testIndex].question
        tvAnswer1.text = viewModel.tests.value!![testIndex].answers[0]
        tvAnswer2.text = viewModel.tests.value!![testIndex].answers[1]
        tvAnswer3.text = viewModel.tests.value!![testIndex].answers[2]
        tvAnswer4.text = viewModel.tests.value!![testIndex].answers[3]

        setOnClickAnswerButton(0, binding.cvAnswer1, binding.clAnswer1)
        setOnClickAnswerButton(1, binding.cvAnswer2, binding.clAnswer2)
        setOnClickAnswerButton(2, binding.cvAnswer3, binding.clAnswer3)
        setOnClickAnswerButton(3, binding.cvAnswer4, binding.clAnswer4)

        binding.btnSound.setOnClickListener {
            viewModel.tts.onInit(android.speech.tts.TextToSpeech.SUCCESS)
        }
    }

    private fun setOnClickAnswerButton(position: Int, cardView: NeumorphCardView, constraintLayout: ConstraintLayout) {
        cardView.setOnClickListener {
            var wordPair =
                viewModel.words.value!!.find { viewModel.tests.value!![testIndex].id == it.id }

            if (viewModel.tests.value!![testIndex].rightAnswerPosition == position) {
                constraintLayout.background =
                    resources.getDrawable(R.drawable.light_gradient_right_answer)

                when (viewModel.typeOfTestInfo) {
                    TypeOfTestInfo.Test -> {
                        wordPair!!.countRightAnswers++
                        viewModel.updateOnStudy(wordPair)
                    }
                    TypeOfTestInfo.Practice -> {}
                    TypeOfTestInfo.Repetition -> {}
                }
            } else {
                constraintLayout.background =
                    resources.getDrawable(R.drawable.dark_gradient_right_answer)

                when (viewModel.typeOfTestInfo) {
                    TypeOfTestInfo.Test -> {
                        wordPair!!.countRightAnswers = 0
                        viewModel.updateOnStudy(wordPair)
                    }
                    TypeOfTestInfo.Practice -> {
                        if (viewModel.words.value!!.count() >= 30) {
                            Snackbar.make(
                                binding.root,
                                resources.getString(R.string.limit_30),
                                Snackbar.LENGTH_SHORT or Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ok) {}
                                .show()
                            return@setOnClickListener
                        }
                        viewModel.moveStudiedToOnStudy(wordPair!!)
                    }
                    TypeOfTestInfo.Repetition -> {
                        if (viewModel.words.value!!.count() >= 30) {
                            Snackbar.make(
                                binding.root,
                                resources.getString(R.string.limit_30),
                                Snackbar.LENGTH_SHORT or Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ok) {}
                                .show()
                            return@setOnClickListener
                        }
                        viewModel.moveStudiedToOnStudy(wordPair!!)
                    }
                }
            }

            lifecycleScope.launch {
                for (i in 0..40) {
                    delay(10)
                    binding.pbProgress.progress += 1 // 1
                }
                constraintLayout.background = resources.getDrawable(R.drawable.little_dark_gradient)

                if (testIndex + 1 == viewModel.tests.value!!.count()) { //если конец теста
                    when (viewModel.typeOfTestInfo) {
                        TypeOfTestInfo.Test -> {
                            //выдаем результат
                            Snackbar.make(
                                binding.root,
                                "The end of test",
                                Snackbar.LENGTH_SHORT or Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ok) {}
                                .show()
                        }
                        TypeOfTestInfo.Repetition -> {
                            //выдаем результат
                            Snackbar.make(
                                binding.root,
                                "The end of repetition",
                                Snackbar.LENGTH_SHORT or Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ok) {}.show()
                        }
                        TypeOfTestInfo.Practice -> {
                            testIndex = 0
                            viewModel.shuffleTests()
                            showTest()
                        }
                    }
                } else {
                    testIndex++
                    showTest()
                }
            }
        }
    }
}