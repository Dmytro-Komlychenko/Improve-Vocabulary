package com.example.improvevocabulary.presentation.wordList

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.improvevocabulary.R
import com.example.improvevocabulary.databinding.WordItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class WordPair(
    var id: Int,
    var word: String,
    var translate: String,
    var countRightAnswers: Int = 0
)

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordHolder>() {

    private var words = ArrayList<WordPair>()
    private var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordHolder { // создает холдера
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        context = parent.context
        return WordHolder(view)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) { // вызывает bind холдера
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }


    inner class WordHolder(var item: View) : RecyclerView.ViewHolder(item) {

        private val binding = WordItemBinding.bind(item)
        private var areItemDetailsShown = false
        private lateinit var wordPair: WordPair

        fun getWordPair(): WordPair {
            return wordPair
        }

        fun bind(word: WordPair) = with(binding) {
            wordPair = word

            etWord.setText(word.word)

            if (word.countRightAnswers > 9) {
                isOpportunityTransferWord.visibility = VISIBLE
            }

            cvItem.setOnClickListener {
                setCardForm()

            }

            etWord.onFocusChangeListener = OnFocusChangeListener { _, _ ->
                if(!areItemDetailsShown) {
                    showCardDetails()
                    areItemDetailsShown = true
                }

            }

            btnRemove.setOnClickListener {
                removeWord(word.id)
            }
        }

        private fun setCardForm() {
            if (areItemDetailsShown) {
                hideCardDetails()
                areItemDetailsShown = false
            } else {
                showCardDetails()
                areItemDetailsShown = true
            }
        }

        private fun showCardDetails() = with(binding) {
            setImageBtnMove()
            etTranslate.setText(wordPair.translate)

            setIsOpportunityTransferWordToShowCardDetails()

            animateView(btnSound, 65F, 0F, 0F, 0F)

            textChangingHandlerToShowCardDetails(etWord, wordPair.word)
            textChangingHandlerToShowCardDetails(etTranslate, wordPair.translate)


            dividingLine.visibility = VISIBLE
            etTranslate.visibility = VISIBLE
            btnRemove.visibility = VISIBLE
            btnMove.visibility = VISIBLE

            setConstraintsToShowCardDetails()
        }

        private fun setImageBtnMove() = with(binding) {
            btnMove.setImageResource(
                when (wordPair.countRightAnswers) {
                    0 -> R.drawable.ic_0_from_10
                    1 -> R.drawable.ic_1_from_10
                    2 -> R.drawable.ic_2_from_10
                    3 -> R.drawable.ic_3_from_10
                    4 -> R.drawable.ic_4_from_10
                    5 -> R.drawable.ic_5_from_10
                    6 -> R.drawable.ic_6_from_10
                    7 -> R.drawable.ic_7_from_10
                    8 -> R.drawable.ic_8_from_10
                    9 -> R.drawable.ic_9_from_10
                    else -> R.drawable.ic_move
                }
            )
        }

        private fun animateView(view: View, fromXDelta: Float, toXDelta: Float, fromYDelta: Float, toYDelta: Float) {
            val animation = TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta).apply {
                duration = 200
                fillAfter = true
            }
            view.startAnimation(animation)
        }

        private fun setIsOpportunityTransferWordToShowCardDetails() = with(binding) {
            if (wordPair.countRightAnswers > 9) {
                animateView(isOpportunityTransferWord, 0F, 0F, -50F, 0F)

                val layoutParams = ConstraintLayout.LayoutParams(15, 60)
                isOpportunityTransferWord.layoutParams = layoutParams
                isOpportunityTransferWord.setImageResource(R.drawable.ic_btn_words_message_long)
            }
        }

        private fun textChangingHandlerToShowCardDetails(editText: EditText, word: String) =
            with(binding) {
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (btnSave.visibility != View.GONE) return
                        animateView(btnSound, -65F, 0F, 0F, 0F)

                        if(editText.text.toString() == word) return
                        btnSave.visibility = View.VISIBLE
                        ConstraintSet().apply {
                            clone(clWordView)
                            clear(btnSound.id, ConstraintSet.START)
                            clear(btnSound.id, ConstraintSet.END)
                            connect(
                                btnSound.id,
                                ConstraintSet.LEFT,
                                btnSave.id,
                                ConstraintSet.RIGHT,
                                12
                            )
                            connect(
                                btnSound.id,
                                ConstraintSet.RIGHT,
                                clWordView.id,
                                ConstraintSet.RIGHT,
                                12
                            )
                        }.applyTo(clWordView)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (wordPair.word == s.toString() && wordPair.translate == etTranslate.text.toString()) {
                            animateView(btnSound, 65F, 0F, 0F, 0F)

                            btnSave.visibility = View.GONE

                            ConstraintSet().apply {
                                clone(clWordView)
                                clear(btnSound.id, ConstraintSet.START)
                                connect(
                                    btnSound.id,
                                    ConstraintSet.LEFT,
                                    dividingLine.id,
                                    ConstraintSet.RIGHT
                                )
                            }.applyTo(clWordView)
                        }
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }
                })
            }

        private fun setConstraintsToShowCardDetails() = with(binding) {
            ConstraintSet().apply {
                clone(clWordView)
                //tvWord
                clear(etWord.id, ConstraintSet.BOTTOM)
                clear(etWord.id, ConstraintSet.TOP)
                connect(etWord.id, ConstraintSet.BOTTOM, dividingLine.id, ConstraintSet.TOP, 12)
                connect(etWord.id, ConstraintSet.TOP, clWordView.id, ConstraintSet.TOP, 12)
                //btnSound
                clear(btnSound.id, ConstraintSet.TOP)
                clear(btnSound.id, ConstraintSet.BOTTOM)
                clear(btnSound.id, ConstraintSet.START)
                clear(btnSound.id, ConstraintSet.END)
                connect(btnSound.id, ConstraintSet.TOP, etWord.id, ConstraintSet.TOP)
                connect(btnSound.id, ConstraintSet.BOTTOM, etWord.id, ConstraintSet.BOTTOM)
                connect(btnSound.id, ConstraintSet.RIGHT, btnMove.id, ConstraintSet.RIGHT)
                connect(btnSound.id, ConstraintSet.LEFT, btnRemove.id, ConstraintSet.LEFT)
                //isOpportunityTransferWord
                clear(isOpportunityTransferWord.id, ConstraintSet.TOP)
                clear(isOpportunityTransferWord.id, ConstraintSet.BOTTOM)
                clear(isOpportunityTransferWord.id, ConstraintSet.START)
                clear(isOpportunityTransferWord.id, ConstraintSet.END)
                clear(isOpportunityTransferWord.id, ConstraintSet.BOTTOM)
                connect(
                    isOpportunityTransferWord.id,
                    ConstraintSet.TOP,
                    dividingLine.id,
                    ConstraintSet.TOP
                )
                connect(
                    isOpportunityTransferWord.id,
                    ConstraintSet.BOTTOM,
                    dividingLine.id,
                    ConstraintSet.BOTTOM
                )
                connect(
                    isOpportunityTransferWord.id,
                    ConstraintSet.LEFT,
                    clWordView.id,
                    ConstraintSet.LEFT
                )
                connect(
                    isOpportunityTransferWord.id,
                    ConstraintSet.RIGHT,
                    dividingLine.id,
                    ConstraintSet.LEFT
                )
            }.applyTo(clWordView)
        }


        private fun hideCardDetails() = with(binding) {
            dividingLine.visibility = View.GONE
            etTranslate.visibility = View.GONE
            btnRemove.visibility = View.GONE
            btnMove.visibility = View.GONE
            btnSave.visibility = View.GONE

            setIsOpportunityTransferWordToHideCardDetails()
            animateView(btnSound, -65F, 0F, 0F, 0F)
            textChangingHandlerToHideCardDetails()

            setConstraintsToHideCardDetails()
        }

        private fun setIsOpportunityTransferWordToHideCardDetails() = with(binding) {
            if (wordPair.countRightAnswers > 9) {
                animateView(isOpportunityTransferWord, 0F, 0F, 50F, 0F)

                val layoutParams = ConstraintLayout.LayoutParams(19, 19)
                isOpportunityTransferWord.layoutParams = layoutParams
                isOpportunityTransferWord.setImageResource(R.drawable.ic_btn_words_message)
            }
        }

        private fun textChangingHandlerToHideCardDetails() = with(binding) {
            etWord.clearFocus()
            etWord.tag = etWord.keyListener
            etWord.keyListener = null
            //TODO: добавить tvWord что б  они сменяли etWord когда слово открыто и закрыто
            //TODO: если добавлять/удалять слова из списка то вылетает activity не понятно где и когда и почему
            //TODO: если удалить/перенести слово то кнопка звука на тех словах которые были открыты/закрыты ранее сьезжает влево
            //TODO: свайп переноса должен быть не доступен если countRightAnswers < 10
        }

        private fun setConstraintsToHideCardDetails() = with(binding) {
            ConstraintSet().apply {
                clone(clWordView)

                //btnSound
                clear(btnSound.id, ConstraintSet.TOP)
                clear(btnSound.id, ConstraintSet.BOTTOM)
                clear(btnSound.id, ConstraintSet.LEFT)
                clear(btnSound.id, ConstraintSet.RIGHT)
                connect(btnSound.id, ConstraintSet.TOP, clWordView.id, ConstraintSet.TOP, 10)
                connect(btnSound.id, ConstraintSet.BOTTOM, clWordView.id, ConstraintSet.BOTTOM, 10)
                connect(btnSound.id, ConstraintSet.RIGHT, clWordView.id, ConstraintSet.RIGHT, 10)
                //isOpportunityTransferWord
                clear(isOpportunityTransferWord.id, ConstraintSet.TOP)
                clear(isOpportunityTransferWord.id, ConstraintSet.BOTTOM)
                clear(isOpportunityTransferWord.id, ConstraintSet.START)
                clear(isOpportunityTransferWord.id, ConstraintSet.END)
                connect(isOpportunityTransferWord.id, ConstraintSet.TOP, clWordView.id, ConstraintSet.TOP)
                connect(isOpportunityTransferWord.id, ConstraintSet.BOTTOM, clWordView.id, ConstraintSet.BOTTOM)
                connect(isOpportunityTransferWord.id, ConstraintSet.LEFT, clWordView.id, ConstraintSet.LEFT)
                connect(isOpportunityTransferWord.id, ConstraintSet.RIGHT, etWord.id, ConstraintSet.LEFT)
            }.applyTo(clWordView)
        }

    }



    fun init(words: ArrayList<WordPair>) {
        words.forEach { addWord(it) }
    }

    fun addWord(word: WordPair) {
        val diffUtil = MyDiffUtil(words, words + word)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        words.add(word)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addWordAtPosition(word: WordPair, index: Int) {
        val diffUtil = MyDiffUtil(words, words + word)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        words.add(index, word)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getWordPairIndexById(id: Int): Int {
        return words.indexOf(words.find { word -> word.id == id })
    }

    fun removeWord(id: Int) : Int {
        val index = words.indexOf(words.find { word -> word.id == id })

        val newList = arrayListOf<WordPair>()
        words.forEach { word ->
            if(word.id != id)
                newList.add(word)
        }
        val diffUtil = MyDiffUtil(words, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        words.remove(words.find { word -> word.id == id })
        diffResult.dispatchUpdatesTo(this)

        return index
    }

    fun sort(sortedList: ArrayList<WordPair>){
        val diffUtil = MyDiffUtil(words, sortedList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        words = sortedList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setNewList(newList: ArrayList<WordPair>) {
        val diffUtil = MyDiffUtil(words, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        words = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class MyDiffUtil(
        private val oldList: List<WordPair>,
        private val newList: List<WordPair>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id && oldList[oldItemPosition].word == newList[newItemPosition].word
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList == newList //for DataClass
        }

    }
}