package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener


class ElectionsFragment: Fragment() {


    private var RecyclerAdapter1: ElectionListAdapter?=null
    private var RecyclerAdapter2: ElectionListAdapter?=null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding:FragmentElectionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this

        //View Model
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao


        val viewModelFactory = ElectionsViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val electionViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(ElectionsViewModel::class.java)


        binding.electionviewmodel = electionViewModel


// Recycler 1
        //connect viewmodel adapter with the clicklistener
        RecyclerAdapter1= ElectionListAdapter(ElectionListener {
            electionViewModel.onNavigateToVoterelection(it)
        })

        binding.upcomingRecycler.adapter = RecyclerAdapter1

        //update recycler data
//        electionViewModel.electionsList.observe(viewLifecycleOwner, Observer{ electionlist ->
//            electionlist?.apply {
//                RecyclerAdapter1?.data= electionlist
//                RecyclerAdapter1?.notifyDataSetChanged()
//            }
//        })

// Recycler 2
        //connect viewmodel adapter with the clicklistener
        RecyclerAdapter2= ElectionListAdapter(ElectionListener {
            electionViewModel.onNavigateToVoterelection(it) })

        binding.savedRecycler.adapter = RecyclerAdapter2

        //update recycler data
//        electionViewModel.savedElectionList.observe(viewLifecycleOwner, Observer{ savedlist ->
//            savedlist?.apply {
//                RecyclerAdapter2?.data= savedlist
//                RecyclerAdapter2?.notifyDataSetChanged()
//            }
//        })

        // Up comming Election
        electionViewModel.electionsList.observe(viewLifecycleOwner, Observer {
            RecyclerAdapter1!!.submitList(it)
        })
//        Saved Election
        electionViewModel.savedElectionList.observe(viewLifecycleOwner, Observer {
            RecyclerAdapter2!!.submitList(it)
        })

        electionViewModel.navigateToVoter.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id,it.division))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                electionViewModel.onNavigationComplete()
            }
        })

       return binding.root
    }


}