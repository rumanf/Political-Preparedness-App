package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepresentativeViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}


class RepresentativeViewModel(application: Application): AndroidViewModel(application) {
    //Establish live data for representatives and address
    //need to get access too API
    //need to get address using the button
    //need to convert address to text
    //need to use address and do api call and retrieve reps
    //need to connect to display items
    //need url item for each
    //need title
    //need name
    //need party affil


    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    //Create function to fetch representatives from API from a provided address

     fun getReps() {
        viewModelScope.launch {
            try {
                _address.value?.let {
                    val (offices, officials) = CivicsApi.retrofitService.getRepsAPI(address.toString())
                        .await()
                    _representatives.value =
                        offices.flatMap { office -> office.getVoterInfoAPIs(officials) }
                } ?: run {
                    Log.e("Error", "error")
                }

            } catch (e: Exception) {
                Log.e("Error", "error")
            }

        }
    }
    // Create function get address from geo location
    fun geoLocationAddress(address: Address) {
        _address.value=address
    }

    //Create function to get address from individual fields
     fun addressFromFields(address: Address) {
        _address.value = address
    }
}

