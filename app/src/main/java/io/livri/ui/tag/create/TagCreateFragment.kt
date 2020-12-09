package io.livri.ui.tag.create

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.livri.R
import io.livri.databinding.TagCreateFragmentBinding
import io.livri.util.hideKeyboard
import io.livri.ui.task.create.TaskCreateTagSharedViewModel
import io.livri.ui.task.detail.TaskDetailTagSharedViewModel
import timber.log.Timber

class TagCreateFragment : Fragment() {

    private val viewModel: TagCreateViewModel by lazy {
        ViewModelProvider(this).get(TagCreateViewModel::class.java)
    }

    private lateinit var taskDetailTagSharedViewModel: TaskDetailTagSharedViewModel
    private lateinit var taskCreateTagSharedViewModel: TaskCreateTagSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskDetailTagSharedViewModel = activity?.run {
            ViewModelProvider(this)[TaskDetailTagSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        taskCreateTagSharedViewModel = activity?.run {
            ViewModelProvider(this)[TaskCreateTagSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = TagCreateFragmentArgs.fromBundle(arguments!!)

        val binding = TagCreateFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.bc000000.setOnClickListener { viewModel.setColor("000000") }
        binding.bc96AC32.setOnClickListener { viewModel.setColor("96AC32") }
        binding.bc00BCD4.setOnClickListener { viewModel.setColor("00BCD4") }
        binding.bc1A8F05.setOnClickListener { viewModel.setColor("1A8F05") }
        binding.bc246167.setOnClickListener { viewModel.setColor("246167") }
        binding.bc673AB7.setOnClickListener { viewModel.setColor("673AB7") }
        binding.bc6E7273.setOnClickListener { viewModel.setColor("6E7273") }
        binding.bc9E5737.setOnClickListener { viewModel.setColor("9E5737") }
        binding.bcA59401.setOnClickListener { viewModel.setColor("A59401") }
        binding.bcBE39DC.setOnClickListener { viewModel.setColor("BE39DC") }
        binding.bcA10273.setOnClickListener { viewModel.setColor("A10273") }
        binding.bc8C0202.setOnClickListener { viewModel.setColor("8C0202") }
        binding.bc0218A3.setOnClickListener { viewModel.setColor("0218A3") }
        binding.bcFF9800.setOnClickListener { viewModel.setColor("FF9800") }

        binding.fab.setOnClickListener {
            hideKeyboard(this.context, view)
            viewModel.createTag()
            viewModel.status.observe(viewLifecycleOwner, Observer { status ->
                Timber.i(status.toString())
                Timber.i(args.toString())
                if (status == TagCreateApiStatus.DONE) {
                    when (args.origin) {
                        "TaskDetail" -> {
                            val tag = viewModel.tag.value
                            taskDetailTagSharedViewModel.select(tag!!)
                            this.findNavController().navigateUp()
                            this.findNavController().navigateUp()
                        }
                        "TaskCreate" -> {
                            val tag = viewModel.tag.value
                            taskCreateTagSharedViewModel.select(tag!!)
                            this.findNavController().navigateUp()
                            this.findNavController().navigateUp()
                        }
                        "TaskList" -> this.findNavController().navigate(TagCreateFragmentDirections.actionTagCreateFragmentToTagListFragment(args.origin))
                    }
                }
            })
        }

        setHasOptionsMenu(true)

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
