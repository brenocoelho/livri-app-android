package io.livri.ui.user.login

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.livri.R
import io.livri.databinding.UserLoginFragmentBinding
import io.livri.util.hideKeyboard
import io.livri.ui.task.create.TaskCreateTagSharedViewModel
import io.livri.ui.task.detail.TaskDetailTagSharedViewModel

class UserLoginFragment : Fragment() {

    private val viewModel: UserLoginViewModel by lazy {
        ViewModelProvider(this).get(UserLoginViewModel::class.java)
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

        val binding = UserLoginFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.login.setOnClickListener {
            hideKeyboard(this.context, view)
        }

        return binding.root
    }


}
