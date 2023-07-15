package com.devative.littledoor.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.devative.littledoor.R
import com.devative.littledoor.adapter.QuestionPagerAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityMcqactivityBinding
import com.devative.littledoor.fragment.Question
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class MCQActivity : BaseActivity() {
    private lateinit var binding: ActivityMcqactivityBinding
    private lateinit var viewModel: MainViewModel
    val jsonArray = JSONArray()
    private val questionList = ArrayList<GetAllQuestions.Data>()
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: QuestionPagerAdapter
    private var selectedAnswers = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMcqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = MainViewModel.getViewModel(this)
        viewModel.getQuestions()
        progress = Progress(this)
        // questionList = getQuestionList()
        viewPager = binding.viewPager
        adapter = QuestionPagerAdapter(supportFragmentManager, questionList)
        viewPager.adapter = adapter
        viewPager.setOnTouchListener { v, event -> true }
        binding.txtPageIndicator.text = "${1}/${questionList.size}"
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                var percentage = (position.toDouble() / questionList.size) * 100
                binding.prgIndeicator.progress = percentage.toInt()
               binding.txtPageIndicator.text = "${position+1}/${questionList.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        binding.btnNext.setOnClickListener { onQuestionAnswered() }
        binding.txtSkip.setOnClickListener { submitAnswers() }

        viewModel.getQuestions.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }
                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                      it.data.data?.let {list ->
                          questionList.addAll(list)
                          adapter.notifyDataSetChanged()
                      }
                    } else {
                      //  Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }
                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    private fun getQuestionList(): List<Question> {
        val questionList = mutableListOf<Question>()
        // Add questions to the list
        return questionList
    }

    fun onQuestionAnswered() {
        if (selectedOptionIndex != -1) {
            val question = questionList[viewPager.currentItem]
            val jsonObject = JSONObject()
            jsonObject.put("questionID", question.id)
            jsonObject.put("question", question.name)
            jsonObject.put("selectedAnswer", question.options[selectedOptionIndex].option_id)
            jsonArray.put(jsonObject)
            if (viewPager.currentItem == questionList.lastIndex) {
                submitAnswers()
                selectedOptionIndex = -1
            } else {
                viewPager.currentItem += 1
                selectedOptionIndex = -1
            }
        }else{
            Utility.errorToast(applicationContext,"Please select the answer")
        }

    }

    private fun submitAnswers() {
        Logger.d("Selected Answers", jsonArray.toString())
        startActivity(Intent(applicationContext,GetStartedActivity::class.java).setFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish()

    }

    companion object{
        var selectedOptionIndex = -1
    }
}
