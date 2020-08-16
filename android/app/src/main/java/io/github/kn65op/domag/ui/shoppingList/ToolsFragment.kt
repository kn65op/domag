package io.github.kn65op.domag.ui.shoppingList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.kn65op.domag.R

class ToolsFragment : Fragment() {

    private lateinit var toolsViewModel: ToolsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("HEHE", "$activity")
        Log.i("HEHE", "$context")
        Log.i("HEHE", "${activity?.actionBar}")
        activity?.actionBar?.title = "ENO"
        activity?.title = "HEHESZKI"
        requireActivity().title = "ASD"
        toolsViewModel =
            ViewModelProviders.of(this).get(ToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        toolsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i("HEHE", "act llcreate")
        super.onActivityCreated(savedInstanceState)
        activity?.actionBar?.title = "4 ENO"
        activity?.title = "4 HEHESZKI"
        requireActivity().title = "4 ASD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("HEHE", "llcreate")
        super.onCreate(savedInstanceState)
        activity?.actionBar?.title = "2 ENO"
        activity?.title = "2 HEHESZKI"
        requireActivity().title = "2 ASD"
    }
    override fun onResume() {
        Log.i("HEHE", "resume")
        super.onResume()
        activity?.actionBar?.title = "1 ENO"
        activity?.title = "1 HEHESZKI"
        requireActivity().title = "1 ASD"
    }

}