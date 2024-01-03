package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 23-09-2023.
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrExperienceFormVM
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.SelectFilterBinding
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.model.SubCategoryResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet(
    val event: FilterBottomSheetEvent? = null,
) :
    BottomSheetDialogFragment(), OnClickListener {
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }

    private var _binding: SelectFilterBinding? = null
    private val categoryList = ArrayList<CategoryResponse.Data>()
    private val subCategoryList = ArrayList<SubCategoryResponse.Data>()
    private val cities = ArrayList<GetAllCitiesResponse.Data>()
    private val viewModel: MainViewModel by activityViewModels()
    private val vm: DrExperienceFormVM by activityViewModels()
    private val binding get() = _binding!!

    companion object {
        const val TAG = "SelectPocketBottomSheetFragment"
        var FILTER_CITY: String? = null
        var FILTER_CATEGORY: String? = null
        var FILTER_CATEGORY_ID: String? = null
        var FILTER_SUB_CATEGORY: String? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectFilterBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        binding.editTextCities.setOnClickListener(this)
        binding.editTextSubCategory.setOnClickListener(this)
        binding.editTextCategory.setOnClickListener(this)
        binding.ivBottomSheetClose.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        FILTER_CITY?.let { binding.editTextCities.setText(it) }
        FILTER_CATEGORY?.let {
            binding.editTextCategory.setText(it)
            vm.getSubCategory(FILTER_CATEGORY_ID ?: "0")
        }
        FILTER_SUB_CATEGORY?.let { binding.editTextSubCategory.setText(it) }
    }

    private fun observe() {
        vm.getCategory()
        viewModel.getCities()
        vm.categoryData.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        categoryList.clear()
                        categoryList.addAll(it.data.data)
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        vm.subCategoryData.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        subCategoryList.clear()
                        subCategoryList.addAll(it.data.data)
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                }

            }
        }
        viewModel.getCities.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        cities.clear()
                        cities.addAll(it.data.data)
                    }
                }

                Status.ERROR -> {
                }

            }
        }
    }

    interface FilterBottomSheetEvent {
        fun onDismiss()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.editTextCities.id -> {
                val items = ArrayList<SearchAbleList>()
                val uniqueStates =
                    cities.distinctBy { it.city_name }.sortedBy { it.city_name }
                for (i in uniqueStates) {
                    if (i.city_name != null)
                        items.add(SearchAbleList(i.id, i.city_name))
                }
                val dialog = SingleSelectBottomSheetDialogFragment(
                    requireActivity(),
                    items,
                    "Select City",
                    0,
                    true,
                    true
                ) { selectedValue ->
                    Logger.d("TAG", "onCreate: $selectedValue")
                    binding.editTextCities.setText(selectedValue.title)
                    FILTER_CITY = selectedValue.title
                }
                dialog.show(
                    requireActivity().supportFragmentManager,
                    "SingleSelectBottomSheetDialogFragment"
                )
            }

            binding.editTextCategory.id -> {
                val items = ArrayList<SearchAbleList>()
                for (i in categoryList) {
                    items.add(SearchAbleList(i.id, i.name))
                }
                val dialog = SingleSelectBottomSheetDialogFragment(
                    requireActivity(),
                    items,
                    "Select Category",
                    0,
                    false,
                    true
                ) { selectedValue ->
                    Logger.d("TAG", "onCreate: $selectedValue")
                    binding.editTextCategory.setText(selectedValue.title)
                    binding.editTextSubCategory.text?.clear()
                    vm.getSubCategory(selectedValue.position.toString())
                    FILTER_CATEGORY = selectedValue.title
                    FILTER_CATEGORY_ID = selectedValue.position.toString()
                }
                dialog.show(
                    requireActivity().supportFragmentManager,
                    "SingleSelectBottomSheetDialogFragment"
                )
            }

            binding.editTextSubCategory.id -> {
                if (FILTER_CATEGORY_ID == null) {
                    Utility.errorToast(requireContext(), "Please Select Category")
                } else {
                    val items = ArrayList<SearchAbleList>()
                    for (i in subCategoryList) {
                        items.add(SearchAbleList(i.id, i.name))
                    }
                    val dialog = MultipleSelectBottomSheetDialogFragment(
                        requireActivity(),
                        items,
                        "Select Sub Category",
                        "",
                        false,
                        true
                    ) { selectedValue ->
                        Logger.d("TAG", "onCreate: $selectedValue")
                        binding.editTextSubCategory.setText(selectedValue)
                        FILTER_SUB_CATEGORY = selectedValue
                    }
                    dialog.show(
                        requireActivity().supportFragmentManager,
                        "SingleSelectBottomSheetDialogFragment"
                    )
                }
            }

            binding.ivBottomSheetClose.id -> {
                clearFilter()
            }

            binding.btnSubmit.id -> {
                dismiss()
                event?.onDismiss()
            }

        }
    }

    private fun clearFilter() {
        FILTER_CITY = null
        FILTER_CATEGORY = null
        FILTER_CATEGORY_ID = null
        FILTER_SUB_CATEGORY = null
        binding.editTextCategory.text?.clear()
        binding.editTextSubCategory.text?.clear()
        binding.editTextCities.text?.clear()
    }

}

