package com.example.improvevocabulary.presentation.wordList

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.improvevocabulary.R
import com.example.improvevocabulary.databinding.WordItemBinding


data class WordPair(var id: Int, var word: String, var translate: String)

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordHolder>() {

    private val words = ArrayList<WordPair>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordHolder { // создает холдера
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_item, parent, false)
        return WordHolder(view)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) { // вызывает bind холдера
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }


    inner class WordHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = WordItemBinding.bind(item)
        private var isItemViewExtended = false

        fun bind(word: WordPair) = with(binding) {
            etWord.setText(word.word)

            etWord.tag = etWord.keyListener;
            etWord.keyListener = null;

            cvItem.setOnClickListener {
                extendView(word)
            }

            etWord.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                extendView(word)

                Handler().postDelayed({
                    etWord.keyListener = etWord.tag as KeyListener
                }, 10)
            }


        }

        fun extendView(word: WordPair) = with(binding) {
            if (isItemViewExtended) {
                isItemViewExtended = false
                return
            } else isItemViewExtended = true
            etTranslate.setText(word.translate)

            etWord.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (word.word == s.toString() && word.translate == etTranslate.text.toString()) {
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
                            cvItem.id,
                            ConstraintSet.RIGHT,
                            12
                        )
                    }.applyTo(clWordView)
                }
            })

            etTranslate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (word.translate == s.toString() && word.word == etWord.text.toString()) {
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
            })


            //TODO: show count right answers instead of image if it's less than 10


            //TODO:
            //TODO: by pressed hide back extended view

            dividingLine.visibility = View.VISIBLE
            etTranslate.visibility = View.VISIBLE
            btnRemove.visibility = View.VISIBLE

            btnMove.visibility = View.VISIBLE

            val layoutParams = ConstraintLayout.LayoutParams(15, 60)
            isOpportunityTransferWord.layoutParams = layoutParams
            isOpportunityTransferWord.setImageResource(R.drawable.ic_btn_words_message_long)

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

    class MyDiffUtil(
        private val oldList: List<WordPair>,
        private val newList: List<WordPair>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList == newList //for DataClass
        }

    }
}