package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionForRecyclerBinding
//import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election
import com.squareup.moshi.JsonClass


//connects viewholder to the layout
//class ElectionViewHolder(val viewDataBinding: ElectionForRecyclerBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
//    companion object {
//        @LayoutRes
//        val LAYOUT = R.layout.election_for_recycler
//    }
//}

class ElectionDiffCallback: DiffUtil.ItemCallback<Election>(){
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}
@JsonClass(generateAdapter = true)
class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ElectionViewHolder(private val binding: ElectionForRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election, electionListener: ElectionListener) {
            binding.election = election
            binding.electionclicked = electionListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ElectionForRecyclerBinding.inflate(layoutInflater, parent, false)
                return ElectionViewHolder(binding)
            }
        }

    }













//class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {
//
//    //this is the data variable for list
//    var data: List<Election> = emptyList()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//    //gets the size of the list as required by the recyclerview
//    override fun getItemCount() =data.size
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
//        return ElectionViewHolder.from(parent)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
//        val withDataBinding: ElectionForRecyclerBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(parent.context),
//            ElectionViewHolder.LAYOUT,
//            parent,
//            false)
//        return ElectionViewHolder(withDataBinding)}
//
//
//    //Bind ViewHolder
//    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
//        holder.viewDataBinding.also {
//            it.election = data[position]
//            it.electionclicked=clickListener
//
//
//        }

//Create ElectionListener
//    class ElectionListener(val block: (Election) -> Unit) {
//
//        fun onClick(election: Election) = block(election)
//    }
}
class ElectionListener(val electionListener:(election_id:Election)->Unit){
    fun onClick(election: Election) {electionListener(election)}

}

