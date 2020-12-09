package io.livri.ui.task.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.livri.R
import io.livri.databinding.TaskDetailFragmentBinding
import io.livri.util.hideKeyboard
import io.livri.domain.Tag
import java.util.*

class TaskDetailFragment : Fragment() {

    private lateinit var viewModel: TaskDetailViewModel

    private lateinit var taskDetailTagSharedViewModel: TaskDetailTagSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskDetailTagSharedViewModel = activity?.run {
            ViewModelProvider(this).get(TaskDetailTagSharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = TaskDetailFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        var task = TaskDetailFragmentArgs.fromBundle(arguments!!).selectedTask
        val application = requireNotNull(activity).application
        viewModel = ViewModelProvider(this, TaskDetailViewModel.Factory(task, application))
            .get(TaskDetailViewModel::class.java)

        binding.viewModel = viewModel

        binding.tvDueDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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

        binding.tvTaskDetailTag.setOnClickListener {
            hideKeyboard(this.context, view)
            this.findNavController().navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToTagListFragment("TaskDetail"))
        }

        taskDetailTagSharedViewModel.selected.observe(viewLifecycleOwner, Observer<Tag> {
            if (taskDetailTagSharedViewModel.selected()) {
                viewModel.setTag(it.color + it.name)
                taskDetailTagSharedViewModel.clean()
            }
        })

        binding.fab.setOnClickListener {
            hideKeyboard(this.context, view)
            viewModel.updateTask()
            viewModel.statusUpdate.observe(viewLifecycleOwner, Observer { status ->
                if (status == TaskDetailUpdateApiStatus.DONE)
                    this.findNavController()
                        .navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskListFragment())
            })
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hideKeyboard(this.context, view)
        return when (item.itemId) {
            R.id.action_delete -> {
                viewModel.deleteTask()
                viewModel.statusDelete.observe(viewLifecycleOwner, Observer { status ->
                    if (status == TaskDetailDeleteApiStatus.DONE)
                        this.findNavController().navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskListFragment())
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
