package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.API_Status
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(private val dataSource: ElectionDao,private val electionID: Int,val division: Division) : ViewModel() {

    //get data from network
    //gt database, save election as needed
    //check saved state of the election

    //update live data variables
    //connect livedata variables to the xml item

    //Add live data to hold voter info

    private val _status = MutableLiveData<API_Status>()
    val status: LiveData<API_Status> get() = _status

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse:LiveData<VoterInfoResponse> get() = _voterInfoResponse

    // need date variable, and election name variable

    private val _corrAddress = MutableLiveData<String>()
    val corrAddress:LiveData<String> get() = _corrAddress

    val electionname= MutableLiveData<String>()

    private val _state = MutableLiveData<State>()
    val state:LiveData<State> get() = _state

    private val _votingLocationsClicked = MutableLiveData<Boolean>(false)
    val votingLocationsClicked  :LiveData<Boolean> get() = _votingLocationsClicked

    private val _ballotInfoClicked = MutableLiveData<Boolean>(false)
    val ballotInfoClicked :LiveData<Boolean> get() = _ballotInfoClicked



    val savedState:LiveData<Boolean> = Transformations.map(dataSource.getElectionWithId(electionID)) {
        if (it==null){false } else{true}
    }

    init {
        //Load Elections Api
        viewModelScope.launch {
            _status.value= API_Status.LOADING
            VoterInfoAPI()
            electionname.value="VIP TEST ELECTION"
        }
    }

    //getting data from API for specific election
    fun  VoterInfoAPI() {
       // val address="${division.state},${division.country}"

        val address="CA,US"

        viewModelScope.launch{
            try {
                var listResult = CivicsApi.retrofitService.getVoterInfoAPI( address,electionID,false).await()
                _voterInfoResponse.postValue(listResult)
                electionname.value=voterInfoResponse.value?.election?.name
                _state.postValue(listResult.state?.get(0))
                _status.postValue(API_Status.DONE)
                updateaddress()
            } catch (e: Exception) {
                _status.postValue(API_Status.ERROR)
            }
        }
    }

   private fun updateaddress(){
        val tempcorraddress="${_state.value?.electionAdministrationBody?.correspondenceAddress?.line1?:""} " +
                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.line2?:""}\n" +
                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.city?:""}\n" +
                "${_state.value?.electionAdministrationBody?.correspondenceAddress?.state?:""}"
        _corrAddress.value=tempcorraddress
    }


    fun saveElection(election: Election){
         viewModelScope.launch {
             try {
                 dataSource.insert(election)
             } catch (e: Exception){
                 Log.e("error","Sorry something went wrong! Please try again later!")
             }

    }}
     fun unSaveElection(election: Election){
         viewModelScope.launch {
             try {
                 dataSource.unSaveElection(election)
             } catch (e: Exception){
                 Log.e("error","Sorry something went wrong! Please try again later!")
             }
    }}
    var buttontext:String="Save Election"

    private fun update_button_text() {
        if (savedState.value==true) {
            buttontext = "Unsave Election"
        }
    }


    fun onVotingLocationsClicked(){
        _votingLocationsClicked.value = true }

    fun onBallotInfoClicked(){
        _ballotInfoClicked.value = true}

    fun changeClickStateBack(){
        if (_votingLocationsClicked.value ==true)
        {_votingLocationsClicked.value = false }
        if (ballotInfoClicked.value  ==true)
        {_ballotInfoClicked.value  = false }
    }

    // this is executed when button will be pressed, response depends on current state
   fun saveUnsaveButton(){

            if (savedState.value ==true) {
                    unSaveElection(voterInfoResponse.value!!.election)
                    //unSaveElection(election)}
            }else{
                    Log.i("error","${voterInfoResponse.value!!.election.name}")
                    saveElection(voterInfoResponse.value!!.election)

                update_button_text()
            }
        }
    }



    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

