package com.devative.littledoor.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.devative.littledoor.fragment.Question
import com.devative.littledoor.fragment.QuestionFragment
import com.devative.littledoor.model.GetAllQuestions

class QuestionPagerAdapter(fragmentManager: FragmentManager, private val questionList: List<GetAllQuestions.Data>) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return QuestionFragment.newInstance(questionList[position])
    }

    override fun getCount(): Int {
        return questionList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Question ${position + 1}"
    }
}
