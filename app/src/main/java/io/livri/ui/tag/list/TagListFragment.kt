package io.livri.ui.tag.list

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.livri.R
import io.livri.databinding.TagListFragmentBinding
import io.livri.ui.task.create.TaskCreateTagSharedViewModel

import io.livri.ui.task.create.TaskCreateViewModel
import io.livri.ui.task.detail.TaskDetailTagSharedViewModel
import io.livri.ui.task.detail.TaskDetailViewModel
import io.livri.ui.task.list.TaskGridAdapter
import io.livri.ui.task.list.TaskListTagSharedViewModel
import io.livri.ui.task.list.TaskListViewModel
import java.util.*

class TagListFragment : Fragment() {

    private val viewModel: TagListViewModel by lazy {
        ViewModelProvider(this).get(TagListViewModel::class.java)
    }

    private lateinit var taskListTagSharedViewModel: TaskListTagSharedViewModel
    private lateinit var taskDetailTagSharedViewModel: TaskDetailTagSharedViewModel
    private lateinit var taskCreateTagSharedViewModel: TaskCreateTagSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskListTagSharedViewModel = activity?.run {
            ViewModelProvider(this)[TaskListTagSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

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

        val args = TagListFragmentArgs.fromBundle(arguments!!)

        val binding = TagListFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the tagGrid RecyclerView
        val tagGridAdapter = TagGridAdapter(TagGridAdapter.OnClickListener {
            viewModel.displayTagDetails(it)
        })
        binding.tagsGrid.adapter = tagGridAdapter

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedTag.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                when (args.origin) {
                    "TaskList" -> {
                        taskListTagSharedViewModel.select(it)
                    }
                    "TaskDetail" -> {
                        taskDetailTagSharedViewModel.select(it)
                    }
                    "TaskCreate" -> {
                        taskCreateTagSharedViewModel.select(it)
                    }
                }
                this.findNavController().navigateUp()

                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayTagDetailsComplete()
            }
        })

        binding.fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Adding new task", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            this.findNavController().navigate(TagListFragmentDirections.actionTagListFragmentToTagCreateFragment(args.origin))
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.refreshTags()
        }

        enableSwipe(binding.tagsGrid, tagGridAdapter, this.context!!)

        return binding.root
    }

    private fun enableSwipe(recyclerView: RecyclerView, tagGridAdapter: TagGridAdapter, context: Context) {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp)
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
                    val task = tagGridAdapter.getTagAt(position)
                    viewModel.deleteTask(task)
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
                        background.color = Color.parseColor("#FF0404")
                        background.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        background.draw(c)

                        // Draw the icon
                        deleteIcon?.setBounds(
                            itemView.left + width.toInt(),
                            itemView.top + width.toInt(),
                            itemView.left + 2 * width.toInt(),
                            itemView.bottom - width.toInt())
                        deleteIcon?.draw(c)
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
