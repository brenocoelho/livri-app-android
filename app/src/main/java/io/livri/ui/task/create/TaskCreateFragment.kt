package io.livri.ui.task.create

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.livri.R
import io.livri.databinding.TaskCreateFragmentBinding
import io.livri.util.hideKeyboard
import io.livri.domain.Tag
import java.util.*

class TaskCreateFragment : Fragment() {

    private val viewModel: TaskCreateViewModel by lazy {
        ViewModelProvider(this).get(TaskCreateViewModel::class.java)
    }

    private lateinit var taskCreateTagSharedViewModel: TaskCreateTagSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskCreateTagSharedViewModel = activity?.run {
            ViewModelProvider(this)[TaskCreateTagSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TaskCreateFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.tvDueDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                viewModel.setDueDate(cal.time)
            }, year, month, day)
            dpd.show()
        }

        binding.tvTag.setOnClickListener {
            hideKeyboard(this.context, view)
            this.findNavController().navigate(TaskCreateFragmentDirections.actionTaskCreateFragmentToTagListFragment("TaskCreate"))
        }

        binding.fab.setOnClickListener {
            hideKeyboard(this.context, view)
            viewModel.createTask()
            viewModel.status.observe(viewLifecycleOwner, Observer { status ->
                if (status == TaskCreateApiStatus.DONE) {
                    this.findNavController()
                        .navigate(TaskCreateFragmentDirections.actionTaskCreateFragmentToTaskListFragment())
                }
            })
        }

        taskCreateTagSharedViewModel.selected.observe(viewLifecycleOwner, Observer<Tag> {
            if (taskCreateTagSharedViewModel.selected()) {
                viewModel.setTag(it.color + it.name)
                taskCreateTagSharedViewModel.clean()
            }
        })

        setHasOptionsMenu(true)

//        showKeyboard(this.context, binding.etName)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_create_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hideKeyboard(this.context, view)
        return super.onOptionsItemSelected(item)
    }

}
