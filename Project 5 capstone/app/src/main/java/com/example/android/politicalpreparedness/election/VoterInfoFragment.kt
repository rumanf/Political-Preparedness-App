package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoFragment : Fragment() {


        private lateinit var electionInfo: Election

        override fun onCreateView(inflater: LayoutInflater,
                                  container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

                val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
                binding.lifecycleOwner = this


                val application = requireNotNull(this.activity).application
                val dataSource = ElectionDatabase.getInstance(application).electionDao
                //electionInfo = arguments.let { VoterInfoFragmentArgs.fromBundle(it).argElection}
                var electionID = arguments?.let { VoterInfoFragmentArgs.fromBundle(it).argElectionId }
                var division = arguments?.let { VoterInfoFragmentArgs.fromBundle(it).argDivision }

                // Create an instance of the ViewModel Factory.
                val viewModelFactory = VoterInfoViewModelFactory(dataSource,electionID!!,division!!)



                // Get a reference to the ViewModel associated with this fragment.
                val voterinfoViewModel =
                        ViewModelProvider(
                                this, viewModelFactory).get(VoterInfoViewModel::class.java)


                binding.voterinfoviewmodel = voterinfoViewModel

                // set up click listeners
                binding.saveElectionButton.setOnClickListener{
                        voterinfoViewModel.saveUnsaveButton()
                }

                voterinfoViewModel.votingLocationsClicked.observe(viewLifecycleOwner, Observer {
                        if (it == true) {
                                loadUrl(voterinfoViewModel.state.value?.electionAdministrationBody?.votingLocationFinderUrl)
                                voterinfoViewModel.changeClickStateBack()
                        }})

                voterinfoViewModel.ballotInfoClicked.observe(viewLifecycleOwner, Observer {
                        if (it == true) {
                                loadUrl(voterinfoViewModel.state.value?.electionAdministrationBody?.ballotInfoUrl)
                                voterinfoViewModel.changeClickStateBack()
                        }})

                return binding.root}

        fun loadUrl(url:String?){
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireContext().startActivity(intent)}

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */
}
