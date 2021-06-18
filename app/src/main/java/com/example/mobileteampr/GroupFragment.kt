package com.example.mobileteampr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class GroupFragment : Fragment() {
    var data : ArrayList<Group> = ArrayList() // 사용?
    val myViewModel:MyViewModel by activityViewModels()
    lateinit var adapter : MyGroupAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var rdb: DatabaseReference
    lateinit var curId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group, container, false)
        init(view)
        return view
    }

    private fun init(view:View){
        curId = myViewModel.getLiveData().toString()
        val addBtn = view.findViewById<ImageView>(R.id.addBtn)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        rdb = FirebaseDatabase.getInstance().getReference("All/private/$curId")  //  해당사용자의 leaf 안으로

        // 데이터 가져올 쿼리
        // 최근 50개 데이터만 가져옴
        val query = rdb.limitToLast(50)

        val option = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(query, Group::class.java)
            .build()

        adapter = MyGroupAdapter(option)
        adapter.itemClickListener = object : MyGroupAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                // itemView 클릭때마다 ?

            }
        }


        recyclerView.adapter = adapter

        addBtn.setOnClickListener {
            // 그룹 생성 다이얼로그 그룹이름, 유저리리스트
            GroupDialogFragment().show(activity?.supportFragmentManager!!, "GroupDialog")
        }

    }

    private fun createGroup(){

    }

}