package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //Add insert query
    @Insert
    suspend fun insert(election: Election)

    // Add select all election query
    @Query("SELECT * FROM   ELECTION_TABLE")
    fun getAllElection():LiveData<List<Election>>

    // Add select single election query
    @Query("SELECT * from ELECTION_TABLE WHERE id = :key")
    fun getElectionWithId(key: Int): LiveData<Election>

    //Add delete query
    @Delete
    fun unSaveElection(election: Election)

    // Add clear query
    @Query("DELETE FROM ELECTION_TABLE")
    suspend fun clear()




}