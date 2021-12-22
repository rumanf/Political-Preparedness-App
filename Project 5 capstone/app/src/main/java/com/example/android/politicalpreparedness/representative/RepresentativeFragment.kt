package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModelFactory
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailFragment : Fragment() {

    private var RepRecyclerAdapter: RepresentativeListAdapter?=null
    private lateinit var representativeViewModel: RepresentativeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentRepresentativeBinding

    companion object {
        const val REQUEST_LOCATION_PERMISSION=1
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //Establish bindings
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application

        val viewModelFactory = RepresentativeViewModelFactory(application )
         representativeViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(RepresentativeViewModel::class.java)

        binding.repviewmodel=representativeViewModel

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        //location click listener
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
            if (checkLocationPermissions()) {
                getLocation()}

            if (representativeViewModel.address.value != null) {

            }
        }

        //click listener on search button. sent toast to fill data if its not full
        binding.searchButton.setOnClickListener {
            hideKeyboard()
            representativeViewModel.addressFromFields(Address(binding.addressLine1.text.toString(),binding.addressLine2.text.toString(),binding.city.text.toString(),binding.zip.text.toString(),binding.state.selectedItem.toString()))

            if (representativeViewModel.address.value != null) {
                representativeViewModel.getReps()
            } else {
                Toast.makeText(context, R.string.address_error, Toast.LENGTH_SHORT).show()
            }
        }


        RepRecyclerAdapter=RepresentativeListAdapter()
        binding.repRecycler.adapter = RepRecyclerAdapter

        representativeViewModel.representatives.observe(viewLifecycleOwner, Observer{
            RepRecyclerAdapter!!.submitList(it)
        })


    return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.location_error),
                    Snackbar.LENGTH_SHORT
                ).setAction(android.R.string.ok) {
                    Log.i("error", " location permission required")
                }.show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isPermissionGranted()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val address = geoCodeLocation(location)
                    representativeViewModel.geoLocationAddress(address)
                }
            }

        }
    }


//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        //Handle location permission result to get location on permission granted
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (isPermissionGranted()) {
//                getLocation()
//                representativeViewModel.getReps()
//            } else {
//                Toast.makeText(context, R.string.location_error, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    private fun checkLocationPermissions(): Boolean {
//        return if (isPermissionGranted()) {
//            true
//        } else {
//            //Request Location permissions
//            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
//            false
//        }
//    }
//
//    private fun isPermissionGranted() : Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLocation() {
//    if (checkLocationPermissions()){
//        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location: Location ->
//        val address=geoCodeLocation(location)
//            representativeViewModel.geoLocationAddress(address)
//        }}else{}
//
//
//    }



    private fun geoCodeLocation(location: Location):Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}