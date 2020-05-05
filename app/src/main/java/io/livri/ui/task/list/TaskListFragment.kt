package io.livri.ui.task.list

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.livri.databinding.TaskListFragmentBinding

import io.livri.R
import io.livri.domain.Task
import io.livri.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.task_list_bottom_sheet.view.*
import kotlinx.android.synthetic.main.task_list_date_postpone.view.*
import timber.log.Timber
import java.util.*

class TaskListFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var taskListTagSharedViewModel: TaskListTagSharedViewModel

    private lateinit var binding: TaskListFragmentBinding

    private lateinit var dateDialog: BottomSheetDialog

    private val viewModel: TaskListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, TaskListViewModel.Factory(activity.application))
            .get(TaskListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        loginViewModel = activity?.run {
//            ViewModelProvider(this)[LoginViewModel::class.java]
//        } ?: throw Exception("Invalid Activity")

        taskListTagSharedViewModel = activity?.run {
            ViewModelProvider(this)[TaskListTagSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val dateView = layoutInflater.inflate(R.layout.task_list_date_postpone, null)
        dateDialog = BottomSheetDialog(this.context!!)
        dateDialog.setContentView(dateView)

        dateView.tvOneDay.setOnClickListener {
            var task = viewModel.selectedTask.value
            task?.let {
                viewModel.setTaskDate(it, Calendar.DAY_OF_YEAR, 1)
            }
            dateDialog.hide()
        }

        dateView.tvOneWeek.setOnClickListener {
            var task = viewModel.selectedTask.value
            task?.let {
                viewModel.setTaskDate(it, Calendar.DAY_OF_YEAR, 7)
            }
            dateDialog.hide()
        }

        dateView.tvOneMonth.setOnClickListener {
            var task = viewModel.selectedTask.value
            task?.let {
                viewModel.setTaskDate(it, Calendar.MONTH, 1)
            }
            dateDialog.hide()
        }

//        dateDialog.setOnDismissListener {
//            viewModel.refreshTasks()
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TaskListFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the taskGrid RecyclerView
        val taskGridAdapter = TaskGridAdapter(TaskGridAdapter.OnClickListener {
            viewModel.displayTaskDetails(it)
        })
        binding.tasksGrid.adapter = taskGridAdapter

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.

        viewModel.navigateToSelectedTask.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayTaskDetailsComplete()
            }
        })

        binding.fab.setOnClickListener { view ->
            this.findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragment())
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.refreshTasks()
        }

        taskListTagSharedViewModel.selected.observe(viewLifecycleOwner, Observer {
            if (taskListTagSharedViewModel.selected()) {
                viewModel.updateFilter(it.color + it.name)
                taskListTagSharedViewModel.clean()
           }
        })

        enableSwipe(binding.tasksGrid, taskGridAdapter, this.context!!)

        binding.bar.replaceMenu(R.menu.task_list_menu)

        binding.bar.setOnMenuItemClickListener {item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_filter -> {
                    val view = layoutInflater.inflate(R.layout.task_list_bottom_sheet, null)
                    val dialog = BottomSheetDialog(this.context!!)
                    dialog.setContentView(view)

                    view.tvTaskListAll.setOnClickListener {
                        dialog.hide()
                        viewModel.updateFilter("all")
                        taskListTagSharedViewModel.clean()
                    }

                    view.tvTaskListTag.setOnClickListener {
                        dialog.hide()
                        this.findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToTagListFragment("TaskList"))
                    }

                    dialog.show()
                }
            }
            true
        }

        binding.bar.setNavigationOnClickListener {
//            this.findNavController().navigate(R.id.userLoginFragment)
            this.findNavController().navigate(R.id.loginFragment)
        }


//        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.livri_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun enableSwipe(recyclerView: RecyclerView, taskGridAdapter: TaskGridAdapter, context: Context) {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                private val doneIcon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_white_24dp)
                private val renewIcon = ContextCompat.getDrawable(context, R.drawable.ic_access_time_white_24dp)
                private val background = ColorDrawable()
                private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position = viewHolder.adapterPosition
                    val task = taskGridAdapter.getTaskAt(position)

                    if (direction == ItemTouchHelper.LEFT) {
                        viewModel.setSelectedTask(task)
                        dateDialog.show()
                    } else {
                        viewModel.doneTask(task)
                    }
                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {

                    val itemView = viewHolder.itemView
                    val isCanceled = dX == 0f && !isCurrentlyActive

                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (isCanceled) {
                        clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        return
                    }

                    if (dX > 0) {
                        // Draw the background
                        background.color = Color.parseColor("#2f8f36")
                        background.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        background.draw(c)

                        // Draw the icon
                        doneIcon?.setBounds(
                            itemView.left + width.toInt(),
                            itemView.top + width.toInt(),
                            itemView.left + 2 * width.toInt(),
                            itemView.bottom - width.toInt())
                        doneIcon?.draw(c)
                    } else {
                        // Draw the background
                        background.color = Color.parseColor("#db9737")
                        background.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom                        )
                        background.draw(c)

                        // Draw the icon
                        renewIcon?.setBounds(
                                itemView.right - 2 * width.toInt(),
                                itemView.top + width.toInt(),
                                itemView.right - width.toInt(),
                                itemView.bottom - width.toInt()                        )
                        renewIcon?.draw(c)
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

                private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
                    c.drawRect(left, top, right, bottom, clearPaint)
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
