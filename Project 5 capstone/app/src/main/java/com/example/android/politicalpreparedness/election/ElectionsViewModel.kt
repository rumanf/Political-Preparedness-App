package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.API_Status
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Construct ViewModel and provide election datasource
class ElectionsViewModel(val database: ElectionDao,
                         application: Application) : AndroidViewModel(application) {



    // Create live data val for upcoming elections

    private val _status = MutableLiveData<API_Status>()
    val status: LiveData<API_Status>
        get() = _status

    private val _navigateToVoter = MutableLiveData<Election>()
    val navigateToVoter : LiveData<Election>
        get() =_navigateToVoter

    private val _selectedElection = MutableLiveData<Election>()
     val selectedElection: LiveData<Election>
        get() = _selectedElection
    private val _electionsList = MutableLiveData<List<Election>>()
     val electionsList: LiveData<List<Election>>
        get() = _electionsList

    //Create live data val for saved elections
    val savedElectionList: LiveData<List<Election>> =database.getAllElection()

    //Create val and functions to populate live data for upcoming elections from the API and saved elections from local database



    init {
        //Load Data From API
        viewModelScope.launch {
            _status.value = API_Status.LOADING
            electionAPI()
        }
    }

    suspend fun electionAPI() {
        withContext(Dispatchers.IO) {
            try {
                var listResult = CivicsApi.retrofitService.getElectionAPI().await()
                _electionsList.postValue(listResult.elections)
                _status.postValue(API_Status.DONE)

            } catch (e: Exception) {
                _status.postValue(API_Status.ERROR)
            }
        }
    }

    // Create functions to navigate to saved or upcoming election voter info
    fun onNavigateToVoterelection (electionitem: Election) {
        _navigateToVoter.value = electionitem
    }

    fun onNavigationComplete() {
        _navigateToVoter.value = null
    }
}
