package com.devative.littledoor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.fragment.app.Fragment
import carbon.widget.OnCheckedChangeListener
import com.devative.littledoor.activity.MCQActivity.Companion.selectedOptionIndex
import com.devative.littledoor.databinding.FragmentQuestionBinding
import com.devative.littledoor.model.GetAllQuestions

/**
 * Created by AQUIB RASHID SHAIKH on 12-03-2023.
 */

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>
) : java.io.Serializable

class QuestionFragment : Fragment() {
    private lateinit var question: GetAllQuestions.Data

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)

        question = arguments?.get(ARG_QUESTION) as GetAllQuestions.Data
        mcqHandling()
        return binding.root
    }

    private fun mcqHandling() {
        val questionTextView = binding.questionTextView
        val option1RadioButton = binding.option1RadioButton
        val option2RadioButton = binding.option2RadioButton
        val option3RadioButton = binding.option3RadioButton
        val option4RadioButton = binding.option4RadioButton

        questionTextView.text = question.question_name
        if (question.options.size > 0)
        option1RadioButton.text = question.options[0].option_name
        if (question.options.size > 1)
        option2RadioButton.text = question.options[1].option_name
        if (question.options.size > 2)
        option3RadioButton.text = question.options[2].option_name
        if (question.options.size > 3)
            option4RadioButton.text = question.options[3].option_name

        option1RadioButton.setOnCheckedChangeListener(object :
            OnCheckedChangeListener {
            override fun onCheckedChanged(view: Checkable, isChecked: Boolean) {
                if (isChecked) {
                    selectedOptionIndex = 0
                }
            }

        })
        option2RadioButton.setOnCheckedChangeListener(object :
            OnCheckedChangeListener {
            override fun onCheckedChanged(view: Checkable, isChecked: Boolean) {
                if (isChecked) {
                    selectedOptionIndex = 1
                }
            }
        })
        option3RadioButton.setOnCheckedChangeListener(object :
            OnCheckedChangeListener {
            override fun onCheckedChanged(view: Checkable, isChecked: Boolean) {
                if (isChecked) {
                    selectedOptionIndex = 2
                }
            }
        })
        option4RadioButton.setOnCheckedChangeListener(object :
            OnCheckedChangeListener {
            override fun onCheckedChanged(view: Checkable, isChecked: Boolean) {
                if (isChecked) {
                    selectedOptionIndex = 3
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_QUESTION = "question"

        fun newInstance(question: GetAllQuestions.Data): QuestionFragment {
            val args = Bundle()
            args.putSerializable(ARG_QUESTION, question)
            val fragment = QuestionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
